package com.G3.scm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Entity
public class Veiculo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true)
	private String placa;
	
	@Size(min = 1, max = 50, message = "Nome deve ser preenchido")
	private String nome;
	
	@Size(min = 1, max = 50, message = "Modelo deve ser preenchido")
	private String modelo;
	private boolean locado;
	private String capacidade;
	private String cor;
	private int km;
	
	public Veiculo() {}
	
	public Veiculo(String placa, String nome,String modelo, String capacidade, int km) {
		super();
		this.placa = placa;
		this.nome = nome;
		this.modelo = modelo;
		this.capacidade = capacidade;
		this.km = km;
		this.locado = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public boolean isLocado() {
		return locado;
	}

	public void setLocado(boolean locado) {
		this.locado = locado;
	}

	public String getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(String capacidade) {
		this.capacidade = capacidade;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public int getKm() {
		return km;
	}

	public void setKm(int km) {
		this.km = km;
	}
	
	
}
