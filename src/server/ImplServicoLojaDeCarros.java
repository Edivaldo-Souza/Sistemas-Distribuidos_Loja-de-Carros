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
import interfaces.ReverseProxy;
import interfaces.ServicoLojaDeCarros;
import model.Categorias;
import model.Veiculo;

public class ImplServicoLojaDeCarros implements ServicoLojaDeCarros{
	private static int usedPort;
	
	@Override
	public Veiculo adicionar(Veiculo v) throws RemoteException {
		BancoDeDados stub;
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			
			BancoDeDados rep2;
			BancoDeDados rep3;
			if(usedPort==2001) {
				rep2 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
				rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
			}
			else if(usedPort==2002) {
				rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
				rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
			}
			else {
				rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
				rep3 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
			}
			
			Veiculo nv = stub.add(v);
			rep2.add(v);
			rep3.add(v);
			return nv;
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
			stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
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
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
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
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = stub.get();
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(renavam)) {
					v.setRenavam(renavam);
					v.setDisponivel(database.get(i).isDisponivel());
					
					BancoDeDados rep2;
					BancoDeDados rep3;
					if(usedPort==2001) {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
					}
					else if(usedPort==2002) {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
					}
					else {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
					}
					
					Veiculo nv = stub.update(v, i);
					rep2.update(nv, i);
					rep3.update(nv, i);
					return nv;
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
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = stub.get();
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(v)) {
					BancoDeDados rep2;
					BancoDeDados rep3;
					if(usedPort==2001) {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
					}
					else if(usedPort==2002) {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
					}
					else {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
					}
					
					stub.delete(i);
					rep2.delete(i);
					rep3.delete(i);
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
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = stub.get();
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(v) && database.get(i).isDisponivel()) {
					database.get(i).setDisponivel(false);
					
					BancoDeDados rep2;
					BancoDeDados rep3;
					if(usedPort==2001) {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
					}
					else if(usedPort==2002) {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2003/BancoDeDados");
					}
					else {
						rep2 = (BancoDeDados) Naming.lookup("//localhost:2001/BancoDeDados");
						rep3 = (BancoDeDados) Naming.lookup("//localhost:2002/BancoDeDados");
					}
					
					stub.update(database.get(i), i);
					rep2.update(database.get(i), i);
					rep3.update(database.get(i), i);
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
			stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
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
			ReverseProxy stub = (ReverseProxy) Naming.lookup("//localhost:2000/ReverseProxy");
			
			ImplServicoLojaDeCarros objRemoto = new ImplServicoLojaDeCarros();
			ImplBancoDeDados objRemotoBD = new ImplBancoDeDados();
			
			System.out.println("Porta do servico: "+stub.getServicePort());
			
			ServicoLojaDeCarros skeleton = 
					(ServicoLojaDeCarros) UnicastRemoteObject.exportObject
					(objRemoto, stub.getIndividualPort());
			stub.setIndividualPort(stub.getIndividualPort()+1);
			
			BancoDeDados skeletonBD = 
					(BancoDeDados) UnicastRemoteObject.exportObject
					(objRemotoBD, stub.getIndividualPort());
			stub.setIndividualPort(stub.getIndividualPort()+1);
			
			
			LocateRegistry.createRegistry(stub.getServicePort());
			Registry reg = LocateRegistry.getRegistry(stub.getServicePort());
			reg.bind("ServicoLojaDeCarros", skeleton);
			reg.bind("BancoDeDados",skeletonBD);
			
			usedPort = stub.getServicePort();
			stub.setServicePort(stub.getServicePort()+1);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
