package com.atividade001.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionario")
public class Funcionario extends Pessoa {
	private double salarioBruto;
	private String formacao;
	@ManyToOne
	private Setor setor;
	private LocalDate dataAdmissao;

	public Funcionario() {
		super();
	}

	public Funcionario(String nomeCompleto, String cpf, LocalDate dataDeNascimento, String sexo, double salarioBruto, String formacao, Setor setor, LocalDate dataAdmissao) {
		super(nomeCompleto, cpf, dataDeNascimento, sexo);
		this.setSalarioBruto(salarioBruto);
		this.setFormacao(formacao);
		this.setSetor(setor);
		this.setDataAdmissao(dataAdmissao);
	}

	public Funcionario(int _id, String _nome, String _cpf, LocalDate _data_nascimento, String _sexo) {
		super(_id, _nome, _cpf, _data_nascimento, _sexo);

	}

	public Funcionario(int _id, String _nome, String _cpf, LocalDate _data_nascimento, String _sexo, double _salarioBruto, String _formacao, Setor _setor, LocalDate _dataAdmissao) {
		super(_id, _nome, _cpf, _data_nascimento, _sexo);
		this.setSalarioBruto(_salarioBruto);
		this.setFormacao(_formacao);
		this.setSetor(_setor);
		this.setDataAdmissao(_dataAdmissao);
	}

	public void setSalarioBruto(double _salarioBruto) {
		this.salarioBruto = _salarioBruto;
	}

	public double getSalarioBruto() {
		return salarioBruto;
	}

	public void setFormacao(String _formacao) {
		this.formacao = _formacao;
	}

	public String getFormacao() {
		return this.formacao;
	}

	public void setSetor(Setor _setor) {
		this.setor = _setor;
	}

	public Setor getSetor() {
		return setor;
	}

	public LocalDate getDataAdmissao() {
		return this.dataAdmissao;
	}

	public void setDataAdmissao(LocalDate _dataAdmissao) {
		this.dataAdmissao = _dataAdmissao;
	}

	public double calcularInss() {
		return 0.0;
	}

	public double calcularIrpf() {
		return 0.0;
	}

	public double calcularSalarioLiquido() {
		return getSalarioBruto() - calcularInss() - calcularIrpf();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String divisoria = "=".repeat(50);
		sb.append(divisoria).append("\n");
		sb.append("Informações do Funcionário").append("\n");
		sb.append(divisoria).append("\n");
		sb.append(super.toString());
		sb.append("Salário bruto: R$ ").append(salarioBruto).append("\n");
		sb.append("Formação: ").append(formacao).append("\n");
		sb.append(divisoria).append("\n");
		sb.append("Informações do Setor").append("\n");
		sb.append(divisoria).append("\n");
		sb.append(setor.toString()).append("\n");

		return sb.toString();
	}

	@Override
	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toCsv()).append(",");
		sb.append(salarioBruto).append(",");
		sb.append(formacao).append(",");
		sb.append(dataAdmissao).append(",");
		sb.append(setor.toCsv());

		return sb.toString();
	}

}
