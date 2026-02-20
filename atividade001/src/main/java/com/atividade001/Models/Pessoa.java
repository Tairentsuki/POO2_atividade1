package com.atividade001.Models;

public class Pessoa {
    private String nome;
    private String endereco;
    private String dataDeNascimento;

    public Pessoa(String[] strings) {
    }

    public Pessoa(String nome, String endereco, String dataDeNascimento) {
        this.setNome(nome);
        this.setEndereco(endereco);
        this.setDataDeNascimento(dataDeNascimento);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEndereco() {
        return this.endereco;
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
        sb.append("Endereço: ").append(endereco).append("\n");
        sb.append("Data de nascimento: ").append(dataDeNascimento).append("\n");

        return sb.toString();
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();

        sb.append(nome).append(',');
        sb.append(endereco).append(',');
        sb.append(dataDeNascimento);

        return sb.toString();
    }
}
