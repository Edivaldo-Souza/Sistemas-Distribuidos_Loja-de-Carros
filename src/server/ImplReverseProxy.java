package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import interfaces.Autenticador;
import interfaces.ReverseProxy;
import interfaces.ServicoLojaDeCarros;
import model.Categorias;
import model.Credenciais;
import model.TipoDeUsuario;
import model.Veiculo;

public class ImplReverseProxy implements ReverseProxy{
	public int servicePort = 2001;
	public int clientPort = 2001;
	
	public int individualsPorts = 2;
	
	public ImplReverseProxy() {
		
	}
	
	@Override
	public int autenticar(Credenciais c) throws RemoteException {
		try {
			Autenticador stub = (Autenticador) Naming.lookup("//localhost:2000/Autenticador");
			return stub.autenticar(c);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Veiculo adicionar(Veiculo v,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			
			stub.adicionar(v);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return v;
	}

	@Override
	public List<Veiculo> buscar(String renavam,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			
			return stub.buscar(renavam);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<Veiculo> listar(String categoria,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.listar(categoria);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Veiculo atualizar(String renavam, Veiculo v,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.atualizar(renavam, v);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deletar(String v,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.deletar(v);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean comprar(String v,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.comprar(v);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getQuantidade(int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.getQuantidade();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		try {
			ImplReverseProxy refObjRemoto = new ImplReverseProxy();
			ImplAutenticador refObjRemotoAuth = new ImplAutenticador();
			ReverseProxy skeleton = (ReverseProxy) UnicastRemoteObject.exportObject(refObjRemoto, 0);
			Autenticador skeletonAuth = (Autenticador) UnicastRemoteObject.exportObject(refObjRemotoAuth,1);
			
			LocateRegistry.createRegistry(2000);
			Registry reg = LocateRegistry.getRegistry(2000);
			reg.bind("ReverseProxy", skeleton);
			reg.bind("Autenticador", skeletonAuth);
			
		} catch (Exception e) {
			System.err.println("Servidor: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public int getIndividualPort() throws RemoteException {
		return this.individualsPorts;
	}

	@Override
	public int getServicePort() throws RemoteException {
		
		return this.servicePort;
	}

	@Override
	public int getClientPort() throws RemoteException {
		return this.clientPort;
	}

	
	@Override
	public void setIndividualPort(int port) throws RemoteException {
		this.individualsPorts = port;
	}

	@Override
	public void setServicePort(int port) throws RemoteException {
		this.servicePort = port;
	}

	@Override
	public void setClientPort(int port) throws RemoteException {
		if(port>2003) {
			this.clientPort = 2001;
		}
		else this.clientPort = port;
		
	}
	
	
	
}
