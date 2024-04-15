package interfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cripto.Chave;
import model.Credenciais;
import model.Veiculo;

public interface ReverseProxy extends Remote{
	byte[] autenticar(byte[] c) throws RemoteException;
	byte[] adicionar(byte[] v, int port)  throws RemoteException;
	byte[] buscar(byte[] dados, int port) throws RemoteException;
	byte[] listar(byte[] dados, int port) throws RemoteException;
	byte[] atualizar(byte[] renavam, byte[] v,int port) throws RemoteException;
	byte[] deletar(byte[] v,int port) throws RemoteException;
	byte[] comprar(byte[] v,int port) throws RemoteException;
	byte[] getQuantidade(int port) throws RemoteException;
	int getIndividualPort() throws RemoteException;
	int getServicePort() throws RemoteException;
	int getClientPort() throws RemoteException;
	void setIndividualPort(int port) throws RemoteException;
	void setServicePort(int port) throws RemoteException;
	void setClientPort(int port ) throws RemoteException;
	Chave trocaDeChavesRsaAuth(Chave publicKey) throws RemoteException, MalformedURLException, NotBoundException;
	Chave trocaDeChavesRsaLoja(Chave publicKey, int port) throws RemoteException, MalformedURLException, NotBoundException;
	byte[] requisitarChaveAesAuth() throws IOException, NotBoundException;
	byte[] requisitarChaveAesLoja(int port) throws IOException, NotBoundException;
	byte[] requisitarChaveHmacAuth() throws IOException, NotBoundException;
	byte[] requisitarChaveHmacLoja(int port) throws IOException, NotBoundException;
}
