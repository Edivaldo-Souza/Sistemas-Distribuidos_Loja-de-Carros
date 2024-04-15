package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Credenciais;
import model.Veiculo;

public interface ReverseProxy extends Remote{
	int autenticar(Credenciais c) throws RemoteException;
	String adicionar(String req)  throws RemoteException;
	List<String> buscar(String req) throws RemoteException;
	List<String> listar(String req) throws RemoteException;
	String atualizar(String req) throws RemoteException;
	String deletar(String req) throws RemoteException;
	String comprar(String req) throws RemoteException;
	int getQuantidade(String req) throws RemoteException;
	int getIndividualPort() throws RemoteException;
	int getServicePort() throws RemoteException;
	int getClientPort() throws RemoteException;
	void setIndividualPort(int port) throws RemoteException;
	void setServicePort(int port) throws RemoteException;
	void setClientPort(int port ) throws RemoteException;
}
