package com.atividade001;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.atividade001.model.Funcionario;
import com.atividade001.repository.FuncionarioRepository;

@Controller
public class WebController {

    private final FuncionarioRepository funcionarioRepo;

    public WebController(FuncionarioRepository funcionarioRepo) {
        this.funcionarioRepo = funcionarioRepo;
    }

    @GetMapping({ "/", "/index" })
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/funcionarios")
    public String listFuncionarios(Model model) {
        adicionarFuncionariosAoModelo(model);
        return "list_func";
    }

    @GetMapping("/setor")
        public String listSetor(Model setor) {
            adicionarFuncionariosAoModelo(setor);
            return "list_setor";
        }

    @GetMapping({ "/exemplo", "/exemplo.html" })
    public String exemplo(Model model) {
        adicionarFuncionariosAoModelo(model);
        return "exemplo";
    }

    private void adicionarFuncionariosAoModelo(Model model) {
        List<Funcionario> funcionarios = funcionarioRepo.findAllComSetorOrdenados();
        model.addAttribute("funcionarios", funcionarios);
        model.addAttribute("totalFuncionarios", funcionarios.size());
    }
}
