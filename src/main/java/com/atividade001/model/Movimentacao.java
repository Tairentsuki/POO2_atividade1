package com.atividade001.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Classe de domínio que representa uma movimentação financeira.
 */
@Getter
@Setter
@Entity
@Table(name = "movimentacao")
@NoArgsConstructor
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

    /**
     * Constrói movimentação sem id.
     *
     * @param tipo tipo da movimentação
     * @param data data e hora da movimentação
     * @param valor valor da movimentação
     * @param descricao descrição
     * @param setor setor relacionado
     * @param funcionario funcionário relacionado
     */
    public Movimentacao(
            TipoMovimentacao tipo,
            LocalDateTime data,
            double valor,
            String descricao,
            Setor setor,
            Funcionario funcionario) {
        this.tipo = tipo;
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
        this.setor = setor;
        this.funcionario = funcionario;
    }

    /**
     * Constrói movimentação com id.
     *
     * @param id identificador
     * @param tipo tipo da movimentação
     * @param data data e hora da movimentação
     * @param valor valor da movimentação
     * @param descricao descrição
     * @param setor setor relacionado
     * @param funcionario funcionário relacionado
     */
    public Movimentacao(
            int id,
            TipoMovimentacao tipo,
            LocalDateTime data,
            double valor,
            String descricao,
            Setor setor,
            Funcionario funcionario) {
        this.id = id;
        this.tipo = tipo;
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
        this.setor = setor;
        this.funcionario = funcionario;
    }

    /**
     * Converte movimentação para uma linha CSV simples.
     *
     * @return linha CSV com os campos principais
     */
    public String toCsv() {
        String tipoTexto = tipo != null ? tipo.name() : "";
        String dataTexto = data != null ? data.toString() : "";
        String nomeSetor = setor != null ? setor.getNome() : "";
        String cpfFuncionario = funcionario != null ? funcionario.getCpf() : "";

        return tipoTexto + "," + dataTexto + "," + valor + "," + limpar(descricao) + ","
                + limpar(nomeSetor) + "," + limpar(cpfFuncionario);
    }

    /**
     * Remove vírgulas e espaços excedentes do texto.
     *
     * @param valor texto original
     * @return texto limpo para CSV
     */
    private String limpar(String valor) {
        return valor == null ? "" : valor.replace(",", " ").trim();
    }
}
