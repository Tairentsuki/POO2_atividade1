package model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
	private double salarioBruto;
	private String formacao;
	private Setor setor;

	public Funcionario(int _id, String _nome, String _cpf, LocalDate _data_nascimento, String _sexo) {
		super(_id, _nome, _cpf, _data_nascimento, _sexo);

	}

	public Funcionario(int _id, String _nome, String _cpf, LocalDate _data_nascimento, String _sexo, double _salarioBruto, String _formacao, Setor _setor) {
		super(_id, _nome, _cpf, _data_nascimento, _sexo);
		this.setSalarioBruto(_salarioBruto);
		this.setFormacao(_formacao);
		this.setSetor(_setor);
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
		sb.append(setor.toCsv());

		return sb.toString();
	}

}
