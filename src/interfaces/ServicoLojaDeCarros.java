package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Veiculo;

public interface ServicoLojaDeCarros extends Remote{
	Veiculo adicionar(Veiculo v)  throws RemoteException;
	List<Veiculo> buscar(String renavam) throws RemoteException;
	List<Veiculo> listar(String categoria) throws RemoteException;
	Veiculo atualizar(String renavam, Veiculo v) throws RemoteException;
	boolean deletar(String v) throws RemoteException;
	boolean comprar(String v) throws RemoteException;
	int getQuantidade() throws RemoteException;
}
