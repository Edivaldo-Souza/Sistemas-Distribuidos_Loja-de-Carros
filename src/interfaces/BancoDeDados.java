package interfaces;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cripto.Chave;
import model.Veiculo;

public interface BancoDeDados extends Remote{
	byte[] add(byte[] dadosV) throws Exception;
	byte[] update(byte[] dadosV, byte[] dadosIndex) throws Exception;
	void delete(byte[] dadosIndex) throws Exception;
	byte[] get() throws Exception;
	void setBD(List<Veiculo> lista) throws RemoteException;
	Chave trocaDeChavesRsa(Chave publicKey) throws  RemoteException;
	byte[] requisitarChaveAes() throws IOException;
	byte[] requisitarChaveHmac() throws IOException;
}
