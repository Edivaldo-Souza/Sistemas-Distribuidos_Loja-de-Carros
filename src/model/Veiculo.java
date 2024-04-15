package model;

import java.io.Serializable;

public class Veiculo implements Serializable, Comparable<Veiculo>{

	private static final long serialVersionUID = 1L;
	private Categorias categoria;
	private String renavam;
	private String nome;
	private int anoFabricacao;
	private double preco;
	private boolean disponivel;
	
	public Veiculo() {}
	
	public Veiculo(Categorias categoria, String renavam, String nome, int anoFabricacao, double preco) {
		super();
		this.categoria = categoria;
		this.renavam = renavam;
		this.nome = nome;
		this.anoFabricacao = anoFabricacao;
		this.preco = preco;
		this.setDisponivel(true);
	}

	public Categorias getCategoria() {
		return categoria;
	}

	public void setCategoria(Categorias categoria) {
		this.categoria = categoria;
	}

	public String getRenavam() {
		return renavam;
	}

	public void setRenavam(String renavam) {
		this.renavam = renavam;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getAnoFabricacao() {
		return anoFabricacao;
	}

	public void setAnoFabricacao(int anoFabricacao) {
		this.anoFabricacao = anoFabricacao;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}
	
	public boolean isDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}

	public String toString() {
		String str = "Informacoes do veiculo:\n"
				+ "Categoria: "+this.categoria+"\n"
				+ "Nome: "+this.nome+"\n"
				+ "Renavam: "+this.renavam+"\n"
				+ "Ano de Fabricao: "+this.anoFabricacao+"\n"
				+ "Preço: R$"+this.preco+"\n";
		if(this.disponivel) {
			str = str + "Status: Disponivel\n";
		}
		else 
			str = str + "Status: Vendido\n";
		return str; 
				 
	}

	@Override
	public int compareTo(Veiculo v) {
		return this.nome.compareTo(v.getNome());
		
	}
	
	public void toVeiculo(String v) {
		String[] params = v.split("0ersf1");
		
		if(params[0].equals("ECONOMICO")) this.categoria = Categorias.ECONOMICO;
		else if(params[0].equals("INTERMEDIARIO")) this.categoria = Categorias.INTERMEDIARIO;
		else if(params[0].equals("EXECUTIVO")) this.categoria = Categorias.EXECUTIVO;
		
		this.nome = params[1];
		this.renavam = params[2];
		this.anoFabricacao = Integer.parseInt(params[3]);
		if(params[4].equals("D")) this.disponivel = true;
		else this.disponivel = false;
		this.preco = Double.parseDouble(params[5]);
	}
	
	
}
