package services;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import model.Funcionario;
import model.Pessoa;
import net.datafaker.Faker;

public class DataFakerGenerator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    public static Pessoa gerarFuncionario() {
        String nome = faker.name().fullName();
        String cpf = faker.cpf().toString();
        String dataDeNascimento = faker.timeAndDate().past(36525, TimeUnit.DAYS, "yyyy-MM-dd");

        return new Funcionario(nome, cpf, dataDeNascimento);
    }
}
