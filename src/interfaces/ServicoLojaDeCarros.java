package interfaces;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cripto.Chave;
import cripto.Cripto;
import cripto.DadoCifrado;
import model.Veiculo;

public interface ServicoLojaDeCarros extends Remote{
	byte[] adicionar(byte[] v) throws Exception;
	byte[] buscar(byte[] renavam) throws Exception;
	byte[] listar(byte[] categoria) throws Exception;
	byte[] atualizar(byte[] renavam, byte[] v) throws RemoteException;
	byte[] deletar(byte[] v) throws Exception;
	byte[] comprar(byte[] v) throws Exception;
	byte[] getQuantidade() throws Exception;
	Chave trocaDeChavesRsa(Chave publicKey) throws  RemoteException;
	byte[] requisitarChaveAes() throws IOException;
	byte[] requisitarChaveHmac() throws IOException;
	Cripto getCripto() throws  RemoteException;
}
