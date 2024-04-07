package server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.BancoDeDados;
import interfaces.ServicoLojaDeCarros;
import model.Categorias;
import model.Veiculo;

public class ImplServicoLojaDeCarros implements ServicoLojaDeCarros{
	private List<Veiculo> database;
	
	@Override
	public Veiculo adicionar(Veiculo v) throws RemoteException {
		BancoDeDados stub;
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			return stub.add(v);
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	@Override
	public List<Veiculo> buscar(String renavam) throws RemoteException {
		BancoDeDados stub;
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			List<Veiculo> database = stub.get();
			List<Veiculo> resultado = new ArrayList<Veiculo>();
			for(Veiculo c : database) {
				if(c.getRenavam().equals(renavam) || c.getNome().equals(renavam)) {
					resultado.add(c);
				}
			}
			return resultado;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	@Override
	public List<Veiculo> listar(String categoria) throws RemoteException {
		List<Veiculo> resultado = new ArrayList<Veiculo>();
		try {
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			if(categoria.equals("ECONOMICO")) {
				for(Veiculo v : stub.get()) {
					if(v.getCategoria()==Categorias.ECONOMICO) resultado.add(v);
				}
			}
			else if(categoria.equals("INTERMEDIARIO")) {
				for(Veiculo v : stub.get()) {
					if(v.getCategoria()==Categorias.INTERMEDIARIO) resultado.add(v);
				}
			}
			else if(categoria.equals("EXECUTIVO")) {
				for(Veiculo v : stub.get()) {
					if(v.getCategoria()==Categorias.EXECUTIVO) resultado.add(v);
				}
			}
			else {
				for(Veiculo v : stub.get()) {
					resultado.add(v);
				}
			}
			Collections.sort(resultado);
			return resultado;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public Veiculo atualizar(String renavam, Veiculo v) throws RemoteException {
		try {
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			List<Veiculo> database = stub.get();
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(renavam)) {
					v.setRenavam(renavam);
					v.setDisponivel(database.get(i).isDisponivel());
					return stub.update(v, i);
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	@Override
	public boolean deletar(String v) throws RemoteException {
		try {
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			List<Veiculo> database = stub.get();
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(v)) {
					stub.delete(i);
					return true;
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	@Override
	public boolean comprar(String v) throws RemoteException {
		try {
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			List<Veiculo> database = stub.get();
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(v) && database.get(i).isDisponivel()) {
					database.get(i).setDisponivel(false);
					stub.update(database.get(i), i);
					return true;
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public int getQuantidade() throws RemoteException {
		
		BancoDeDados stub;
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			List<Veiculo> database = stub.get();
			return database.size();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		
		try {
			ImplServicoLojaDeCarros objRemoto = new ImplServicoLojaDeCarros();
			ImplBancoDeDados objRemotoBD = new ImplBancoDeDados();
			ServicoLojaDeCarros skeleton = (ServicoLojaDeCarros) UnicastRemoteObject.exportObject(objRemoto, 0);
			BancoDeDados skeletonBD = (BancoDeDados) UnicastRemoteObject.exportObject(objRemotoBD, 2);
			
			LocateRegistry.createRegistry(ImplReverseProxy.servicePort);
			Registry reg = LocateRegistry.getRegistry(ImplReverseProxy.servicePort);
			reg.bind("ServicoLojaDeCarros", skeleton);
			reg.bind("BancoDeDados",skeletonBD);
			
			
			//stub = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
			
			//ImplReverseProxy.servicePort++;

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
	}
}
}
