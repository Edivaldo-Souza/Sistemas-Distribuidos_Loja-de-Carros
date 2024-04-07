package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import interfaces.Autenticador;
import interfaces.ServicoLojaDeCarros;

public class Servidor {
	private static int currentPort = 20001;
	public static void main(String args[]) {
		try {
			ImplAutenticador refObjetoRemoto = new ImplAutenticador();
			ImplServicoLojaDeCarros refObjRemotoServico = new ImplServicoLojaDeCarros();
			Autenticador skeleton = (Autenticador) UnicastRemoteObject.exportObject(refObjetoRemoto, 0);
			ServicoLojaDeCarros skeletonServico = (ServicoLojaDeCarros)
					UnicastRemoteObject.exportObject(refObjRemotoServico, 1);
			
			LocateRegistry.createRegistry(currentPort);
			Registry registro = LocateRegistry.getRegistry(currentPort);
			
			registro.bind("Autenticador", skeleton);
			registro.bind("ServicoLojaDeCarros", skeletonServico);
			System.out.println(currentPort++);
			System.out.println("Servidor pronto:");
		} catch (Exception e) {
			System.err.println("Servidor: " + e.toString());
			e.printStackTrace();
		}
	}
}
