package com.atividade001.services;

import java.time.LocalDate;
import java.util.Locale;

import com.atividade001.model.Funcionario;
import com.atividade001.model.Setor;
import net.datafaker.Faker;

public class DataFakerGenerator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    private static int contadorFuncionario = 1;
    private static int contadorSetor = 1;

    public static Funcionario gerarFuncionario() {
        return criarFuncionario(true);
    }

    public static Funcionario gerarFuncionarioPersistivel() {
        return criarFuncionario(false);
    }

    private static Funcionario criarFuncionario(boolean incluirIds) {
        String areaAtuacao = faker.job().field();
        String posicao = faker.job().position();
        String formacao = posicao + " de " + areaAtuacao;
        double salarioBruto = gerarSalarioBruto(formacao);

        boolean isMasculino = faker.bool().bool();
        String primeiroNome = isMasculino ? faker.name().maleFirstName() : faker.name().femaleFirstName();
        String nomeCompleto = primeiroNome + " " + faker.name().lastName();
        LocalDate dataNascimento = faker.timeAndDate().birthday(18, 65);
        String sexo = isMasculino ? "Masculino" : "Feminino";
        String cpf = faker.cpf().valid();

        Setor setor;
        if (incluirIds) {
            int proximoSetor = contadorSetor++;
            String ramal = String.valueOf(proximoSetor % 9000 + 1000);
            setor = new Setor(proximoSetor, areaAtuacao, ramal);
        } else {
            String ramal = String.valueOf(faker.number().numberBetween(1000, 10000));
            setor = new Setor(areaAtuacao, ramal);
        }

        if (incluirIds) {
            int proximoFuncionario = contadorFuncionario++;
            return new Funcionario(proximoFuncionario, nomeCompleto, cpf, dataNascimento, sexo, salarioBruto, formacao, setor);
        }

        return new Funcionario(nomeCompleto, cpf, dataNascimento, sexo, salarioBruto, formacao, setor);
    }

    private static double gerarSalarioBruto(String formacao) {
        double salarioBase = faker.number().randomDouble(2, 1412, 6000);
        double multiplicador = calcularMultiplicador(formacao);
        return Math.round((salarioBase * multiplicador) * 100.0) / 100.0;
    }

    private static double calcularMultiplicador(String formacao) {
        double multiplicador = 1.0;
        String formacaoLower = formacao.toLowerCase();

        if (formacaoLower.contains("chief") || formacaoLower.contains("director") || formacaoLower.contains("diretor")) {
            multiplicador = faker.number().randomDouble(2, 6, 8);
        } else if (formacaoLower.contains("manager") || formacaoLower.contains("lead") || formacaoLower.contains("gerente")) {
            multiplicador = faker.number().randomDouble(2, 4, 6);
        } else if (formacaoLower.contains("senior") || formacaoLower.contains("principal") || formacaoLower.contains("sênior")) {
            multiplicador = faker.number().randomDouble(2, 2, 4);
        } else if (formacaoLower.contains("assistant") || formacaoLower.contains("assistente") || formacaoLower.contains("auxiliar")) {
            multiplicador = faker.number().randomDouble(2, 1, 2);
        }

        return multiplicador;
    }
}
