package model;

import java.io.Serializable;

public class Credenciais implements Serializable {
	private TipoDeUsuario tipo;
	private String nome;
	private String senha;
	
	public Credenciais() {
		
	}
	
	public Credenciais(TipoDeUsuario tipo, String nome, String senha) {
		this.tipo = tipo;
		this.nome = nome;
		this.senha = senha;
	}

	public TipoDeUsuario getTipo() {
		return tipo;
	}

	public void setTipo(TipoDeUsuario tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public boolean compareTo(Credenciais c) {
		if(this.nome.equals(c.getNome()) && this.senha.equals(c.getSenha())) {
			return true;
		}
		return false;
	}
	
	
}

