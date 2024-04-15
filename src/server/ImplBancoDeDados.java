package server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import cripto.Base64;
import cripto.Chave;
import cripto.Cripto;
import cripto.DadoCifrado;
import interfaces.BancoDeDados;
import model.Mensagem;
import model.NaoAutenticoException;
import model.Veiculo;

public class ImplBancoDeDados implements BancoDeDados{
	private static List<Veiculo> database = new ArrayList<Veiculo>();
	private Cripto cripto;

	public ImplBancoDeDados() throws UnsupportedEncodingException {
		Chave chavePublica = new Chave();
		chavePublica.modulo = new BigInteger("14857"); chavePublica.valorDaChave = new BigInteger("3");
		Chave chavePrivada = new Chave();
		chavePrivada.modulo = new BigInteger("14857"); chavePrivada.valorDaChave = new BigInteger("9731");
		this.cripto = new Cripto("mudjanoplijsd12k", chavePublica, chavePrivada);
		this.cripto.chaveHmac = "kalsdcbasjd123oa";
	}
	@Override
	public byte[] add(byte[] dadosV) throws Exception {
		Veiculo v = (Veiculo) handleRequest(dadosV, cripto);
		if(database.add(v)) {
			Veiculo retorno = database.get(database.size()-1);
			return cripto.criptografar(new Mensagem(retorno,cripto.assinarHash(cripto.hMac(retorno))));
		}
		return null;
	}

	@Override
	public byte[] update(byte[] dadosV, byte[] dadosIndex) throws Exception {
		Veiculo v = (Veiculo) handleRequest(dadosV, cripto);
		int index = (int) handleRequest(dadosIndex, cripto);
		database.set(index, v);
		Veiculo retorno = database.get(index);
		return cripto.criptografar(new Mensagem(retorno, cripto.assinarHash(cripto.hMac(retorno))));
	}

	@Override
	public void delete(byte[] dadosIndex) throws Exception {
		int index = (int) handleRequest(dadosIndex,cripto);
		database.remove(index);
	}

	@Override
	public byte[] get() throws Exception {
		return cripto.criptografar(new Mensagem(database, cripto.assinarHash(cripto.hMac(database))));
	}

	@Override
	public void setBD(List<Veiculo> lista) throws RemoteException {
		ImplBancoDeDados.database = lista;
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
	@Override
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
}
