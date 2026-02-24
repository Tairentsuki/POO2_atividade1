package com.atividade001.Services;

import com.atividade001.Models.Funcionario;
import com.atividade001.Models.Pessoa;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import net.datafaker.Faker;

public class DataFakerGenerator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    public static Pessoa gerarFuncionario() {
        String nome = faker.name().fullName();
        String cpf = faker.cpf().toString().formatted(true);
        String dataDeNascimento = faker.timeAndDate().past(36525, TimeUnit.DAYS, "yyyy-MM-dd");

        return new Funcionario(nome, cpf, dataDeNascimento);
    }
}
