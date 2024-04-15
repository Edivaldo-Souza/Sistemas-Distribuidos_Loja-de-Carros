package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import interfaces.Autenticador;
import interfaces.ReverseProxy;
import interfaces.ServicoLojaDeCarros;
import model.Categorias;
import model.Credenciais;
import model.Veiculo;

public class Cliente {
	private static Scanner s = new Scanner(System.in);
	private static String endLine = "0ersf1";
	
	public static void main(String[] args) {
		String host = "localhost";
		boolean keepRunning = true;
		boolean keepLogged = true;
		int toMainMenu, cont;
		Credenciais currentUser;
		List<String> resultado;
		int usedPort;
		String connection;
		
		
		System.setProperty("java.security.policy", "java.policy");
		try {
			Registry registro = LocateRegistry.getRegistry(host, 2000);
			ReverseProxy stub = (ReverseProxy) registro.lookup("ReverseProxy");
			usedPort = stub.getClientPort();
			stub.setClientPort(usedPort+1);
			
			connection = "//"+host+":"+usedPort+"/ServicoLojaDeCarros";
			//connection = "//"+host+":"+usedPort+"/BancoDeDados";
			
			while(keepRunning) {
				keepLogged = true;
				currentUser = menuInicial();
				if(currentUser==null) {
					break;
				}
				System.out.println("Informe o servico: ");
				s.nextLine();
				toMainMenu = stub.autenticar(currentUser);
				
				if(toMainMenu==1) {
					while(keepLogged) {
						
						String option,reply,temp;
						
						System.out.println("\nBem vindo Funcionario "+currentUser.getNome()+" !\n"
								+ "Banco de Veículos: \n"
								+ "1 - Adicionar Veiculo\n"
								+ "2 - Buscar Veiculo\n"
								+ "3 - Listar Veiculos\n"
								+ "4 - Alterar Dados de Veículo\n"
								+ "5 - Deletar Dados de Veiculo\n"
								+ "6 - Comprar Veiculo\n"
								+ "7 - Consultar quantidade de veiculos\n"
								+ "8 - Sair\n"
								+ "Digite uma opcao:");

						option = s.nextLine();
						s.nextLine();

						int num = Integer.parseInt(option);
						switch(num) {
						case 1:
							reply = stub.adicionar(adicionarVeiculo()+endLine+connection).toString();
							System.out.println(reply);
							
							break;
						case 2:
							temp = buscarVeiculo();
							cont = 0;
							for(String c: stub.buscar(temp+endLine+connection)) {
								cont++;
								System.out.println(c.toString());
							}
							if(cont==0) {
								System.out.println("Nenhum correspondencia");
							}
							break;
						case 3:
							temp = listarVeiculos();
							if(temp!=null) {
								for(String v : stub.listar(temp+endLine+connection)) {
									System.out.println(v.toString());
								}
							}
							break;
						case 4:

							System.out.println("Digite o renavam do veiculo: ");
							temp = s.nextLine();

							String v = alterarVeiculo(temp);
							String novoVec = stub.atualizar(v+endLine+connection);
							if(novoVec!=null) {
								System.out.println(novoVec);
							}
							else System.out.println("Nenhuma correspondencia");
							break;

						case 5:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							resultado = stub.buscar(temp+endLine+connection);
							for(String c: resultado) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c);
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								s.nextLine();
								System.out.println
								(stub.deletar(temp+endLine+connection)); 
									
							}
							break;

						case 6:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							resultado = stub.buscar(temp+endLine+connection);
							for(String c: resultado) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c);
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								s.nextLine();
								System.out.println
								(stub.deletar(temp+endLine+connection));
							}
							break;

						case 7:
							System.out.println("Total de veiculos: "+stub.getQuantidade(connection));
							break;
						case 8:
							keepLogged = false;
							break;
						default:
							System.out.println("Opcao Invalida! Tente denovo");
							break;
						}
					}

				}
				else if(toMainMenu==0) {
					while(keepLogged) {
						
						String option,reply,temp;
						System.out.println("\nBem vindo Cliente "+currentUser.getNome()+" !\n"
								+ "Banco de Veículos: \n"
								+ "1 - Buscar Veiculo\n"
								+ "2 - Listar Veiculos\n"
								+ "3 - Comprar Veiculo\n"
								+ "4 - Consultar quantidade de veiculos\n"
								+ "5 - Sair\n"
								+ "Digite uma opcao:");

						option = s.nextLine();
						s.nextLine();

						int num = Integer.parseInt(option);
						switch(num) {
						case 1:
							temp = buscarVeiculo();
							cont = 0;
							for(String c: stub.buscar(temp+endLine+connection)) {
								cont++;
								System.out.println(c);
							}
							if(cont==0) {
								System.out.println("Nenhum correspondencia");
							}
							break;
						case 2:
							temp = listarVeiculos();
							if(temp!=null) {
								for(String v : stub.listar(temp+endLine+connection)) {
									System.out.println(v);
								}
							}
							break;

						case 3:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							resultado = stub.buscar(temp+endLine+connection);
							for(String c: resultado) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c);
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								s.nextLine();
								System.out.println
								(stub.comprar(temp+endLine+connection));
							}
							break;

						case 4:
							System.out.println("Total de veiculos: "+stub.getQuantidade(connection));
							break;
						case 5:
							keepLogged = false;
							break;
						default:
							System.out.println("Opcao Invalida! Tente denovo");
							break;
						}
					}
				}
			}
	
		
		} catch (Exception e) {
			System.err.println("Cliente: " + e.toString());
			e.printStackTrace();
			}
		}
	
	public static Credenciais menuInicial() {
		String option;
		System.out.println("Banco de Veículos: \n"
				+ "1 - Fazer Login\n"
				+ "2 - Sair do sistema\n"
				+ "Digite uma opcao:");
		
		option = s.nextLine();
		s.nextLine();
		
		while(!option.equals("1") && !option.equals("2")) {
			System.out.println("Escolha uma opcao valida: ");
			option = s.nextLine();
			s.nextLine();
		}
		
		if(option.equals("1")) {
			Credenciais c = new Credenciais();
			System.out.println("Digite o nome do usuario: ");
			c.setNome(s.nextLine());
			s.nextLine();
			
			System.out.println("Digite a senha do usuario: ");
			c.setSenha(s.nextLine());
			s.nextLine();
			
			return c;
		}
		else return null;
		
	}
	
	
	private static String adicionarVeiculo() {
		Veiculo vec = new Veiculo();
		String categoria;
		
		System.out.println("Informe a categoria do veiculo:\n"
				+ "1 - Economico\n"
				+ "2 - Intermediario\n"
				+ "3 - Executivo\n"
				+ "Digite:");
		categoria = s.nextLine();
		s.nextLine();
		switch(Integer.parseInt(categoria)) {
		case 1: vec.setCategoria(Categorias.ECONOMICO);break;
		case 2: vec.setCategoria(Categorias.INTERMEDIARIO);break;
		case 3: vec.setCategoria(Categorias.EXECUTIVO);break;
		}
		
		System.out.println("Digite o nome do veiculo: ");
		vec.setNome(s.nextLine());
		s.nextLine();
		
		System.out.println("Digite o renavam do veiculo: ");
		vec.setRenavam(s.nextLine());
		s.nextLine();
		
		System.out.println("Digite o ano de fabricacao do veiculo: ");
		vec.setAnoFabricacao(s.nextInt());
		s.nextLine();
		
		System.out.println("Digite preco do veiculo: ");
		String preco = s.nextLine();
		String req = s.nextLine();
		String veiculoStr;
		
		if(req.isEmpty()) {
			veiculoStr = vec.getCategoria()+endLine
					+vec.getNome()+endLine
					+vec.getRenavam()+endLine
					+vec.getAnoFabricacao()+endLine
					+"D"+endLine
					+preco;
		}
		else veiculoStr = vec.getCategoria()+endLine
					+vec.getNome()+endLine
					+vec.getRenavam()+endLine
					+vec.getAnoFabricacao()+endLine
					+"D"+endLine
					+preco+endLine
					+req;
		
		
		return veiculoStr;
	}
	
	public static String buscarVeiculo() {
		String option;
		System.out.println("Digite o renavam ou nome do veiculo: ");
		option = s.nextLine();
		String con = s.nextLine();
		
		if(con.isEmpty()) {
			return option;
		}
		else return option+endLine+con;
		
	
	}
	
	public static String listarVeiculos() {
		String option;
		System.out.println("Deseja listar por alguma categoria:\n"
				+ "1 - ECONOMICO\n"
				+ "2 - INTERMEDIARIO\n"
				+ "3 - EXECUTIVO\n"
				+ "4 - Nao especificar\n"
				+ "Digite uma opcao: ");
		option = s.nextLine();
		String con = s.nextLine();
		int opt;
		
		try {
			opt = Integer.parseInt(option);
		}catch(Exception e) {
			opt = 4;
		}
		
		if(con.isEmpty()) {
			switch(opt) {
			case 1: return "ECONOMICO";
			case 2: return "INTERMEDIARIO";
			case 3: return "EXECUTIVO";
			case 4: return "NAODEFINIDO";
			default: return null;
			}
		}
		else {
			switch(opt) {
			case 1: return "ECONOMICO"+endLine+con;
			case 2: return "INTERMEDIARIO"+endLine+con;
			case 3: return "EXECUTIVO"+endLine+con;
			case 4: return "NAODEFINIDO"+endLine+con;
			default: return null;
		}
		}
	}
	
	public static String alterarVeiculo(String temp) {
		Veiculo vec = new Veiculo();
		String categoria;
		
		System.out.println("Alteracao de dados");
		System.out.println("Informe a categoria do veiculo:\n"
				+ "1 - Economico\n"
				+ "2 - Intermediario\n"
				+ "3 - Executivo\n"
				+ "Digite:");
		categoria = s.nextLine();
		s.nextLine();
		switch(Integer.parseInt(categoria)) {
		case 1: vec.setCategoria(Categorias.ECONOMICO);break;
		case 2: vec.setCategoria(Categorias.INTERMEDIARIO);break;
		case 3: vec.setCategoria(Categorias.EXECUTIVO);break;
		}
		
		System.out.println("Digite o nome do veiculo: ");
		vec.setNome(s.nextLine());
		s.nextLine();
		
		System.out.println("Digite o ano de fabricacao do veiculo: ");
		vec.setAnoFabricacao(s.nextInt());
		s.nextLine();
		
		System.out.println("Digite preco do veiculo: ");
		String preco = s.nextLine();
		String req = s.nextLine();
		String veiculoStr;
		vec.setRenavam(temp);
		
		if(req.isEmpty()) {
			veiculoStr = vec.getCategoria()+endLine
					+vec.getNome()+endLine
					+vec.getRenavam()+endLine
					+vec.getAnoFabricacao()+endLine
					+"D"+endLine
					+preco;
		}
		else veiculoStr = vec.getCategoria()+endLine
					+vec.getNome()+endLine
					+vec.getRenavam()+endLine
					+vec.getAnoFabricacao()+endLine
					+"D"+endLine
					+preco+endLine
					+req;
		
		
		return veiculoStr;
	}

}
