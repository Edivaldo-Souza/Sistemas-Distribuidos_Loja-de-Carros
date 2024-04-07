package model;

public enum TipoDeUsuario {
	CLIENTE(0),
	FUNCIONARIO(1);
	
	private int i;
	
	private TipoDeUsuario(int i) {
		this.i = i;
	}
}
