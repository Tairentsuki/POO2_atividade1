package services;

import java.time.LocalDate;
import java.util.Locale;

import model.Funcionario;
import model.Setor;
import net.datafaker.Faker;

public class DataFakerGenerator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    private static int contadorFuncionario = 1;
    private static int contadorSetor = 1;

    public static Funcionario gerarFuncionario() {
        RegistroFuncionario registro = gerarRegistroFuncionario();
        Setor setor = new Setor(registro.getIdSetor(), registro.getNomeSetor(), registro.getRamal());

        return new Funcionario(
                registro.getIdFuncionario(),
                registro.getNomeCompleto(),
                registro.getCpf(),
                registro.getDataNascimento(),
                registro.getSexo(),
                registro.getSalarioBruto(),
                registro.getFormacao(),
                setor);
    }

    public static RegistroFuncionario gerarRegistroFuncionario() {
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

        int idSetor = contadorSetor++;
        String ramal = String.valueOf(idSetor % 9000 + 1000);
        int idFuncionario = contadorFuncionario++;

        return new RegistroFuncionario(
                idFuncionario,
                nomeCompleto,
                dataNascimento,
                cpf,
                sexo,
                salarioBruto,
                formacao,
                idSetor,
                areaAtuacao,
                ramal);
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
        } else if (formacaoLower.contains("manager") || formacaoLower.contains("lead")
                || formacaoLower.contains("gerente")) {
            multiplicador = faker.number().randomDouble(2, 4, 6);
        } else if (formacaoLower.contains("senior") || formacaoLower.contains("principal")
                || formacaoLower.contains("sênior")) {
            multiplicador = faker.number().randomDouble(2, 2, 4);
        } else if (formacaoLower.contains("assistant") || formacaoLower.contains("assistente")
                || formacaoLower.contains("auxiliar")) {
            multiplicador = faker.number().randomDouble(2, 1, 2);
        }

        return multiplicador;
    }

    public static final class RegistroFuncionario {
        private final int idFuncionario;
        private final String nomeCompleto;
        private final LocalDate dataNascimento;
        private final String cpf;
        private final String sexo;
        private final double salarioBruto;
        private final String formacao;
        private final int idSetor;
        private final String nomeSetor;
        private final String ramal;

        private RegistroFuncionario(
                int idFuncionario,
                String nomeCompleto,
                LocalDate dataNascimento,
                String cpf,
                String sexo,
                double salarioBruto,
                String formacao,
                int idSetor,
                String nomeSetor,
                String ramal) {
            this.idFuncionario = idFuncionario;
            this.nomeCompleto = nomeCompleto;
            this.dataNascimento = dataNascimento;
            this.cpf = cpf;
            this.sexo = sexo;
            this.salarioBruto = salarioBruto;
            this.formacao = formacao;
            this.idSetor = idSetor;
            this.nomeSetor = nomeSetor;
            this.ramal = ramal;
        }

        public int getIdFuncionario() {
            return idFuncionario;
        }

        public String getNomeCompleto() {
            return nomeCompleto;
        }

        public LocalDate getDataNascimento() {
            return dataNascimento;
        }

        public String getCpf() {
            return cpf;
        }

        public String getSexo() {
            return sexo;
        }

        public double getSalarioBruto() {
            return salarioBruto;
        }

        public String getFormacao() {
            return formacao;
        }

        public int getIdSetor() {
            return idSetor;
        }

        public String getNomeSetor() {
            return nomeSetor;
        }

        public String getRamal() {
            return ramal;
        }
    }
}
