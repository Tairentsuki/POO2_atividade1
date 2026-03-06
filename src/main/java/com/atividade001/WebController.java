package com.atividade001;

import java.time.*;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.atividade001.model.*;
import com.atividade001.repository.*;

/**
 * Controlador principal das páginas HTML do sistema.
 */
@Controller
public class WebController {

    private final FuncionarioRepository funcionarioRepository;
    private final SetorRepository setorRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    /**
     * Construtor com os repositórios usados nas telas.
     *
     * @param funcionarioRepository repositório de funcionários
     * @param setorRepository repositório de setores
     * @param movimentacaoRepository repositório de movimentações
     */
    public WebController(
            FuncionarioRepository funcionarioRepository,
            SetorRepository setorRepository,
            MovimentacaoRepository movimentacaoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.setorRepository = setorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Exibe a página inicial com os gráficos.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping({ "/", "/index", "/graficos" })
    public String index(Model model) {
        model.addAttribute("totalFuncionarios", funcionarioRepository.count());
        model.addAttribute("totalSetores", setorRepository.count());
        model.addAttribute("totalMovimentacoes", movimentacaoRepository.count());

        List<List<Object>> setoresChartData = new ArrayList<>();
        List<List<Object>> salariosChartData = new ArrayList<>();
        List<List<Object>> admissoesChartData = new ArrayList<>();
        List<List<Object>> generoChartData = new ArrayList<>();
        List<List<Object>> financeiroChartData = new ArrayList<>();

        try {
            for (Object[] linha : funcionarioRepository.countFuncionariosPorSetor()) {
                String setor = linha[0] != null ? linha[0].toString() : "Sem setor";
                long quantidade = linha[1] instanceof Number ? ((Number) linha[1]).longValue() : 0L;
                List<Object> item = new ArrayList<>();
                item.add(setor);
                item.add(quantidade);
                setoresChartData.add(item);
            }
        } catch (RuntimeException ex) {
            setoresChartData.add(List.of("Sem dados", 0));
        }

        try {
            for (Object[] linha : funcionarioRepository.avgSalarioBrutoPorSetor()) {
                String setor = linha[0] != null ? linha[0].toString() : "Sem setor";
                double salarioMedio = linha[1] instanceof Number ? ((Number) linha[1]).doubleValue() : 0.0;
                salariosChartData.add(List.of(setor, salarioMedio));
            }
        } catch (RuntimeException ex) {
            salariosChartData.add(List.of("Sem dados", 0.0));
        }

        try {
            for (Object[] linha : funcionarioRepository.countAdmissoesPorAno()) {
                String ano = linha[0] != null ? linha[0].toString() : "Sem ano";
                long quantidade = linha[1] instanceof Number ? ((Number) linha[1]).longValue() : 0L;
                admissoesChartData.add(List.of(ano, quantidade));
            }
        } catch (RuntimeException ex) {
            admissoesChartData.add(List.of("Sem dados", 0));
        }

        try {
            for (Object[] linha : funcionarioRepository.countFuncionariosPorSexo()) {
                String sexo = linha[0] != null ? linha[0].toString() : "Nao informado";
                long quantidade = linha[1] instanceof Number ? ((Number) linha[1]).longValue() : 0L;
                generoChartData.add(List.of(sexo, quantidade));
            }
        } catch (RuntimeException ex) {
            generoChartData.add(List.of("Sem dados", 0));
        }

        try {
            for (Object[] linha : movimentacaoRepository.sumMovimentacoesPorSetor()) {
                String setor = linha[0] != null ? linha[0].toString() : "Sem setor";
                double receitas = linha[1] instanceof Number ? ((Number) linha[1]).doubleValue() : 0.0;
                double despesas = linha[2] instanceof Number ? ((Number) linha[2]).doubleValue() : 0.0;
                financeiroChartData.add(List.of(setor, receitas, despesas));
            }
        } catch (RuntimeException ex) {
            financeiroChartData.add(List.of("Sem dados", 0.0, 0.0));
        }

        model.addAttribute("setoresChartData", setoresChartData);
        model.addAttribute("salariosChartData", salariosChartData);
        model.addAttribute("admissoesChartData", admissoesChartData);
        model.addAttribute("generoChartData", generoChartData);
        model.addAttribute("financeiroChartData", financeiroChartData);
        return "index";
    }

    /**
     * Exibe a tela de estatísticas.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping({ "/estatistica" })
    public String estatistica(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        LocalDate hoje = LocalDate.now();

        List<Double> salarios = funcionarios.stream()
                .map(Funcionario::getSalarioBruto)
                .toList();

        List<Double> idades = funcionarios.stream()
                .map(Funcionario::getDataDeNascimento)
                .filter(Objects::nonNull)
                .map(dataNascimento -> (double) Period.between(dataNascimento, hoje).getYears())
                .toList();

        EstatisticaResumo resumoSalario = calcularResumo(salarios);
        EstatisticaResumo resumoIdade = calcularResumo(idades);

        model.addAttribute("totalFuncionarios", funcionarios.size());
        model.addAttribute("funcionariosAtivos", funcionarios.size());
        model.addAttribute("totalSetores", setorRepository.count());
        model.addAttribute("totalMovimentacoes", movimentacaoRepository.count());

        model.addAttribute("salarioMedia", resumoSalario.media());
        model.addAttribute("salarioMediana", resumoSalario.mediana());
        model.addAttribute("salarioModa", resumoSalario.moda());
        model.addAttribute("salarioDesvioPadrao", resumoSalario.desvioPadrao());
        model.addAttribute("salarioVariancia", resumoSalario.variancia());
        model.addAttribute("salarioMinimo", resumoSalario.minimo());
        model.addAttribute("salarioMaximo", resumoSalario.maximo());
        model.addAttribute("folhaTotal", resumoSalario.total());

        model.addAttribute("idadeMedia", resumoIdade.media());
        model.addAttribute("idadeMediana", resumoIdade.mediana());
        model.addAttribute("idadeModa", resumoIdade.moda());
        model.addAttribute("idadeDesvioPadrao", resumoIdade.desvioPadrao());
        model.addAttribute("idadeVariancia", resumoIdade.variancia());
        model.addAttribute("idadeMinima", resumoIdade.minimo());
        model.addAttribute("idadeMaxima", resumoIdade.maximo());
        model.addAttribute("idadeAmplitude", resumoIdade.amplitude());

        return "estatistica";
    }


    /**
     * Exibe a listagem de funcionários.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping("/funcionarios")
    public String listFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAllComSetorOrdenados();
        model.addAttribute("funcionarios", funcionarios);
        model.addAttribute("totalFuncionarios", funcionarios.size());
        return "list_func";
    }

    /**
     * Exibe a listagem de pessoas.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping("/pessoas")
    public String listPessoas(Model model) {
        List<Funcionario> pessoas = funcionarioRepository.findAllComSetorOrdenados();
        model.addAttribute("pessoas", pessoas);
        model.addAttribute("totalPessoas", pessoas.size());
        return "list_pessoas";
    }

    /**
     * Exibe a listagem de setores.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping({ "/setor", "/setores" })
    public String listSetores(Model model) {
        List<Setor> setores = setorRepository.findAllByOrderByNomeAsc();
        model.addAttribute("setores", setores);
        model.addAttribute("totalSetores", setores.size());
        return "list_setor";
    }

    /**
     * Exibe a listagem de movimentações.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping("/movimentacoes")
    public String listMovimentacoes(Model model) {
        List<Movimentacao> movimentacoes = movimentacaoRepository.findAllByOrderByDataDescIdDesc();
        model.addAttribute("movimentacoes", movimentacoes);
        model.addAttribute("totalMovimentacoes", movimentacoes.size());
        return "list_movimentacoes";
    }

    /**
     * Exibe a página de exemplo.
     *
     * @param model objeto de modelo do Spring MVC
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping({ "/exemplo", "/exemplo.html" })
    public String exemplo(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAllComSetorOrdenados();
        model.addAttribute("funcionarios", funcionarios);
        model.addAttribute("totalFuncionarios", funcionarios.size());
        return "exemplo";
    }

    /**
     * Exibe a homepage simples.
     *
     * @return nome do arquivo HTML a ser renderizado
     */
    @GetMapping("/homepage")
    public String homepage() {
        return "homepage";
    }

    /**
     * Calcula resumo estatístico básico para uma lista numérica.
     *
     * @param valoresEntrada lista de valores
     * @return objeto com média, mediana, moda e outros indicadores
     */
    private EstatisticaResumo calcularResumo(List<Double> valoresEntrada) {
        if (valoresEntrada == null || valoresEntrada.isEmpty()) {
            return new EstatisticaResumo(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }

        List<Double> valores = new ArrayList<>(valoresEntrada);
        Collections.sort(valores);

        int n = valores.size();
        double soma = valores.stream().mapToDouble(Double::doubleValue).sum();
        double media = soma / n;
        double mediana = (n % 2 == 0)
                ? (valores.get(n / 2 - 1) + valores.get(n / 2)) / 2.0
                : valores.get(n / 2);

        Map<Double, Integer> frequencias = new HashMap<>();
        for (Double valor : valores) {
            double chave = arredondar(valor, 2);
            frequencias.merge(chave, 1, Integer::sum);
        }

        int maiorFrequencia = frequencias.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        double moda = mediana;
        if (maiorFrequencia > 1) {
            moda = frequencias.entrySet().stream()
                    .filter(entry -> entry.getValue() == maiorFrequencia)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .findFirst()
                    .orElse(mediana);
        }

        double variancia = valores.stream()
                .mapToDouble(valor -> Math.pow(valor - media, 2))
                .sum() / n;
        double desvioPadrao = Math.sqrt(variancia);

        double minimo = valores.get(0);
        double maximo = valores.get(n - 1);
        double amplitude = maximo - minimo;

        return new EstatisticaResumo(media, mediana, moda, desvioPadrao, variancia, minimo, maximo, soma, amplitude);
    }

    /**
     * Arredonda um valor com a quantidade de casas desejada.
     *
     * @param valor número de entrada
     * @param casas quantidade de casas decimais
     * @return valor arredondado
     */
    private double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }

    private record EstatisticaResumo(
            double media,
            double mediana,
            double moda,
            double desvioPadrao,
            double variancia,
            double minimo,
            double maximo,
            double total,
            double amplitude) {
    }
}
