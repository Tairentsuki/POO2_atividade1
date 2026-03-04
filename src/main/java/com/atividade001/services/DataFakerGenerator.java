package com.atividade001.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.atividade001.model.Funcionario;
import com.atividade001.model.Movimentacao;
import com.atividade001.model.Setor;
import com.atividade001.model.TipoMovimentacao;

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

    public static List<Movimentacao> gerarMovimentacoesPersistiveis(List<Funcionario> funcionarios) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        if (funcionarios == null || funcionarios.isEmpty()) {
            return movimentacoes;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate inicioHistorico = hoje.minusMonths(14).withDayOfMonth(1);

        Map<Setor, List<Funcionario>> funcionariosPorSetor = funcionarios.stream()
                .filter(funcionario -> funcionario.getSetor() != null)
                .collect(Collectors.groupingBy(Funcionario::getSetor));

        for (Map.Entry<Setor, List<Funcionario>> entry : funcionariosPorSetor.entrySet()) {
            movimentacoes.addAll(gerarMovimentacoesDoSetor(entry.getKey(), entry.getValue(), inicioHistorico, hoje));
        }

        movimentacoes.sort((a, b) -> b.getData().compareTo(a.getData()));
        return movimentacoes;
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
        LocalDate dataAdmissao = gerarDataAdmissao(dataNascimento);

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
            return new Funcionario(proximoFuncionario, nomeCompleto, cpf, dataNascimento, sexo, salarioBruto, formacao, setor, dataAdmissao);
        }

        return new Funcionario(nomeCompleto, cpf, dataNascimento, sexo, salarioBruto, formacao, setor, dataAdmissao);
    }

    private static List<Movimentacao> gerarMovimentacoesDoSetor(Setor setor, List<Funcionario> funcionarios, LocalDate inicioHistorico, LocalDate fimHistorico) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        if (funcionarios == null || funcionarios.isEmpty()) {
            return movimentacoes;
        }

        ContextoSetor contexto = montarContextoSetor(setor, funcionarios, inicioHistorico, fimHistorico);
        int quantidadeMovimentacoes = calcularQuantidadeMovimentacoes(contexto, fimHistorico);

        for (int i = 0; i < quantidadeMovimentacoes; i++) {
            Funcionario funcionario = sortearFuncionarioPonderado(contexto.funcionarios());
            LocalDateTime data = sortearDataMovimentacao(funcionario, contexto.inicioHistorico(), fimHistorico);
            TipoMovimentacao tipo = sortearTipoMovimentacao(contexto, data);
            double valor = calcularValorMovimentacao(contexto, funcionario, tipo, data);
            String descricao = gerarDescricaoMovimentacao(contexto, funcionario, tipo);

            movimentacoes.add(new Movimentacao(tipo, data, valor, descricao, setor, funcionario));
        }

        return movimentacoes;
    }

    private static ContextoSetor montarContextoSetor(Setor setor, List<Funcionario> funcionarios, LocalDate inicioHistorico, LocalDate fimHistorico) {
        double folhaMensal = funcionarios.stream().mapToDouble(Funcionario::getSalarioBruto).sum();
        double salarioMedio = folhaMensal / funcionarios.size();
        double senioridadeMediaEmMeses = funcionarios.stream()
                .map(Funcionario::getDataAdmissao)
                .filter(dataAdmissao -> dataAdmissao != null)
                .mapToLong(dataAdmissao -> Math.max(1, ChronoUnit.MONTHS.between(YearMonth.from(dataAdmissao), YearMonth.from(fimHistorico))))
                .average()
                .orElse(1.0);
        LocalDate primeiraAdmissao = funcionarios.stream()
                .map(Funcionario::getDataAdmissao)
                .filter(dataAdmissao -> dataAdmissao != null)
                .min(Comparator.naturalOrder())
                .orElse(inicioHistorico);
        LocalDate inicio = primeiraAdmissao.isAfter(inicioHistorico) ? primeiraAdmissao : inicioHistorico;

        return new ContextoSetor(setor, funcionarios, inicio, folhaMensal, salarioMedio, senioridadeMediaEmMeses);
    }

    private static int calcularQuantidadeMovimentacoes(ContextoSetor contexto, LocalDate fimHistorico) {
        long mesesDeHistorico = Math.max(1, ChronoUnit.MONTHS.between(YearMonth.from(contexto.inicioHistorico()), YearMonth.from(fimHistorico)) + 1);
        double intensidadeTemporal = mesesDeHistorico * randomEntre(0.8, 1.4);
        double porteEquipe = contexto.funcionarios().size() * randomEntre(1.4, 2.8);
        double relevanciaFinanceira = Math.max(1.0, contexto.folhaMensal() / Math.max(2200.0, contexto.salarioMedio() * 0.75));
        double senioridade = contexto.senioridadeMediaEmMeses() * 0.12;
        return Math.max(4, (int) Math.round(intensidadeTemporal + porteEquipe + relevanciaFinanceira + senioridade));
    }

    private static Funcionario sortearFuncionarioPonderado(List<Funcionario> funcionarios) {
        double folhaTotal = funcionarios.stream().mapToDouble(Funcionario::getSalarioBruto).sum();
        double alvo = randomEntre(0.0, folhaTotal);
        double acumulado = 0.0;

        for (Funcionario funcionario : funcionarios) {
            acumulado += funcionario.getSalarioBruto();
            if (acumulado >= alvo) {
                return funcionario;
            }
        }

        return funcionarios.get(funcionarios.size() - 1);
    }

    private static LocalDateTime sortearDataMovimentacao(Funcionario funcionario, LocalDate inicioHistorico, LocalDate fimHistorico) {
        LocalDate inicio = funcionario.getDataAdmissao() != null && funcionario.getDataAdmissao().isAfter(inicioHistorico)
                ? funcionario.getDataAdmissao()
                : inicioHistorico;

        long diaInicial = inicio.toEpochDay();
        long diaFinal = fimHistorico.toEpochDay();
        long diaSorteado = ThreadLocalRandom.current().nextLong(diaInicial, diaFinal + 1);
        LocalDate data = LocalDate.ofEpochDay(diaSorteado);

        while (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            data = data.plusDays(1);
            if (data.isAfter(fimHistorico)) {
                data = ajustarDiaUtil(fimHistorico);
            }
        }

        return data.atTime(randomEntre(8, 18), randomEntre(0, 59));
    }

    private static TipoMovimentacao sortearTipoMovimentacao(ContextoSetor contexto, LocalDateTime data) {
        double probabilidadeReceita = 0.32
                + (contexto.funcionarios().size() * 0.015)
                + Math.min(0.12, contexto.senioridadeMediaEmMeses() / 180.0)
                + fatorSazonalReceita(data.getMonthValue());

        return chance(limitar(probabilidadeReceita, 0.25, 0.68)) ? TipoMovimentacao.RECEITA : TipoMovimentacao.DESPESA;
    }

    private static double calcularValorMovimentacao(ContextoSetor contexto, Funcionario funcionario, TipoMovimentacao tipo, LocalDateTime data) {
        double referencia = (funcionario.getSalarioBruto() + contexto.salarioMedio()) / 2.0;
        double fatorEquipe = 1.0 + Math.min(0.45, contexto.funcionarios().size() * 0.035);
        double fatorTempoCasa = 1.0 + Math.min(0.35, mesesDesdeAdmissao(funcionario, data.toLocalDate()) / 120.0);
        double fatorSazonal = 1.0 + fatorSazonalValor(data.getMonthValue(), tipo);
        double faixa = tipo == TipoMovimentacao.RECEITA ? randomEntre(0.55, 2.40) : randomEntre(0.18, 1.45);
        return arredondar(referencia * faixa * fatorEquipe * fatorTempoCasa * fatorSazonal);
    }

    private static String gerarDescricaoMovimentacao(ContextoSetor contexto, Funcionario funcionario, TipoMovimentacao tipo) {
        String[] verbosReceita = { "Recebimento", "Entrada", "Venda", "Faturamento", "Repasse" };
        String[] verbosDespesa = { "Pagamento", "Compra", "Despesa", "Custo", "Reembolso" };
        String[] complementos = {
                faker.company().buzzword(),
                faker.company().industry(),
                faker.commerce().department(),
                faker.commerce().productName()
        };

        String verbo = tipo == TipoMovimentacao.RECEITA
                ? verbosReceita[randomEntre(0, verbosReceita.length - 1)]
                : verbosDespesa[randomEntre(0, verbosDespesa.length - 1)];
        String complemento = complementos[randomEntre(0, complementos.length - 1)];
        String referencia = funcionario.getNomeCompleto().split(" ")[0] + " / " + contexto.setor().getNome();

        return verbo + " de " + complemento.toLowerCase(Locale.ROOT) + " - " + referencia;
    }

    private static long mesesDesdeAdmissao(Funcionario funcionario, LocalDate dataReferencia) {
        if (funcionario.getDataAdmissao() == null) {
            return 1;
        }

        return Math.max(1, ChronoUnit.MONTHS.between(YearMonth.from(funcionario.getDataAdmissao()), YearMonth.from(dataReferencia)));
    }

    private static double fatorSazonalReceita(int mes) {
        return switch (mes) {
            case 3, 6, 9, 12 -> 0.08;
            case 1, 2 -> -0.04;
            default -> 0.02;
        };
    }

    private static double fatorSazonalValor(int mes, TipoMovimentacao tipo) {
        if (tipo == TipoMovimentacao.RECEITA) {
            return switch (mes) {
                case 3, 6, 9, 12 -> 0.18;
                case 1, 2 -> -0.08;
                default -> randomEntre(-0.03, 0.10);
            };
        }

        return switch (mes) {
            case 1, 2 -> -0.05;
            case 7, 12 -> 0.12;
            default -> randomEntre(0.00, 0.08);
        };
    }

    private static double limitar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    private static double gerarSalarioBruto(String formacao) {
        double salarioBase = faker.number().randomDouble(2, 1412, 6000);
        double multiplicador = calcularMultiplicador(formacao);
        return Math.round((salarioBase * multiplicador) * 100.0) / 100.0;
    }

    private static LocalDate gerarDataAdmissao(LocalDate dataNascimento) {
        LocalDate dataMinimaAdmissao = dataNascimento.plusYears(18);
        LocalDate hoje = LocalDate.now();

        if (dataMinimaAdmissao.isAfter(hoje)) {
            return hoje;
        }

        long inicioEpochDay = dataMinimaAdmissao.toEpochDay();
        long fimEpochDay = hoje.toEpochDay();
        long diaSorteado = ThreadLocalRandom.current().nextLong(inicioEpochDay, fimEpochDay + 1);
        return LocalDate.ofEpochDay(diaSorteado);
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

    private static LocalDate ajustarDiaUtil(LocalDate data) {
        DayOfWeek diaDaSemana = data.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SATURDAY) {
            return data.minusDays(1);
        }
        if (diaDaSemana == DayOfWeek.SUNDAY) {
            return data.minusDays(2);
        }
        return data;
    }

    private static boolean chance(double probabilidade) {
        return ThreadLocalRandom.current().nextDouble() < probabilidade;
    }

    private static int randomEntre(int minimo, int maximo) {
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }

    private static double randomEntre(double minimo, double maximo) {
        return ThreadLocalRandom.current().nextDouble(minimo, maximo);
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private record ContextoSetor(
            Setor setor,
            List<Funcionario> funcionarios,
            LocalDate inicioHistorico,
            double folhaMensal,
            double salarioMedio,
            double senioridadeMediaEmMeses) {
    }
}
