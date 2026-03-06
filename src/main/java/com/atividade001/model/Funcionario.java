package com.atividade001.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

/**
 * Classe de domínio que representa um funcionário.
 */
@Getter
@Setter
@Entity
@Table(name = "funcionario")
@NoArgsConstructor
public class Funcionario extends Pessoa {
    private double salarioBruto;
    private String formacao;

    @ManyToOne
    private Setor setor;

    private LocalDate dataAdmissao;

    /**
     * Constrói um funcionário sem id (uso comum na persistência).
     *
     * @param nomeCompleto nome completo
     * @param cpf CPF
     * @param dataDeNascimento data de nascimento
     * @param sexo sexo
     * @param salarioBruto salário bruto
     * @param formacao formação/cargo
     * @param setor setor do funcionário
     * @param dataAdmissao data de admissão
     */
    public Funcionario(
            String nomeCompleto,
            String cpf,
            LocalDate dataDeNascimento,
            String sexo,
            double salarioBruto,
            String formacao,
            Setor setor,
            LocalDate dataAdmissao) {
        super(nomeCompleto, cpf, dataDeNascimento, sexo);
        this.salarioBruto = salarioBruto;
        this.formacao = formacao;
        this.setor = setor;
        this.dataAdmissao = dataAdmissao;
    }

    /**
     * Constrói funcionário com dados básicos e id.
     *
     * @param id identificador
     * @param nome nome completo
     * @param cpf CPF
     * @param dataNascimento data de nascimento
     * @param sexo sexo
     */
    public Funcionario(int id, String nome, String cpf, LocalDate dataNascimento, String sexo) {
        super(id, nome, cpf, dataNascimento, sexo);
    }

    /**
     * Constrói funcionário completo com id.
     *
     * @param id identificador
     * @param nome nome completo
     * @param cpf CPF
     * @param dataNascimento data de nascimento
     * @param sexo sexo
     * @param salarioBruto salário bruto
     * @param formacao formação/cargo
     * @param setor setor
     * @param dataAdmissao data de admissão
     */
    public Funcionario(
            int id,
            String nome,
            String cpf,
            LocalDate dataNascimento,
            String sexo,
            double salarioBruto,
            String formacao,
            Setor setor,
            LocalDate dataAdmissao) {
        super(id, nome, cpf, dataNascimento, sexo);
        this.salarioBruto = salarioBruto;
        this.formacao = formacao;
        this.setor = setor;
        this.dataAdmissao = dataAdmissao;
    }

    /**
     * Converte funcionário para uma linha CSV simples.
     *
     * @return linha CSV com os campos principais
     */
    public String toCsv() {
        String nascimento = getDataDeNascimento() != null ? getDataDeNascimento().toString() : "";
        String setorNome = setor != null ? setor.getNome() : "";

        return limpar(getNomeCompleto()) + "," + limpar(getCpf()) + "," + nascimento + ","
                + limpar(getSexo()) + "," + salarioBruto + "," + limpar(formacao) + "," + limpar(setorNome);
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
