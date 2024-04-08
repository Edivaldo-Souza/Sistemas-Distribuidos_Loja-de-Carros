package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Credenciais;
import model.Veiculo;

public interface ReverseProxy extends Remote{
	int autenticar(Credenciais c) throws RemoteException;
	Veiculo adicionar(Veiculo v,int port)  throws RemoteException;
	List<Veiculo> buscar(String renavam,int port) throws RemoteException;
	List<Veiculo> listar(String categoria,int port) throws RemoteException;
	Veiculo atualizar(String renavam, Veiculo v,int port) throws RemoteException;
	boolean deletar(String v,int port) throws RemoteException;
	boolean comprar(String v,int port) throws RemoteException;
	int getQuantidade(int port) throws RemoteException;
	int getIndividualPort() throws RemoteException;
	int getServicePort() throws RemoteException;
	int getClientPort() throws RemoteException;
	void setIndividualPort(int port) throws RemoteException;
	void setServicePort(int port) throws RemoteException;
	void setClientPort(int port ) throws RemoteException;
}
