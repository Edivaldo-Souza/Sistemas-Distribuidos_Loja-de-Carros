package model;

import java.io.Serializable;

public class CredenciaisDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isFuncionario;
	private String nome;
	public CredenciaisDTO(boolean isFuncionario, String nome) {
		super();
		this.isFuncionario = isFuncionario;
		this.nome = nome;
	}
	public boolean isFuncionario() {
		return isFuncionario;
	}
	public void setFuncionario(boolean isFuncionario) {
		this.isFuncionario = isFuncionario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
