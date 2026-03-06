package com.atividade001.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

/**
 * Classe de domínio que representa um setor da empresa.
 */
@Getter
@Setter
@Entity
@Table(name = "setor")
@NoArgsConstructor
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String ramal;

    @OneToMany(mappedBy = "setor")
    private List<Funcionario> funcionarios = new ArrayList<>();
    @OneToMany(mappedBy = "setor")
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    /**
     * Cria um setor com nome e ramal.
     *
     * @param nome nome do setor
     * @param ramal ramal do setor
     */
    public Setor(String nome, String ramal) {
        this.nome = nome;
        this.ramal = ramal;
    }

    /**
     * Cria um setor com id, nome e ramal.
     *
     * @param id identificador do setor
     * @param nome nome do setor
     * @param ramal ramal do setor
     */
    public Setor(int id, String nome, String ramal) {
        this.id = id;
        this.nome = nome;
        this.ramal = ramal;
    }

    /**
     * Converte o setor para uma linha CSV simples.
     *
     * @return linha CSV com nome e ramal
     */
    public String toCsv() {
        return limpar(nome) + "," + limpar(ramal);
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
