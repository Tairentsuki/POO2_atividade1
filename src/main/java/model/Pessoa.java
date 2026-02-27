package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public abstract class Pessoa {
    private int id;
    private String nome;
    private String cpf;
    private LocalDate dataDeNascimento;

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
        try {
            this.dataDeNascimento = LocalDate.parse(dataDeNascimento);
        } catch (DateTimeParseException e) {
            System.err.println("Erro: A data deve estar no formato YYYY-MM-DD. Valor recebido: " + dataDeNascimento);
        }
    }

    public LocalDate getDataDeNascimento() {
        return this.dataDeNascimento;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("id: ").append(id).append("\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Cpf: ").append(cpf).append("\n");
        sb.append("Data de nascimento: ").append(dataDeNascimento).append("\n");

        return sb.toString();
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();

        sb.append(dataDeNascimento).append(',');
        sb.append(nome).append(',');
        sb.append(cpf).append(',');
        sb.append(dataDeNascimento != null ? dataDeNascimento.toString() : "");

        return sb.toString();
    }
}
