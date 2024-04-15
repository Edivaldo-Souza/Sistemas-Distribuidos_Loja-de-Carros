package server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import cripto.Chave;
import cripto.Cripto;
import interfaces.Autenticador;
import interfaces.ReverseProxy;
import interfaces.ServicoLojaDeCarros;
import model.Credenciais;
import model.Veiculo;

public class ImplReverseProxy implements ReverseProxy{
	public int servicePort = 2001;
	public int clientPort = 2001;
	private Cripto cripto;
	public int individualsPorts = 8002;
	
	public ImplReverseProxy() {
		this.cripto = new Cripto();
	}
	
	@Override
	public byte[] autenticar(byte[] c) throws RemoteException {
		try {
			Autenticador stub = (Autenticador) Naming.lookup("//localhost:2000/Autenticador");
			return stub.autenticar(c);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public byte[] adicionar(byte[] v,int port) throws RemoteException {
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
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return v;
	}

	@Override
	public byte[] buscar(byte[] dados,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			return stub.buscar(dados);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
	}

	@Override
	public byte[] listar(byte[] dados,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			byte[] retorno = stub.listar(dados);
			return retorno;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
	}

	@Override
	public byte[] atualizar(byte[] renavam, byte[] v,int port) throws RemoteException {
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
	public byte[] deletar(byte[] v,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.deletar(v);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new byte[0];
	}

	@Override
	public byte[] comprar(byte[] v,int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.comprar(v);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new byte[0];
	}

	@Override
	public byte[] getQuantidade(int port) throws RemoteException {
		try {
			ServicoLojaDeCarros stub = (ServicoLojaDeCarros) 
					Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
			
			return stub.getQuantidade();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new byte[0];
	}
	
	
	public static void main(String[] args) {
		try {
			ImplReverseProxy refObjRemoto = new ImplReverseProxy();
			ImplAutenticador refObjRemotoAuth = new ImplAutenticador();
			ReverseProxy skeleton = (ReverseProxy) UnicastRemoteObject.exportObject(refObjRemoto, 0);
			Autenticador skeletonAuth = (Autenticador) UnicastRemoteObject.exportObject(refObjRemotoAuth,8000);
			
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

	@Override
	public Chave trocaDeChavesRsaAuth(Chave publicKey) throws RemoteException, MalformedURLException, NotBoundException {
		Autenticador stub = (Autenticador) Naming.lookup("//localhost:2000/Autenticador");
		return stub.trocaDeChavesRsa(publicKey);
	}

	@Override
	public Chave trocaDeChavesRsaLoja(Chave publicKey, int port) throws RemoteException, MalformedURLException, NotBoundException {
		ServicoLojaDeCarros stub = (ServicoLojaDeCarros)
				Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
		return stub.trocaDeChavesRsa(publicKey);
	}

	@Override
	public byte[] requisitarChaveAesAuth() throws IOException, NotBoundException {
		Autenticador stub = (Autenticador) Naming.lookup("//localhost:2000/Autenticador");
		return stub.requisitarChaveAes();
	}

	@Override
	public byte[] requisitarChaveAesLoja(int port) throws IOException, NotBoundException {
		ServicoLojaDeCarros stub = (ServicoLojaDeCarros)
				Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
		return stub.requisitarChaveAes();
	}

	@Override
	public byte[] requisitarChaveHmacAuth() throws IOException, NotBoundException {
		Autenticador stub = (Autenticador) Naming.lookup("//localhost:2000/Autenticador");
		return stub.requisitarChaveHmac();
	}

	@Override
	public byte[] requisitarChaveHmacLoja(int port) throws IOException, NotBoundException {
		ServicoLojaDeCarros stub = (ServicoLojaDeCarros)
				Naming.lookup("//localhost:"+port+"/ServicoLojaDeCarros");
		return stub.requisitarChaveHmac();
	}

}