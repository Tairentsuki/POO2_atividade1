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

/**
 * Gera dados ficticios de funcionarios e movimentacoes financeiras.
 * Usa locale pt-BR para produzir textos mais naturais em portugues.
 */
public class DataFakerGenerator {
    private static final Faker FAKER = new Faker(Locale.forLanguageTag("pt-BR"));
    private static final String[] NOMES_SETORES_PADRAO = { "TI", "Marketing", "RH", "Vendas", "Financeiro" };

    private static int proximoIdFuncionario = 1;
    private static int proximoIdSetor = 1;

    /**
     * Construtor privado para evitar instancia.
     */
    private DataFakerGenerator() {
    }

    /**
     * Gera um funcionario completo, com IDs no funcionario e no setor.
     *
     * @return funcionario gerado para cenarios nao persistiveis
     */
    public static Funcionario gerarFuncionario() {
        return criarFuncionarioAleatorio(true);
    }

    /**
     * Gera um funcionario pronto para persistir no banco (sem IDs manuais).
     *
     * @return funcionario gerado para persistencia
     */
    public static Funcionario gerarFuncionarioPersistivel() {
        return criarFuncionarioAleatorio(false);
    }

    /**
     * Gera movimentacoes para todos os setores presentes na lista.
     *
     * @param funcionarios funcionarios ja existentes
     * @return lista de movimentacoes ordenada da mais recente para a mais antiga
     */
    public static List<Movimentacao> gerarMovimentacoesPersistiveis(List<Funcionario> funcionarios) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        if (funcionarios == null || funcionarios.isEmpty()) {
            return movimentacoes;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate inicioDoHistorico = hoje.minusMonths(14).withDayOfMonth(1);

        Map<Setor, List<Funcionario>> funcionariosPorSetor = funcionarios.stream()
                .filter(funcionario -> funcionario.getSetor() != null)
                .collect(Collectors.groupingBy(Funcionario::getSetor));

        for (Map.Entry<Setor, List<Funcionario>> grupoSetor : funcionariosPorSetor.entrySet()) {
            Setor setor = grupoSetor.getKey();
            List<Funcionario> equipe = grupoSetor.getValue();
            movimentacoes.addAll(gerarMovimentacoesParaSetor(setor, equipe, inicioDoHistorico, hoje));
        }

        movimentacoes.sort((a, b) -> b.getData().compareTo(a.getData()));
        return movimentacoes;
    }

    /**
     * Monta um funcionario com dados aleatorios.
     *
     * @param incluirIds true para criar IDs manuais, false para deixar o banco gerar
     * @return funcionario com setor e dados pessoais preenchidos
     */
    private static Funcionario criarFuncionarioAleatorio(boolean incluirIds) {
        String areaProfissional = FAKER.job().field();
        String cargo = FAKER.job().position();
        String formacao = cargo + " de " + areaProfissional;
        String nomeSetor = sortearNomeSetor();
        double salarioBruto = gerarSalarioBruto(formacao);

        boolean sexoMasculino = FAKER.bool().bool();
        String primeiroNome = sexoMasculino ? FAKER.name().maleFirstName() : FAKER.name().femaleFirstName();
        String nomeCompleto = primeiroNome + " " + FAKER.name().lastName();
        LocalDate dataNascimento = FAKER.timeAndDate().birthday(18, 65);
        String sexo = sexoMasculino ? "Masculino" : "Feminino";
        String cpf = FAKER.cpf().valid();
        LocalDate dataAdmissao = gerarDataAdmissaoValida(dataNascimento);

        Setor setorGerado;
        if (incluirIds) {
            int idSetor = proximoIdSetor++;
            String ramal = String.valueOf(idSetor % 9000 + 1000);
            setorGerado = new Setor(idSetor, nomeSetor, ramal);
        } else {
            String ramal = String.valueOf(FAKER.number().numberBetween(1000, 10000));
            setorGerado = new Setor(nomeSetor, ramal);
        }

        if (incluirIds) {
            int idFuncionario = proximoIdFuncionario++;
            return new Funcionario(idFuncionario, nomeCompleto, cpf, dataNascimento, sexo, salarioBruto, formacao,
                    setorGerado, dataAdmissao);
        }

        return new Funcionario(nomeCompleto, cpf, dataNascimento, sexo, salarioBruto, formacao, setorGerado,
                dataAdmissao);
    }

    /**
     * Gera movimentacoes para um setor especifico.
     *
     * @param setor setor de referencia das movimentacoes
     * @param equipe funcionarios do setor
     * @param inicioHistorico data inicial permitida
     * @param fimHistorico data final permitida
     * @return lista de movimentacoes do setor
     */
    private static List<Movimentacao> gerarMovimentacoesParaSetor(
            Setor setor,
            List<Funcionario> equipe,
            LocalDate inicioHistorico,
            LocalDate fimHistorico) {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        if (equipe == null || equipe.isEmpty()) {
            return movimentacoes;
        }

        ContextoSetor contexto = criarContextoDoSetor(setor, equipe, inicioHistorico, fimHistorico);
        int quantidadeMovimentacoes = calcularQuantidadeMovimentacoes(contexto, fimHistorico);

        for (int i = 0; i < quantidadeMovimentacoes; i++) {
            Funcionario funcionario = selecionarFuncionarioPorPesoSalarial(contexto.equipe());
            LocalDateTime data = gerarDataUtilDaMovimentacao(funcionario, contexto.inicioHistorico(), fimHistorico);
            TipoMovimentacao tipo = escolherTipoMovimentacao(contexto, data);
            double valor = calcularValorMovimentacao(contexto, funcionario, tipo, data);
            String descricao = montarDescricaoMovimentacao(contexto, funcionario, tipo);

            movimentacoes.add(new Movimentacao(tipo, data, valor, descricao, setor, funcionario));
        }

        return movimentacoes;
    }

    /**
     * Calcula metricas do setor para apoiar a geracao das movimentacoes.
     *
     * @param setor setor analisado
     * @param equipe funcionarios do setor
     * @param inicioHistorico data minima do historico
     * @param fimHistorico data maxima do historico
     * @return contexto com metricas da equipe
     */
    private static ContextoSetor criarContextoDoSetor(
            Setor setor,
            List<Funcionario> equipe,
            LocalDate inicioHistorico,
            LocalDate fimHistorico) {
        double folhaMensal = equipe.stream().mapToDouble(Funcionario::getSalarioBruto).sum();
        double salarioMedio = folhaMensal / equipe.size();
        double senioridadeMediaMeses = equipe.stream()
                .map(Funcionario::getDataAdmissao)
                .filter(dataAdmissao -> dataAdmissao != null)
                .mapToLong(dataAdmissao -> Math.max(1,
                        ChronoUnit.MONTHS.between(YearMonth.from(dataAdmissao), YearMonth.from(fimHistorico))))
                .average()
                .orElse(1.0);
        LocalDate primeiraAdmissao = equipe.stream()
                .map(Funcionario::getDataAdmissao)
                .filter(dataAdmissao -> dataAdmissao != null)
                .min(Comparator.naturalOrder())
                .orElse(inicioHistorico);
        LocalDate inicioEfetivo = primeiraAdmissao.isAfter(inicioHistorico) ? primeiraAdmissao : inicioHistorico;

        return new ContextoSetor(setor, equipe, inicioEfetivo, folhaMensal, salarioMedio, senioridadeMediaMeses);
    }

    /**
     * Define quantas movimentacoes serao geradas para o setor.
     *
     * @param contexto metricas do setor
     * @param fimHistorico limite final de datas
     * @return quantidade total de movimentacoes
     */
    private static int calcularQuantidadeMovimentacoes(ContextoSetor contexto, LocalDate fimHistorico) {
        long mesesDeHistorico = Math.max(1,
                ChronoUnit.MONTHS.between(YearMonth.from(contexto.inicioHistorico()), YearMonth.from(fimHistorico)) + 1);
        double intensidadeTemporal = mesesDeHistorico * sortearDecimalEntre(0.8, 1.4);
        double porteEquipe = contexto.equipe().size() * sortearDecimalEntre(1.4, 2.8);
        double relevanciaFinanceira = Math.max(1.0, contexto.folhaMensal() / Math.max(2200.0, contexto.salarioMedio() * 0.75));
        double senioridade = contexto.senioridadeMediaMeses() * 0.12;
        return Math.max(4, (int) Math.round(intensidadeTemporal + porteEquipe + relevanciaFinanceira + senioridade));
    }

    /**
     * Escolhe funcionario com peso proporcional ao salario.
     *
     * @param equipe funcionarios disponiveis no setor
     * @return funcionario sorteado
     */
    private static Funcionario selecionarFuncionarioPorPesoSalarial(List<Funcionario> equipe) {
        double folhaTotal = equipe.stream().mapToDouble(Funcionario::getSalarioBruto).sum();
        double alvo = sortearDecimalEntre(0.0, folhaTotal);
        double acumulado = 0.0;

        for (Funcionario funcionario : equipe) {
            acumulado += funcionario.getSalarioBruto();
            if (acumulado >= alvo) {
                return funcionario;
            }
        }

        return equipe.get(equipe.size() - 1);
    }

    /**
     * Sorteia uma data e hora util para a movimentacao.
     *
     * @param funcionario funcionario relacionado
     * @param inicioHistorico data inicial permitida
     * @param fimHistorico data final permitida
     * @return data e hora da movimentacao
     */
    private static LocalDateTime gerarDataUtilDaMovimentacao(Funcionario funcionario, LocalDate inicioHistorico,
            LocalDate fimHistorico) {
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

        return data.atTime(sortearInteiroEntre(8, 18), sortearInteiroEntre(0, 59));
    }

    /**
     * Define se a movimentacao sera receita ou despesa.
     *
     * @param contexto metricas do setor
     * @param data data da movimentacao
     * @return tipo da movimentacao
     */
    private static TipoMovimentacao escolherTipoMovimentacao(ContextoSetor contexto, LocalDateTime data) {
        double probabilidadeReceita = 0.32
                + (contexto.equipe().size() * 0.015)
                + Math.min(0.12, contexto.senioridadeMediaMeses() / 180.0)
                + obterFatorSazonalReceita(data.getMonthValue());

        return sortearPorProbabilidade(limitarValor(probabilidadeReceita, 0.25, 0.68))
                ? TipoMovimentacao.RECEITA
                : TipoMovimentacao.DESPESA;
    }

    /**
     * Calcula o valor da movimentacao com base em salario, equipe e sazonalidade.
     *
     * @param contexto metricas do setor
     * @param funcionario funcionario relacionado
     * @param tipo tipo da movimentacao
     * @param data data da movimentacao
     * @return valor final arredondado com duas casas
     */
    private static double calcularValorMovimentacao(ContextoSetor contexto, Funcionario funcionario, TipoMovimentacao tipo,
            LocalDateTime data) {
        double baseReferencia = (funcionario.getSalarioBruto() + contexto.salarioMedio()) / 2.0;
        double fatorEquipe = 1.0 + Math.min(0.45, contexto.equipe().size() * 0.035);
        double fatorTempoCasa = 1.0
                + Math.min(0.35, calcularMesesDesdeAdmissao(funcionario, data.toLocalDate()) / 120.0);
        double fatorSazonal = 1.0 + obterFatorSazonalValor(data.getMonthValue(), tipo);
        double faixaTipo = tipo == TipoMovimentacao.RECEITA ? sortearDecimalEntre(0.55, 2.40)
                : sortearDecimalEntre(0.18, 1.45);
        return arredondarDuasCasas(baseReferencia * faixaTipo * fatorEquipe * fatorTempoCasa * fatorSazonal);
    }

    /**
     * Monta uma descricao curta para a movimentacao.
     *
     * @param contexto setor da movimentacao
     * @param funcionario funcionario relacionado
     * @param tipo tipo da movimentacao
     * @return descricao em texto simples
     */
    private static String montarDescricaoMovimentacao(ContextoSetor contexto, Funcionario funcionario,
            TipoMovimentacao tipo) {
        String[] verbosReceita = { "Recebimento", "Entrada", "Venda", "Faturamento", "Repasse" };
        String[] verbosDespesa = { "Pagamento", "Compra", "Despesa", "Custo", "Reembolso" };
        String[] complementos = {
                FAKER.company().buzzword(),
                FAKER.company().industry(),
                FAKER.commerce().department(),
                FAKER.commerce().productName()
        };

        String verbo = tipo == TipoMovimentacao.RECEITA
                ? verbosReceita[sortearInteiroEntre(0, verbosReceita.length - 1)]
                : verbosDespesa[sortearInteiroEntre(0, verbosDespesa.length - 1)];
        String complemento = complementos[sortearInteiroEntre(0, complementos.length - 1)];
        String referencia = funcionario.getNomeCompleto().split(" ")[0] + " / " + contexto.setor().getNome();

        return verbo + " de " + complemento.toLowerCase(Locale.ROOT) + " - " + referencia;
    }

    /**
     * Calcula meses de casa do funcionario em relacao a uma data.
     *
     * @param funcionario funcionario analisado
     * @param dataReferencia data final do calculo
     * @return quantidade de meses (minimo de 1)
     */
    private static long calcularMesesDesdeAdmissao(Funcionario funcionario, LocalDate dataReferencia) {
        if (funcionario.getDataAdmissao() == null) {
            return 1;
        }

        return Math.max(1,
                ChronoUnit.MONTHS.between(YearMonth.from(funcionario.getDataAdmissao()), YearMonth.from(dataReferencia)));
    }

    /**
     * Retorna ajuste sazonal para probabilidade de receita.
     *
     * @param mes mes de referencia (1 a 12)
     * @return fator adicional de probabilidade
     */
    private static double obterFatorSazonalReceita(int mes) {
        return switch (mes) {
            case 3, 6, 9, 12 -> 0.08;
            case 1, 2 -> -0.04;
            default -> 0.02;
        };
    }

    /**
     * Retorna ajuste sazonal aplicado ao valor.
     *
     * @param mes mes de referencia (1 a 12)
     * @param tipo tipo da movimentacao
     * @return fator de ajuste do valor
     */
    private static double obterFatorSazonalValor(int mes, TipoMovimentacao tipo) {
        if (tipo == TipoMovimentacao.RECEITA) {
            return switch (mes) {
                case 3, 6, 9, 12 -> 0.18;
                case 1, 2 -> -0.08;
                default -> sortearDecimalEntre(-0.03, 0.10);
            };
        }

        return switch (mes) {
            case 1, 2 -> -0.05;
            case 7, 12 -> 0.12;
            default -> sortearDecimalEntre(0.00, 0.08);
        };
    }

    /**
     * Limita um valor entre minimo e maximo.
     *
     * @param valor valor original
     * @param minimo menor valor permitido
     * @param maximo maior valor permitido
     * @return valor ajustado para a faixa permitida
     */
    private static double limitarValor(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    /**
     * Gera salario bruto com base no cargo/formacao.
     *
     * @param formacao descricao da formacao/cargo
     * @return salario bruto com duas casas decimais
     */
    private static double gerarSalarioBruto(String formacao) {
        double salarioBase = FAKER.number().randomDouble(2, 1412, 6000);
        double multiplicador = calcularMultiplicadorPorCargo(formacao);
        return Math.round((salarioBase * multiplicador) * 100.0) / 100.0;
    }

    /**
     * Sorteia nome de setor com distribuicao equilibrada.
     *
     * @return nome do setor escolhido
     */
    private static String sortearNomeSetor() {
        int faixa = sortearInteiroEntre(0, 99);
        if (faixa < 20) {
            return NOMES_SETORES_PADRAO[0];
        }
        if (faixa < 40) {
            return NOMES_SETORES_PADRAO[1];
        }
        if (faixa < 60) {
            return NOMES_SETORES_PADRAO[2];
        }
        if (faixa < 80) {
            return NOMES_SETORES_PADRAO[3];
        }
        return NOMES_SETORES_PADRAO[4];
    }

    /**
     * Gera data de admissao valida, sempre apos 18 anos.
     *
     * @param dataNascimento data de nascimento
     * @return data de admissao dentro da faixa permitida
     */
    private static LocalDate gerarDataAdmissaoValida(LocalDate dataNascimento) {
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

    /**
     * Define um multiplicador salarial baseado em palavras do cargo.
     *
     * @param formacao texto da formacao/cargo
     * @return multiplicador usado no salario base
     */
    private static double calcularMultiplicadorPorCargo(String formacao) {
        double multiplicador = 1.0;
        String formacaoLower = formacao.toLowerCase(Locale.ROOT);

        if (formacaoLower.contains("chief") || formacaoLower.contains("director") || formacaoLower.contains("diretor")) {
            multiplicador = FAKER.number().randomDouble(2, 6, 8);
        } else if (formacaoLower.contains("manager") || formacaoLower.contains("lead") || formacaoLower.contains("gerente")) {
            multiplicador = FAKER.number().randomDouble(2, 4, 6);
        } else if (formacaoLower.contains("senior") || formacaoLower.contains("principal")) {
            multiplicador = FAKER.number().randomDouble(2, 2, 4);
        } else if (formacaoLower.contains("assistant") || formacaoLower.contains("assistente") || formacaoLower.contains("auxiliar")) {
            multiplicador = FAKER.number().randomDouble(2, 1, 2);
        }

        return multiplicador;
    }

    /**
     * Ajusta fim de semana para o dia util anterior.
     *
     * @param data data original
     * @return data util correspondente
     */
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

    /**
     * Retorna true quando o sorteio cai dentro da probabilidade.
     *
     * @param probabilidade valor entre 0 e 1
     * @return true quando evento ocorre
     */
    private static boolean sortearPorProbabilidade(double probabilidade) {
        return ThreadLocalRandom.current().nextDouble() < probabilidade;
    }

    /**
     * Sorteia numero inteiro, incluindo minimo e maximo.
     *
     * @param minimo menor valor permitido
     * @param maximo maior valor permitido
     * @return inteiro sorteado
     */
    private static int sortearInteiroEntre(int minimo, int maximo) {
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }

    /**
     * Sorteia numero decimal no intervalo [minimo, maximo).
     *
     * @param minimo menor valor permitido
     * @param maximo maior valor permitido
     * @return decimal sorteado
     */
    private static double sortearDecimalEntre(double minimo, double maximo) {
        return ThreadLocalRandom.current().nextDouble(minimo, maximo);
    }

    /**
     * Arredonda um valor para duas casas decimais.
     *
     * @param valor valor original
     * @return valor arredondado
     */
    private static double arredondarDuasCasas(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * Agrupa os dados calculados de um setor para gerar movimentacoes.
     *
     * @param setor setor de referencia
     * @param equipe funcionarios do setor
     * @param inicioHistorico inicio efetivo para gerar datas
     * @param folhaMensal soma salarial da equipe
     * @param salarioMedio media salarial da equipe
     * @param senioridadeMediaMeses media de tempo de casa em meses
     */
    private record ContextoSetor(
            Setor setor,
            List<Funcionario> equipe,
            LocalDate inicioHistorico,
            double folhaMensal,
            double salarioMedio,
            double senioridadeMediaMeses) {
    }
}
