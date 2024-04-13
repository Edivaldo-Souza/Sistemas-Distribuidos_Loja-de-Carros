package interfaces;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import cripto.Chave;
import cripto.DadoCifrado;
import model.Credenciais;

public interface Autenticador extends Remote {
	int autenticar(Credenciais c) throws RemoteException;
	Chave trocaDeChavesRsa(Chave publicKey) throws  RemoteException;
	byte[] requisitarChaveAes() throws RemoteException, IOException;
	byte[] requisitarChaveHmac() throws IOException;
}
