package com.atividade001;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.atividade001.model.Funcionario;
import com.atividade001.model.Movimentacao;
import com.atividade001.model.Setor;
import com.atividade001.repository.FuncionarioRepository;
import com.atividade001.repository.MovimentacaoRepository;
import com.atividade001.repository.SetorRepository;
import com.atividade001.services.DataFakerGenerator;
import com.atividade001.services.GeradorCsv;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        long quantidade = 1_000_0L;
        String nomeArquivo = "funcionarios.csv";

        if (args.length >= 1) {
            try {
                quantidade = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Quantidade inválida: " + args[0]);
                return;
            }
        }

        if (args.length >= 2) {
            nomeArquivo = args[1];
        }

        GeradorCsv.exportarFuncionarios(quantidade, nomeArquivo);
    }

    @Bean
    public CommandLineRunner seedDatabase(
            FuncionarioRepository funcionarioRepository,
            MovimentacaoRepository movimentacaoRepository,
            SetorRepository setorRepository,
            JdbcTemplate jdbcTemplate) {
        return args -> {
            limparBanco(jdbcTemplate);

            List<Funcionario> funcionarios = new ArrayList<>();
            Map<String, Setor> setoresPorNome = new HashMap<>();
            for (int contador = 0; contador < 100; contador++) {
                Funcionario funcionario = DataFakerGenerator.gerarFuncionarioPersistivel();
                Setor setor = obterOuCriarSetor(funcionario.getSetor(), setoresPorNome, setorRepository);
                funcionario.setSetor(setor);
                funcionarios.add(funcionarioRepository.save(funcionario));
            }

            List<Movimentacao> movimentacoes = DataFakerGenerator.gerarMovimentacoesPersistiveis(funcionarios);
            movimentacaoRepository.saveAll(movimentacoes);

            System.out.println(
                    "Banco inicializado com " + funcionarioRepository.count() + " funcionários e "
                            + movimentacaoRepository.count() + " movimentações.");
        };
    }

    private void limparBanco(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("DELETE FROM movimentacao");
        jdbcTemplate.update("DELETE FROM funcionario");
        jdbcTemplate.update("DELETE FROM setor");
        jdbcTemplate.update("ALTER TABLE movimentacao AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE funcionario AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE setor AUTO_INCREMENT = 1");
    }

    private Setor obterOuCriarSetor(Setor setorGerado, Map<String, Setor> setoresPorNome, SetorRepository setorRepository) {
        if (setorGerado == null || setorGerado.getNome() == null) {
            return null;
        }

        String chave = setorGerado.getNome().trim().toLowerCase();
        if (setoresPorNome.containsKey(chave)) {
            return setoresPorNome.get(chave);
        }

        Setor setor = setorRepository.findByNomeIgnoreCase(setorGerado.getNome())
                .orElseGet(() -> setorRepository.save(new Setor(setorGerado.getNome(), setorGerado.getRamal())));

        setoresPorNome.put(chave, setor);
        return setor;
    }
}
