package com.atividade001.model;

import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataDeNascimento;
    private String sexo;

    public Pessoa(String nomeCompleto, String cpf, LocalDate dataDeNascimento, String sexo) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.dataDeNascimento = dataDeNascimento;
        this.sexo = sexo;
    }

    public Pessoa(int id, String nomeCompleto, String cpf, LocalDate dataDeNascimento, String sexo) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.dataDeNascimento = dataDeNascimento;
        this.sexo = sexo;
    }

    public int calcularIdade() {
        LocalDate hoje = LocalDate.now();
        return Period.between(this.dataDeNascimento, hoje).getYears();
    }
}
