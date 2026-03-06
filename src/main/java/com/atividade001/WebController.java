package com.atividade001;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.atividade001.model.Funcionario;
import com.atividade001.model.Movimentacao;
import com.atividade001.model.Setor;
import com.atividade001.repository.FuncionarioRepository;
import com.atividade001.repository.MovimentacaoRepository;
import com.atividade001.repository.SetorRepository;

@Controller
public class WebController {

    private final FuncionarioRepository funcionarioRepository;
    private final SetorRepository setorRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public WebController(
            FuncionarioRepository funcionarioRepository,
            SetorRepository setorRepository,
            MovimentacaoRepository movimentacaoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.setorRepository = setorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

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
    @GetMapping({ "/estatistica" })
    public String estatistica(Model model) {
        model.addAttribute("totalFuncionarios", funcionarioRepository.count());
        model.addAttribute("totalSetores", setorRepository.count());
        model.addAttribute("totalMovimentacoes", movimentacaoRepository.count());
        return "estatistica";
    }


    @GetMapping("/funcionarios")
    public String listFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAllComSetorOrdenados();
        model.addAttribute("funcionarios", funcionarios);
        model.addAttribute("totalFuncionarios", funcionarios.size());
        return "list_func";
    }

    @GetMapping("/pessoas")
    public String listPessoas(Model model) {
        List<Funcionario> pessoas = funcionarioRepository.findAllComSetorOrdenados();
        model.addAttribute("pessoas", pessoas);
        model.addAttribute("totalPessoas", pessoas.size());
        return "list_pessoas";
    }

    @GetMapping({ "/setor", "/setores" })
    public String listSetores(Model model) {
        List<Setor> setores = setorRepository.findAllByOrderByNomeAsc();
        model.addAttribute("setores", setores);
        model.addAttribute("totalSetores", setores.size());
        return "list_setor";
    }

    @GetMapping("/movimentacoes")
    public String listMovimentacoes(Model model) {
        List<Movimentacao> movimentacoes = movimentacaoRepository.findAllByOrderByDataDescIdDesc();
        model.addAttribute("movimentacoes", movimentacoes);
        model.addAttribute("totalMovimentacoes", movimentacoes.size());
        return "list_movimentacoes";
    }

    @GetMapping({ "/exemplo", "/exemplo.html" })
    public String exemplo(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAllComSetorOrdenados();
        model.addAttribute("funcionarios", funcionarios);
        model.addAttribute("totalFuncionarios", funcionarios.size());
        return "exemplo";
    }

    @GetMapping("/homepage")
    public String homepage() {
        return "homepage";
    }
}
