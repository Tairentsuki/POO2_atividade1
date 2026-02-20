package com.atividade001.Services;

import com.atividade001.Models.Pessoa;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import net.datafaker.Faker;

public class DataFakerGenerator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    public static Pessoa gerarPessoa() {
        String nome = faker.name().fullName();
        String endereco = faker.address().streetAddress();
        String dataDeNascimento = faker.timeAndDate().past(36525, TimeUnit.DAYS, "yyyy-MM-dd");

        return new Pessoa(nome, endereco, dataDeNascimento);
    }
}
