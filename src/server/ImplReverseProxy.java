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
	
	public int individualsPorts = 3;
	private static String endLine = "0ersf1";
	
	private boolean verificarRequisicao(String r) {
		System.out.println(r);
		if(!r.split(":")[1].split("/")[1].equals("ServicoLojaDeCarros")) {
			return false;
		}
		return true;
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
	public String adicionar(String req) throws RemoteException {
		
		try {
			String[] params = req.split(endLine);
			
			Veiculo newVeichle = new Veiculo();
			
			if(verificarRequisicao(params[6])) {
				newVeichle.toVeiculo(req);
				
				ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
						Naming.lookup(params[6]);
				
				stub.adicionar(newVeichle);
					
				return newVeichle.toString();
			}
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Requisição Inválida";
	}

	@Override
	public List<String> buscar(String req) throws RemoteException {
		try {
			String[] params = req.split(endLine);
			if(verificarRequisicao(params[1])) {
				ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
						Naming.lookup(params[1]);
				
				List<Veiculo> lista = stub.buscar(params[0]);
				List<String> listaStrs = new ArrayList<String>();
				for(Veiculo v : lista) {
					listaStrs.add(v.toString());
				}
				
				return listaStrs; 
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<String> listar(String req) throws RemoteException {
		String[] params = req.split(endLine);
		
		try {
			if(verificarRequisicao(params[1])) {
				ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
						Naming.lookup(params[1]);
				
				List<Veiculo> lista = stub.listar(params[0]);
				List<String> listaStrs = new ArrayList<String>();
				for(Veiculo v : lista) {
					listaStrs.add(v.toString());
				}
				
				return listaStrs; 
			}
			
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String atualizar(String req) throws RemoteException {
		
		String[] params = req.split(endLine);
		Veiculo newVeichle = new Veiculo();
		try {
			newVeichle.toVeiculo(req);
			if(verificarRequisicao(params[6])) {
				ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
						Naming.lookup(params[6]);
				
				return stub.atualizar(newVeichle.getRenavam(), newVeichle).toString();
			}

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String deletar(String req) throws RemoteException {
		String[] params = req.split(endLine);
		String result;
		System.out.println(req);
		try {
			if(verificarRequisicao(params[1])) {
				ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
						Naming.lookup(params[1]);
				
				if(stub.deletar(params[0])) {
					result = "Veiculo deletado";
				}
				else result = "Parametro errado";
				return result;
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = "Requisicao Invalida";
		return result;
	}

	@Override
	public String comprar(String req) throws RemoteException {
		String[] params = req.split(endLine);
		String result;
		try {
			if(verificarRequisicao(params[1])) {
				ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
						Naming.lookup(params[1]);
				stub.comprar(params[0]);
				result = "Compra realizada";
				
				return result;
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = "Requisicao Invalida";
		return result;
	}

	@Override
	public int getQuantidade(String req) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup(req);
			
			return stub.getQuantidade();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		
		//System.setProperty("java.rmi.server.hostname","10.0.0.207");
		//System.setProperty("java.security.policy","java.policy");
		
		try {
			ImplReverseProxy refObjRemoto = new ImplReverseProxy();
			ImplAutenticador refObjRemotoAuth = new ImplAutenticador();
			ImplServicoLojaDeCarros refObjRemotoLoja = new ImplServicoLojaDeCarros();
			ReverseProxy skeleton = (ReverseProxy) UnicastRemoteObject.exportObject(refObjRemoto, 0);
			Autenticador skeletonAuth = (Autenticador) UnicastRemoteObject.exportObject(refObjRemotoAuth,1);
			ServicoLojaDeCarros skeletonLoja = (ServicoLojaDeCarros) UnicastRemoteObject.exportObject(refObjRemotoLoja, 2);
			
			LocateRegistry.createRegistry(2000);
			Registry reg = LocateRegistry.getRegistry(2000);
			reg.bind("ReverseProxy", skeleton);
			reg.bind("Autenticador", skeletonAuth);
			reg.bind("ServicoLojaDeCarros", skeletonLoja);
			
			
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
