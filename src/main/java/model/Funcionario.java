package model;

public class Funcionario extends Pessoa {
	private double SalarioBruto;
	private String setor;

	public Funcionario(String _nome, String _cpf, String _data) {
		super(_nome, _cpf, _data);
	}

	public void setSalarioBruto(double SalarioBruto) {
		this.SalarioBruto = SalarioBruto;
	}

	public double getSalarioBruto() {
		return SalarioBruto;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public String getSetor() {
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

}
