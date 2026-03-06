package com.atividade001.services;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.atividade001.model.*;
import com.atividade001.repository.*;

/**
 * Centraliza operacoes de limpar banco e gerar dados ficticios.
 */
@Service
public class DatabaseSeedService {
    private final FuncionarioRepository funcionarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final SetorRepository setorRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Cria o servico com os componentes necessarios.
     *
     * @param funcionarioRepository repositorio de funcionarios
     * @param movimentacaoRepository repositorio de movimentacoes
     * @param setorRepository repositorio de setores
     * @param jdbcTemplate acesso SQL para limpeza direta
     */
    public DatabaseSeedService(
            FuncionarioRepository funcionarioRepository,
            MovimentacaoRepository movimentacaoRepository,
            SetorRepository setorRepository,
            JdbcTemplate jdbcTemplate) {
        this.funcionarioRepository = funcionarioRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.setorRepository = setorRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Remove todos os dados do banco e reinicia os IDs.
     *
     * @return sem retorno
     */
    public void limparBanco() {
        jdbcTemplate.update("DELETE FROM movimentacao");
        jdbcTemplate.update("DELETE FROM funcionario");
        jdbcTemplate.update("DELETE FROM setor");
        jdbcTemplate.update("ALTER TABLE movimentacao AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE funcionario AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE setor AUTO_INCREMENT = 1");
    }

    /**
     * Gera uma quantidade de funcionarios e suas movimentacoes.
     *
     * @param quantidadeFuncionarios total de funcionarios que devem ser criados
     * @return resumo com totais gerados
     */
    public ResultadoGeracao gerarDados(int quantidadeFuncionarios) {
        if (quantidadeFuncionarios <= 0) {
            return new ResultadoGeracao(0, 0);
        }

        List<Funcionario> funcionarios = new ArrayList<>();
        Map<String, Setor> setoresPorNome = new HashMap<>();

        for (int contador = 0; contador < quantidadeFuncionarios; contador++) {
            Funcionario funcionario = DataFakerGenerator.gerarFuncionarioPersistivel();
            Setor setor = obterOuCriarSetor(funcionario.getSetor(), setoresPorNome);
            funcionario.setSetor(setor);
            funcionarios.add(funcionarioRepository.save(funcionario));
        }

        List<Movimentacao> movimentacoes = DataFakerGenerator.gerarMovimentacoesPersistiveis(funcionarios);
        movimentacaoRepository.saveAll(movimentacoes);

        return new ResultadoGeracao(funcionarios.size(), movimentacoes.size());
    }

    /**
     * Busca ou cria um setor pelo nome.
     *
     * @param setorGerado setor vindo do DataFaker
     * @param setoresPorNome cache local para evitar consultas repetidas
     * @return setor existente ou novo setor salvo
     */
    private Setor obterOuCriarSetor(Setor setorGerado, Map<String, Setor> setoresPorNome) {
        if (setorGerado == null || setorGerado.getNome() == null) {
            return null;
        }

        String chave = setorGerado.getNome().trim().toLowerCase(Locale.ROOT);
        if (setoresPorNome.containsKey(chave)) {
            return setoresPorNome.get(chave);
        }

        Setor setor = setorRepository.findByNomeIgnoreCase(setorGerado.getNome())
                .orElseGet(() -> setorRepository.save(new Setor(setorGerado.getNome(), setorGerado.getRamal())));

        setoresPorNome.put(chave, setor);
        return setor;
    }

    /**
     * Guarda os totais de registros criados.
     *
     * @param funcionariosGerados total de funcionarios criados
     * @param movimentacoesGeradas total de movimentacoes criadas
     */
    public record ResultadoGeracao(int funcionariosGerados, int movimentacoesGeradas) {
    }
}
