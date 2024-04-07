package server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import interfaces.BancoDeDados;
import model.Veiculo;

public class ImplBancoDeDados implements BancoDeDados{
	private static List<Veiculo> database = new ArrayList<Veiculo>();

	@Override
	public Veiculo add(Veiculo v) throws RemoteException {
		if(database.add(v)) {
			return database.get(database.size()-1);
		}
		return null;
	}

	@Override
	public Veiculo update(Veiculo v, int index) throws RemoteException {
		database.set(index, v);
		return database.get(index);
	}

	@Override
	public void delete(int index) throws RemoteException {
		database.remove(index);
		
	}

	@Override
	public List<Veiculo> get() throws RemoteException {
		
		return database;
	}

	@Override
	public void setBD(List<Veiculo> lista) throws RemoteException {
		ImplBancoDeDados.database = lista;
	}
}
