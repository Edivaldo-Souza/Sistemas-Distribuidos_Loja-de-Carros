package server;

import java.io.IOException;
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

import cripto.Base64;
import cripto.Chave;
import cripto.Cripto;
import cripto.DadoCifrado;
import interfaces.BancoDeDados;
import interfaces.ReverseProxy;
import interfaces.ServicoLojaDeCarros;
import model.Categorias;
import model.Mensagem;
import model.NaoAutenticoException;
import model.Veiculo;

public class ImplServicoLojaDeCarros implements ServicoLojaDeCarros{
	private static int usedPort;
	private Cripto cripto;

	public ImplServicoLojaDeCarros(){
		this.cripto = new Cripto("kalo54232bcaa111");
		System.out.println(cripto.rsa.getPublicKey().valorDaChave + " : " + cripto.rsa.getPublicKey().modulo);
	}

	@Override
	public byte[] adicionar(byte[] dados) throws Exception {
		BancoDeDados stub;
		Mensagem msgDescriptografada = cripto.descriptografar(dados);
		autenticar(msgDescriptografada, cripto);
		Veiculo v = (Veiculo) msgDescriptografada.getMensagem();
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
			return cripto.criptografar(new Mensagem(nv, cripto.assinarHash(cripto.hMac(nv))));
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	@Override
	public byte[] buscar(byte[] dados) throws Exception {
		BancoDeDados stub;
		System.out.println(cripto.aes.chave);
		Mensagem msgDescriptografada = cripto.descriptografar(dados);
		autenticar(msgDescriptografada, cripto);
		String renavam = (String) msgDescriptografada.getMensagem();
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = stub.get();
			List<Veiculo> resultado = new ArrayList<Veiculo>();
			for(Veiculo c : database) {
				if(c.getRenavam().equals(renavam) || c.getNome().equals(renavam)) {
					resultado.add(c);
				}
			}
			return cripto.criptografar(new Mensagem(resultado,cripto.assinarHash(cripto.hMac(resultado))));
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	@Override
	public byte[] listar(byte[] dados) throws Exception {
		List<Veiculo> resultado = new ArrayList<Veiculo>();
		Mensagem msgDescriptografada = cripto.descriptografar(dados);
		autenticar(msgDescriptografada,cripto);
		String categoria = (String) msgDescriptografada.getMensagem();
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
			return cripto.criptografar(new Mensagem(resultado, cripto.assinarHash(cripto.hMac(resultado))));
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
		System.out.println("É p;ra printar algo");
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

	@Override
	public Chave trocaDeChavesRsa(Chave publicKey) throws RemoteException {
		System.out.println("Esse método sequer tá sendo executado?");
		cripto.rsa.setPublicKeyExterna(publicKey); // recebe a public key do cliente e retorna a public key do serviço
		System.out.println(cripto.rsa.getPublicKey().valorDaChave + " : modulo " +
				cripto.rsa.getPublicKey().modulo);
		System.out.println(cripto.rsa.getPublicKeyExterna().valorDaChave + " : modulo " +
				cripto.rsa.getPublicKeyExterna().modulo);
		return cripto.rsa.getPublicKey();
	}

	@Override
	public byte[] requisitarChaveAes() throws IOException{
		DadoCifrado chaveCifrada =cripto.rsa.cifrar(cripto.aes.chave.getEncoded(),
				cripto.rsa.getPublicKeyExterna());
		return Base64.codificar(DadoCifrado.serializar(chaveCifrada));
	}

	public byte[] requisitarChaveHmac() throws IOException {
		return cripto.criptografar(new Mensagem(cripto.chaveHmac, cripto.assinarHash(cripto.chaveHmac)));
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
	public static void autenticar(Mensagem msg, Cripto cripto) throws Exception {
		DadoCifrado hmacAssinado = msg.gethMacAssinado();
		String hmac = cripto.verificarAssinatura(hmacAssinado);
		if(!hmac.equals(cripto.hMac(msg.getMensagem()))) {
			throw new NaoAutenticoException("Mensagem não autenticada!");
		}
	}


}
