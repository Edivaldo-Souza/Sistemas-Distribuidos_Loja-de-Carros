package server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import cripto.*;
import interfaces.Autenticador;
import model.Credenciais;
import model.Mensagem;
import model.NaoAutenticoException;
import model.TipoDeUsuario;

public class ImplAutenticador implements Autenticador{
	private Cripto cripto;
	private ArrayList<Credenciais> contas;
	public ImplAutenticador() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
		this.cripto = new Cripto("okay54232ikakjll");
		this.contas = new ArrayList<>();
		Credenciais c1 = new Credenciais(TipoDeUsuario.CLIENTE,"Joao","12345");
		Credenciais c2 = new Credenciais(TipoDeUsuario.CLIENTE,"Maria","54321");
		Credenciais c3 = new Credenciais(TipoDeUsuario.FUNCIONARIO,"Pablo","67890");
		c1.setSenha(HashWithSalt.getHashSenhaSegura(c1));
		c2.setSenha(HashWithSalt.getHashSenhaSegura(c2));
		c3.setSenha(HashWithSalt.getHashSenhaSegura(c3));
		contas.add(c1);
		contas.add(c2);
		contas.add(c3);
	}
	
	@Override
	public byte[] autenticar(byte[] dadosC) throws Exception {
		Credenciais c = (Credenciais) handleRequest(dadosC,cripto);
		for(Credenciais cred : this.contas) {
			c.setSalt(cred.getSalt());
			Credenciais temp = new Credenciais();
			temp.setNome(c.getNome());
            temp.setSenha(HashWithSalt.getHashSenhaSegura(c));
			boolean isEqual = cred.compareTo(temp);
			if(isEqual) {
				if(cred.getTipo()==TipoDeUsuario.FUNCIONARIO) {
					return montarRequest(1,cripto);
				}
				return montarRequest(0,cripto);
			}
		}
		return montarRequest(2,cripto);
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
	public static void autenticar(Mensagem msg, Cripto cripto) throws Exception {
		DadoCifrado hmacAssinado = msg.gethMacAssinado();
		String hmac = cripto.verificarAssinatura(hmacAssinado);
		if(!hmac.equals(cripto.hMac(msg.getMensagem()))) {
			throw new NaoAutenticoException("Mensagem não autenticada!");
		}
	}
	public static Object handleRequest(byte[] reply, Cripto cripto) throws Exception {
		Mensagem msgDecifrada = cripto.descriptografar(reply);
		autenticar(msgDecifrada, cripto);
		return msgDecifrada.getMensagem();
	}
	public static byte[] montarRequest(Object v, Cripto cripto) throws Exception {
		return cripto.criptografar(new Mensagem(v,cripto.assinarHash(cripto.hMac(v))));
	}
}

