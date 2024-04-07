package model;

public enum Categorias {
	ECONOMICO(1),
	INTERMEDIARIO(2),
	EXECUTIVO(3);
	
	private int cat;
	
	Categorias(int c) {
		this.cat = c;
	}
}
