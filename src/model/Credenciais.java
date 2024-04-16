package model;

import cripto.HashWithSalt;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Credenciais implements Serializable {
	private TipoDeUsuario tipo;
	private String nome;
	private String senha;
	private byte[] salt;
	public Credenciais() {
		
	}
	
	public Credenciais(TipoDeUsuario tipo, String nome, String senha) {
		this.tipo = tipo;
		this.nome = nome;
		this.senha = senha;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
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
	
	public boolean compareTo(Credenciais c) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return (this.nome.equals(c.getNome()) && this.senha.equals(c.getSenha()) );
    }
	
	
}

