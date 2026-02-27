package model;

import java.time.LocalDateTime;

public class Movimentacao {
    private int id;
    private TipoMovimentacao tipo;
    private LocalDateTime data;

    private double valor;
    private String descricao;

    public Movimentacao(int id, TipoMovimentacao tipo, LocalDateTime data, double valor, String descricao) {
        this.id = id;
        this.tipo = tipo;
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Movimentacao [id=").append(id);
        sb.append(", tipo=").append(tipo);
        sb.append(", data=").append(data);
        sb.append(", valor=").append(valor);
        sb.append(", descricao=").append(descricao);
        sb.append("]");
        return sb.toString();
    }

}
