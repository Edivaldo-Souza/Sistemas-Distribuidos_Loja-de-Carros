package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.Credenciais;

public interface Autenticador extends Remote {
	int autenticar(Credenciais c) throws RemoteException;
}
