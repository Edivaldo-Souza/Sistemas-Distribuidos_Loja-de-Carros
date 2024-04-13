package interfaces;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cripto.Chave;
import cripto.DadoCifrado;
import model.Veiculo;

public interface ServicoLojaDeCarros extends Remote{
	byte[] adicionar(byte[] v) throws Exception;
	byte[] buscar(byte[] renavam) throws Exception;
	byte[] listar(byte[] categoria) throws Exception;
	Veiculo atualizar(String renavam, Veiculo v) throws RemoteException;
	boolean deletar(String v) throws RemoteException;
	boolean comprar(String v) throws RemoteException;
	int getQuantidade() throws RemoteException;
	Chave trocaDeChavesRsa(Chave publicKey) throws  RemoteException;
	byte[] requisitarChaveAes() throws IOException;
	byte[] requisitarChaveHmac() throws IOException;
}
