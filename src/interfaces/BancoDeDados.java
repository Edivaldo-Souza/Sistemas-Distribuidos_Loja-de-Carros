package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import model.Veiculo;

public interface BancoDeDados extends Remote{
	Veiculo add(Veiculo v) throws RemoteException;
	Veiculo update(Veiculo v, int index) throws RemoteException;
	void delete(int index) throws RemoteException;
	List<Veiculo> get() throws RemoteException;
	void setBD(List<Veiculo> lista) throws RemoteException;
}
