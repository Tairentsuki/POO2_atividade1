package model;

import java.util.ArrayList;
import java.util.List;

public class Setor {

    private int id;
    private String nome;
    private String ramal;

    private List<Funcionario> funcionarios;
    private List<Movimentacao> movimentacoes;

    public Setor() {
    }

    public Setor(int _id, String _nome, String _ramal) {
        this.id = _id;
        this.nome = _nome;
        this.ramal = _ramal;
        this.funcionarios = new ArrayList<>();
        this.movimentacoes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRamal() {
        return ramal;
    }

    public void setRamal(String ramal) {
        this.ramal = ramal;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<Movimentacao> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public void addFuncionario(Funcionario funcionario) {
        this.funcionarios.add(funcionario);
    }

    public void addMovimentacao(Movimentacao movimentacao) {
        this.movimentacoes.add(movimentacao);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(this.id).append("\n");
        sb.append("Nome: ").append(this.nome).append("\n");
        sb.append("Ramal: ").append(this.ramal).append("\n");

        return sb.toString();
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.id).append(',');
        sb.append(this.nome).append(',');
        sb.append(this.ramal);

        return sb.toString();
    }
}