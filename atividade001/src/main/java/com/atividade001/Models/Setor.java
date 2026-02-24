package com.atividade001.Models;

public class Setor {
    private String nome;
    private String ramal;
    private Funcionario funcionario;

    public Setor() {
    }

    public Setor(String nome, String ramal, Funcionario funcionario) {
        this.nome = nome;
        this.ramal = ramal;
        this.funcionario = funcionario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setRamal(String ramal) {
        this.ramal = ramal;
    }

    public String getRamal() {
        return this.ramal;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Funcionario getFuncionario() {
        return this.funcionario;
    }

}
