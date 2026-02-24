package com.atividade001.Models;

public class Funcionario extends Pessoa {
    private float salario_bruto;
    private float salario_liquido;
    private String setor;
    private float descontos;

    public Funcionario(String _nome, String _cpf, String _data) {
        super(_nome, _cpf, _data);
    }

}
