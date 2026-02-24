package com.atividade001.Models;

public abstract class Pessoa {
    private String nome;
    private String cpf;
    private String dataDeNascimento;

    public Pessoa(String[] strings) {
    }

    public Pessoa(String nome, String cpf, String dataDeNascimento) {
        this.setNome(nome);
        this.setCpf(cpf);
        this.setDataDeNascimento(dataDeNascimento);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return this.cpf;
    }

    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getDataDeNascimento() {
        return this.dataDeNascimento;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Cpf: ").append(cpf).append("\n");
        sb.append("Data de nascimento: ").append(dataDeNascimento).append("\n");

        return sb.toString();
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();

        sb.append(nome).append(',');
        sb.append(cpf).append(',');
        sb.append(dataDeNascimento);

        return sb.toString();
    }
}
