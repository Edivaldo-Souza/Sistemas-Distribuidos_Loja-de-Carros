package server;

import java.rmi.RemoteException;

import interfaces.Autenticador;
import model.Credenciais;
import model.TipoDeUsuario;

public class ImplAutenticador implements Autenticador{
	
	@Override
	public int autenticar(Credenciais c) throws RemoteException {
		Credenciais c1 = new Credenciais(TipoDeUsuario.CLIENTE,"Joao","12345");
		Credenciais c2 = new Credenciais(TipoDeUsuario.CLIENTE,"Maria","54321");
		Credenciais c3 = new Credenciais(TipoDeUsuario.FUNCIONARIO,"Pablo","67890");
		Credenciais[] credenciais = {c1,c2,c3};
		for(Credenciais cred : credenciais) {
			if(c.compareTo(cred)) {
				if(cred.getTipo()==TipoDeUsuario.FUNCIONARIO) {
					return 1;
				}
				return 0;
			}
		}
		
		return 2;
	}
	
}

