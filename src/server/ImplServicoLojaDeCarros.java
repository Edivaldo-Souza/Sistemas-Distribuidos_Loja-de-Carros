package server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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

import com.sun.jdi.StringReference;
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
	private Cripto criptoDatabase;


	public ImplServicoLojaDeCarros() throws UnsupportedEncodingException {
		this.cripto = new Cripto("kalo54232bcaa111");
		Chave chavePublica = new Chave();
		chavePublica.modulo = new BigInteger("8909"); chavePublica.valorDaChave = new BigInteger("7");
		Chave chavePrivada = new Chave();
		chavePrivada.modulo = new BigInteger("8909"); chavePrivada.valorDaChave = new BigInteger("1243");
		this.criptoDatabase = new Cripto("[B@29ca901ejdh1u", chavePublica, chavePrivada);
	}

	@Override
	public byte[] adicionar(byte[] dados) throws Exception {
		BancoDeDados stub;
		Veiculo v = (Veiculo) handleRequest(dados,cripto);
		try {
			if(usedPort == 0){
				stub = (BancoDeDados) Naming.lookup("//localhost:2000/BancoDeDados");
				byte[] dadosNv = stub.add(montarRequest(v,criptoDatabase));
				Veiculo nv = (Veiculo) handleRequest(dadosNv,criptoDatabase);
				System.out.println("Printa não é?");
				System.out.println(nv);
				System.out.println(cripto.aes.chave);
				System.out.println(cripto.rsa.getPublicKeyExterna().valorDaChave);
				System.out.println(cripto.rsa.getPublicKey().valorDaChave);
				System.out.println(cripto.rsa.getPrivateKey().valorDaChave);
				return montarRequest(nv,cripto);
			}
			System.out.println("PAssou do if");
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
			byte[] dadosNv = stub.add(montarRequest(v,criptoDatabase));
			Veiculo nv = (Veiculo) handleRequest(dadosNv,criptoDatabase);
			rep2.add(montarRequest(v,criptoDatabase));
			rep3.add(montarRequest(v,criptoDatabase));
			return montarRequest(nv,cripto);
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	@Override
	public byte[] buscar(byte[] dados) throws Exception {
		BancoDeDados stub;
		String renavam = (String) handleRequest(dados,cripto);
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			byte[] bytesDatabase = stub.get();
			List<Veiculo> database = (List<Veiculo>) handleRequest(bytesDatabase,criptoDatabase);
			List<Veiculo> resultado = new ArrayList<Veiculo>();
			for(Veiculo c : database) {
				if(c.getRenavam().equals(renavam) || c.getNome().equals(renavam)) {
					resultado.add(c);
				}
			}
			return montarRequest(resultado,cripto);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	@Override
	public byte[] listar(byte[] dados) throws Exception {
		List<Veiculo> resultado = new ArrayList<>();
		String categoria = (String) handleRequest(dados,cripto);
		try {
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> veiculos = (List<Veiculo>) handleRequest(stub.get(),criptoDatabase);
			if(categoria.equals("ECONOMICO")) {
				for(Veiculo v : veiculos) {
					if(v.getCategoria()==Categorias.ECONOMICO) resultado.add(v);
				}
			}
			else if(categoria.equals("INTERMEDIARIO")) {
				for(Veiculo v : veiculos) {
					if(v.getCategoria()==Categorias.INTERMEDIARIO) resultado.add(v);
				}
			}
			else if(categoria.equals("EXECUTIVO")) {
				for(Veiculo v : veiculos) {
					if(v.getCategoria()==Categorias.EXECUTIVO) resultado.add(v);
				}
			}
			else {
				for(Veiculo v : veiculos) {
					resultado.add(v);
				}
			}
			Collections.sort(resultado);
			return montarRequest(resultado,cripto);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public byte[] atualizar(byte[] bytesRenavam, byte[] bytesVeiculo) throws RemoteException {
		try {
			String renavam = (String) handleRequest(bytesRenavam,cripto);
			Veiculo v = (Veiculo) handleRequest(bytesVeiculo, cripto);
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = (List<Veiculo>) handleRequest(stub.get(), criptoDatabase);
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
					
					byte[] dadosNv = stub.update(montarRequest(v,criptoDatabase), montarRequest(i,criptoDatabase));
					Veiculo nv = (Veiculo) handleRequest(dadosNv,criptoDatabase);
					rep2.update(montarRequest(v,criptoDatabase), montarRequest(i,criptoDatabase));
					rep3.update(montarRequest(nv,criptoDatabase), montarRequest(i,criptoDatabase));
					return montarRequest(nv,cripto);
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
	}
	@Override
	public byte[] deletar(byte[] dadosV) throws Exception {
		try {
			String v = (String) handleRequest(dadosV,cripto);
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = (List<Veiculo>) handleRequest(stub.get(),criptoDatabase);
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(v)) {

					if(usedPort == 0){

					}

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
					
					stub.delete(montarRequest(i,criptoDatabase));
					rep2.delete(montarRequest(i,criptoDatabase));
					rep3.delete(montarRequest(i,criptoDatabase));
					return montarRequest(true,cripto);
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }

		return montarRequest(false,cripto);
	}
	@Override
	public byte[] comprar(byte[] dadosV) throws Exception {
		try {
			BancoDeDados stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = (List<Veiculo>) handleRequest(stub.get(), criptoDatabase);
			String v = (String) handleRequest(dadosV,cripto);
			for(int i = 0; i<database.size(); i++) {
				if(database.get(i).getRenavam().equals(v) && database.get(i).isDisponivel()) {
					database.get(i).setDisponivel(false);
					Veiculo veiculo = database.get(i);

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
					stub.update(montarRequest(veiculo,criptoDatabase), montarRequest(i,criptoDatabase));

					rep2.update(montarRequest(veiculo,criptoDatabase), montarRequest(i,criptoDatabase));
					rep3.update(montarRequest(veiculo,criptoDatabase), montarRequest(i,criptoDatabase));
					return montarRequest(true,cripto);
				}
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
        return montarRequest(false,cripto);
	}
	@Override
	public byte[] getQuantidade() throws Exception {
		BancoDeDados stub;
		try {
			stub = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			List<Veiculo> database = (List<Veiculo>) handleRequest(stub.get(),criptoDatabase);
			return montarRequest(database.size(),cripto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return montarRequest(0, cripto);
	}

	@Override
	public Chave trocaDeChavesRsa(Chave publicKey) throws RemoteException {
		cripto.rsa.setPublicKeyExterna(publicKey); // recebe a public key do cliente e retorna a public key do serviço
		return cripto.rsa.getPublicKey();
	}

	@Override
	public byte[] requisitarChaveAes() throws IOException{
		DadoCifrado chaveCifrada =cripto.rsa.cifrar(cripto.aes.chave.getEncoded(),
				cripto.rsa.getPublicKeyExterna());
		return Base64.codificar(DadoCifrado.serializar(chaveCifrada));
	}

	public byte[] requisitarChaveHmac() throws IOException {
		return cripto.criptografar(new Mensagem(cripto.chaveHmac));
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

			// trocar chaves com o serviço do banco de dados
			BancoDeDados stubBD = (BancoDeDados) Naming.lookup("//localhost:"+usedPort+"/BancoDeDados");
			// troca a chave rsa com o serviço do banco de dados
			objRemoto.criptoDatabase.rsa.setPublicKeyExterna(stubBD
					.trocaDeChavesRsa(objRemoto.criptoDatabase.rsa.getPublicKey()));
			// em seguida, recebe a chave aes do serviço
			byte[] chaveCifradaBase64 = stubBD.requisitarChaveAes();
			DadoCifrado chaveCifrada = DadoCifrado.deserializar(Base64.decodificar(chaveCifradaBase64));
			byte[] chaveDecifrada = objRemoto.criptoDatabase.rsa.decifrar(
					chaveCifrada, objRemoto.criptoDatabase.rsa.getPrivateKey());
			objRemoto.criptoDatabase.aes.reconstruirChave(chaveDecifrada);
			// E por fim, o hmac
			objRemoto.criptoDatabase.chaveHmac = (String)objRemoto.criptoDatabase.descriptografar(
					stubBD.requisitarChaveHmac()).getMensagem();

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
		} catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
	public static void autenticar(Mensagem msg, Cripto cripto) throws Exception {
		DadoCifrado hmacAssinado = msg.gethMacAssinado();
		String hmac = cripto.verificarAssinatura(hmacAssinado);
		if(!hmac.equals(cripto.hMac(msg.getMensagem()))) {
			throw new NaoAutenticoException("Mensagem não autenticada!");
		}
	}
	public static Object handleRequest(byte[] reply, Cripto cripto) throws Exception {
		Mensagem msgDecifrada = cripto.descriptografar(reply);
		autenticar(msgDecifrada,cripto);
		return msgDecifrada.getMensagem();
	}
	public static byte[] montarRequest(Object v, Cripto cripto) throws Exception {
		return cripto.criptografar(new Mensagem(v,cripto.assinarHash(cripto.hMac(v))));
	}

	@Override
	public Cripto getCripto() throws RemoteException {
		Cripto copia = new Cripto(this.cripto);
		copia.rsa.setPublicKeyExterna(this.cripto.rsa.getPublicKey());
		this.cripto.rsa.setPublicKeyExterna(copia.rsa.getPublicKey());
		return this.cripto;
	}


	public void setCripto(Cripto cripto) {
		this.cripto = cripto;
	}

	public Cripto getCriptoDatabase() {
		return criptoDatabase;
	}

	public void setCriptoDatabase(Cripto criptoDatabase) {
		this.criptoDatabase = criptoDatabase;
	}
	public void setUsedPort(int port){
		usedPort = port;
	}
}
