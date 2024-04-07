package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
	
	public static void main(String[] args) {
		String host = "127.0.0.1";
		boolean keepRunning = true;
		boolean keepLogged = true;
		int toMainMenu, cont;
		Credenciais currentUser;
		List<Veiculo> resultado;
		
		try {
			Registry registro = LocateRegistry.getRegistry(host, 2000);
			ReverseProxy stub = (ReverseProxy) registro.lookup("ReverseProxy");
			
			while(keepRunning) {
				keepLogged = true;
				currentUser = menuInicial();
				if(currentUser==null) {
					break;
				}
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
							reply = stub.adicionar(adicionarVeiculo()).toString();
							System.out.println(reply);
							break;
						case 2:
							temp = buscarVeiculo();
							cont = 0;
							for(Veiculo c: stub.buscar(temp)) {
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
								for(Veiculo v : stub.listar(temp)) {
									System.out.println(v.toString());
								}
							}
							break;
						case 4:

							System.out.println("Digite o renavam do veiculo: ");
							temp = s.nextLine();

							Veiculo v = alterarVeiculo();
							Veiculo novoVec = stub.atualizar(temp, v);
							if(novoVec!=null) {
								System.out.println(novoVec.toString());
							}
							else System.out.println("Nenhuma correspondencia");
							break;

						case 5:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							resultado = stub.buscar(temp);
							for(Veiculo c: resultado) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c.toString());
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								s.nextLine();
								if(stub.deletar(temp)) {
									System.out.println("Removido com sucesso");
								}
								else System.out.println("Veiculo nao disponivel");
							}
							break;

						case 6:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							resultado = stub.buscar(temp);
							for(Veiculo c: resultado) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c.toString());
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								s.nextLine();
								if(stub.comprar(temp)) {
									System.out.println("Compra realizada!");
								}
								else System.out.println("Veiculo nao encontrado");
							}
							break;

						case 7:
							System.out.println("Total de veiculos: "+stub.getQuantidade());
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
							for(Veiculo c: stub.buscar(temp)) {
								cont++;
								System.out.println(c.toString());
							}
							if(cont==0) {
								System.out.println("Nenhum correspondencia");
							}
							break;
						case 2:
							temp = listarVeiculos();
							if(temp!=null) {
								for(Veiculo v : stub.listar(temp)) {
									System.out.println(v.toString());
								}
							}
							break;

						case 3:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							resultado = stub.buscar(temp);
							for(Veiculo c: resultado) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c.toString());
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								s.nextLine();
								if(stub.comprar(temp)) {
									System.out.println("Compra realizada!");
								}
								else System.out.println("Veiculo nao encontrado");
							}
							break;

						case 4:
							System.out.println("Total de veiculos: "+stub.getQuantidade());
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
	
	private static Veiculo adicionarVeiculo() {
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
		vec.setPreco(s.nextDouble());
		s.nextLine();
		
		vec.setDisponivel(true);
		
		return vec;
	}
	
	public static String buscarVeiculo() {
		String option;
		System.out.println("Digite o renavam ou nome do veiculo: ");
		option = s.nextLine();
		
		return option;
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
		int opt;
		
		try {
			opt = Integer.parseInt(option);
		}catch(Exception e) {
			opt = 4;
		}
		
		switch(opt) {
		case 1: return "ECONOMICO";
		case 2: return "INTERMEDIARIO";
		case 3: return "EXECUTIVO";
		case 4: return "NAODEFINIDO";
		default: return null;
		}

	}
	
	public static Veiculo alterarVeiculo() {
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
		vec.setPreco(s.nextDouble());
		s.nextLine();
		
		return vec;
	}

}
