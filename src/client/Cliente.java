package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

import cripto.DadoCifrado;
import interfaces.ReverseProxy;
import cripto.Base64;
import cripto.Chave;
import cripto.Cripto;
import model.*;

public class Cliente {
	private static Scanner s = new Scanner(System.in);
	
	public static void main(String[] args) {
		String host = "localhost";
		boolean keepRunning = true;
		boolean keepLogged = true;
		int toMainMenu, cont;
		Credenciais currentUser;
		List<Veiculo> resultado;
		Cripto criptoAuth = new Cripto();
		Cripto criptoLoja = new Cripto();
		int usedPort;
		String connection;
		int tentativas = 0;
		System.setProperty("java.security.policy", "java.policy");
		try {
			Registry registro = LocateRegistry.getRegistry(host, 2000);
			ReverseProxy stub = (ReverseProxy) registro.lookup("ReverseProxy");
			usedPort = stub.getClientPort();
			stub.setClientPort(usedPort+1);

			connection = "//"+host+":"+usedPort+"/ServicoLojaDeCarros";
			
			// antes do login, ele troca a chave rsa com o serviço de autenticacao
			criptoAuth.rsa.setPublicKeyExterna(stub.trocaDeChavesRsaAuth(criptoAuth.rsa.getPublicKey()));
			// em seguida, recebe a chave aes do serviço de autenticação
			byte[] chaveCifradaBase64 = stub.requisitarChaveAesAuth();
			DadoCifrado chaveCifrada = DadoCifrado.deserializar(Base64.decodificar(chaveCifradaBase64));
			byte[] chaveDecifrada = criptoAuth.rsa.decifrar(
					chaveCifrada, criptoAuth.rsa.getPrivateKey());
			criptoAuth.aes.reconstruirChave(chaveDecifrada);
			// E por fim, o hmac
			criptoAuth.chaveHmac = (String)criptoAuth.descriptografar(stub.requisitarChaveHmacAuth()).getMensagem();

			while(keepRunning) {
				keepLogged = true;
				currentUser = menuInicial();
				if(currentUser==null) {
					break;
				}
				byte[] authResponse = stub.autenticar(montarRequest(currentUser,criptoAuth)); // aqui eu tenho que criptografar
				toMainMenu = (int) handleResponse(authResponse, criptoAuth);


				// agora tem que trocar chaves com o serviço da loja
				criptoLoja.rsa.setPublicKeyExterna(stub.trocaDeChavesRsaLoja(criptoLoja.rsa.getPublicKey(), usedPort));

				// em seguida, recebe a chave aes do serviço de loja
				chaveCifradaBase64 = stub.requisitarChaveAesLoja(usedPort);
				chaveCifrada = DadoCifrado.deserializar(Base64.decodificar(chaveCifradaBase64));
				chaveDecifrada = criptoLoja.rsa.decifrar(
						chaveCifrada, criptoLoja.rsa.getPrivateKey());
				criptoLoja.aes.reconstruirChave(chaveDecifrada);
				// E por fim, o hmac
				criptoLoja.chaveHmac = (String)criptoLoja.descriptografar(stub.requisitarChaveHmacLoja(usedPort)).getMensagem();


				if(toMainMenu==1) {
					tentativas = 0;
					while(keepLogged) {
						Mensagem msgDescriptograda;
						String option,temp, descriptografado;
						byte[] reply;
						
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
							Veiculo added = adicionarVeiculo();
							reply = stub.adicionar(montarRequest(added,criptoLoja), connection);
							Veiculo v = (Veiculo) handleResponse(reply, criptoLoja);
							if(v!=null) {
								System.out.println(v);
							}
							else System.out.println("Requisicao Invalida");
							break;
						case 2:
							temp = buscarVeiculo();
							cont = 0;
							reply = stub.buscar(montarRequest(temp,criptoLoja), connection);
							List<Veiculo> veiculos = (List<Veiculo>) handleResponse(reply, criptoLoja);
							if(veiculos==null) {
								System.out.println("Nenhum correspondencia");
								break;
							}
							for(Veiculo c: veiculos) {
								cont++;
								System.out.println(c.toString());
							}
							break;
						case 3:
							temp = listarVeiculos();
							reply = stub.listar(montarRequest(temp,criptoLoja), connection);
							veiculos = (List<Veiculo>) handleResponse(reply, criptoLoja);
							if(temp!=null && veiculos!=null) {
								for(Veiculo d : veiculos) {
									System.out.println(d.toString());
								}
							}
							break;
						case 4:

							System.out.println("Digite o renavam do veiculo: ");
							temp = s.nextLine();

							Veiculo vech = alterarVeiculo();
							reply = stub.atualizar(montarRequest(temp,criptoLoja),
									montarRequest(vech,criptoLoja),connection);
							Veiculo novoVec = (Veiculo) handleResponse(reply, criptoLoja);
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
							reply = stub.buscar(montarRequest(temp,criptoLoja), connection);
							veiculos = (List<Veiculo>) handleResponse(reply, criptoLoja);
							if(veiculos == null) {
								System.out.println("Nenhuma correspondencia");
								break;
							}
							for(Veiculo c: veiculos) {
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
								reply = stub.deletar(montarRequest(temp,criptoLoja),connection);
								boolean retorno = (boolean) handleResponse(reply, criptoLoja);
								if(retorno) {
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
							reply = stub.buscar(montarRequest(temp,criptoLoja),connection);
							veiculos = (List<Veiculo>) handleResponse(reply,criptoLoja);
							if(veiculos == null) {
								System.out.println("Nenhuma correspondencia");
								break;
							}
							for(Veiculo c: veiculos) {
								cont++;
								System.out.println(cont+"° veiculo: \n"+c.toString());
							}
							if(cont==0) {
								System.out.println("Nenhuma correspondencia");
							}
							else {
								System.out.println("Informe o renavam do veiculo desejado: ");
								temp = s.nextLine();
								reply = stub.comprar(montarRequest(temp,criptoLoja), connection);
								boolean retornoCompra = (boolean) handleResponse(reply,criptoLoja);
								s.nextLine();
								if(retornoCompra) {
									System.out.println("Compra realizada!");
								}
								else System.out.println("Veiculo nao encontrado");
							}
							break;

						case 7:
							reply = stub.getQuantidade(connection);
							int quantidade = (int) handleResponse(reply,criptoLoja);
							System.out.println("Total de veiculos: "+quantidade);
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
					tentativas = 0;
					while(keepLogged) {

						Mensagem msgDescriptograda, msg;
						String option,temp, descriptografado;
						byte[] reply;
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
							msg = new Mensagem(temp, criptoLoja.assinarHash(criptoLoja.hMac(temp)));
							reply = stub.buscar(criptoLoja.criptografar(msg), connection);
							List<Veiculo> veiculos = (List<Veiculo>) handleResponse(reply,criptoLoja);
							if(veiculos == null) {
								System.out.println("Nenhuma correspondencia");
								break;
							}
							for(Veiculo c: veiculos) {
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
								msg = new Mensagem(temp, criptoLoja.assinarHash(criptoLoja.hMac(temp)));
								reply = stub.listar(criptoLoja.criptografar(msg), connection);
								veiculos = (List<Veiculo>) handleResponse(reply,criptoLoja);
								if(veiculos!=null) {
									for(Veiculo v : veiculos) {
										System.out.println(v.toString());
									}
								}
							}
							break;

						case 3:
							System.out.println("Digite o nome do veiculo: ");
							temp = s.nextLine();
							s.nextLine();

							cont = 0;
							msg = new Mensagem(temp, criptoLoja.assinarHash(criptoLoja.hMac(temp)));
							reply = stub.buscar(criptoLoja.criptografar(msg), connection);
							veiculos = (List<Veiculo>) handleResponse(reply,criptoLoja);
							if(veiculos == null) {
								System.out.println("Nenhuma correspondencia");
								break;
							}
							for(Veiculo c: veiculos) {
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
								msg = new Mensagem(temp, criptoLoja.assinarHash(criptoLoja.hMac(temp)));
								reply = stub.comprar(criptoLoja.criptografar(msg), connection);
								boolean retornoCompra = (boolean) handleResponse(reply,criptoLoja);
								if(retornoCompra) {
									System.out.println("Compra realizada!");
								}
								else System.out.println("Veiculo nao encontrado");
							}
							break;

						case 4:
							reply = stub.getQuantidade(connection);
							int quantidade = (int) handleResponse(reply,criptoLoja);
							System.out.println("Total de veiculos: "+quantidade);
							break;
						case 5:
							keepLogged = false;
							break;
						default:
							System.out.println("Opcao Invalida! Tente denovo");
							break;
						}
					}
				} if(toMainMenu == 2){
					tentativas++;
					if(tentativas == 3){
						System.out.println("Você excedeu seu limite de tentativas de login, aguarde 10 segundos.");
						Thread.sleep(10000);
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
	public static void autenticar(Mensagem msg, Cripto cripto) throws Exception {
		DadoCifrado hmacAssinado = msg.gethMacAssinado();
		String hmac = cripto.verificarAssinatura(hmacAssinado);
		if(!hmac.equals(cripto.hMac(msg.getMensagem()))) {
			throw new NaoAutenticoException("Mensagem não autenticada!");
		}
	}
	public static Object handleResponse(byte[] reply, Cripto cripto) throws Exception {
		if(reply!=null) {
			Mensagem msgDecifrada = cripto.descriptografar(reply);
			autenticar(msgDecifrada,cripto);
			return msgDecifrada.getMensagem();
		}
		return null;
		
	}
	public static byte[] montarRequest(Object v, Cripto cripto) throws Exception {
		return cripto.criptografar(new Mensagem(v,cripto.assinarHash(cripto.hMac(v))));
	}
}