package model;

public class Funcionario extends Pessoa {
    private float salario_bruto;
	private float salario_liquido;
    private String setor;
    private float descontos;

    public Funcionario(String _nome, String _cpf, String _data) {
        super(_nome, _cpf, _data);
    }
    
    public float getSalario_bruto() {
		return salario_bruto;
	}

	public void setSalario_bruto(float salario_bruto) {
		this.salario_bruto = salario_bruto;
	}

	public float getSalario_liquido() {
		return salario_liquido;
	}

	public void setSalario_liquido(float salario_liquido) {
		this.salario_liquido = salario_liquido;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public float getDescontos() {
		return descontos;
	}

	public void setDescontos(float descontos) {
		this.descontos = descontos;
	}

}
