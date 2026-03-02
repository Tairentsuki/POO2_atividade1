package com.atividade001.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "movimentacao")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private TipoMovimentacao tipo;
    private LocalDateTime data;
    private double valor;
    private String descricao;

    @ManyToOne
    private Setor setor;
    @ManyToOne
    private Funcionario funcionario;

    public Movimentacao() {
    }

    public Movimentacao(int id, TipoMovimentacao tipo, LocalDateTime data, double valor, String descricao, Setor setor, Funcionario funcionario) {
        this.id = this.getId();
        this.tipo = this.getTipo();
        this.data = this.getData();
        this.valor = this.getValor();
        this.descricao = this.getDescricao();
        this.setor = this.getSetor();
        this.funcionario = this.getFuncionario();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}