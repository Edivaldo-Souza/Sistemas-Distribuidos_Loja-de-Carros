package server;

import java.io.IOException;
import java.rmi.RemoteException;

import cripto.Base64;
import cripto.Chave;
import cripto.Cripto;
import cripto.DadoCifrado;
import interfaces.Autenticador;
import model.Credenciais;
import model.Mensagem;
import model.TipoDeUsuario;

public class ImplAutenticador implements Autenticador{
	private Cripto cripto;
	public ImplAutenticador(){
		this.cripto = new Cripto("okay54232ikakjll");
	}
	
	@Override
	public int autenticar(Credenciais c) throws RemoteException {
		Credenciais c1 = new Credenciais(TipoDeUsuario.CLIENTE,"Joao","12345");
		Credenciais c2 = new Credenciais(TipoDeUsuario.CLIENTE,"Maria","54321");
		Credenciais c3 = new Credenciais(TipoDeUsuario.FUNCIONARIO,"Pablo","67890");
		Credenciais[] credenciais = {c1,c2,c3};
		for(Credenciais cred : credenciais) {
			if(c.compareTo(cred)) {
				if(cred.getTipo()==TipoDeUsuario.FUNCIONARIO) {
					return 1;
				}
				return 0;
			}
		}
		
		return 2;
	}

	@Override
	public Chave trocaDeChavesRsa(Chave publicKey) throws RemoteException {
		cripto.rsa.setPublicKeyExterna(publicKey); // recebe a public key do cliente e retorna a public key do serviço
		return cripto.rsa.getPublicKey();
	}

	@Override
	public byte[] requisitarChaveAes() throws IOException{
		DadoCifrado chaveCifrada =cripto.rsa.cifrar(cripto.aes.chave.getEncoded(),
				cripto.rsa.getPublicKeyExterna());
		return Base64.codificar(DadoCifrado.serializar(chaveCifrada));
	}

	public byte[] requisitarChaveHmac() throws IOException {
		return cripto.criptografar(new Mensagem(cripto.chaveHmac, cripto.assinarHash(cripto.chaveHmac)));
	}

}

