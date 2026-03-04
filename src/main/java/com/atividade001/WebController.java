package com.atividade001;

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
        return "index";
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
