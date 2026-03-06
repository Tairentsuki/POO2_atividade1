package com.atividade001.services;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.atividade001.model.*;
import com.atividade001.repository.*;

/**
 * Exporta e importa um único CSV com seções: Setor, Funcionários e Movimentações.
 */
@Service
public class CsvDatabaseService {
    private final FuncionarioRepository funcionarioRepository;
    private final SetorRepository setorRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    /**
     * Cria o serviço com os repositórios necessários.
     *
     * @param funcionarioRepository repositório de funcionários
     * @param setorRepository repositório de setores
     * @param movimentacaoRepository repositório de movimentações
     */
    public CsvDatabaseService(
            FuncionarioRepository funcionarioRepository,
            SetorRepository setorRepository,
            MovimentacaoRepository movimentacaoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.setorRepository = setorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Gera um CSV único com todas as tabelas, organizado por seção.
     * O formato foi reduzido para ficar mais simples.
     *
     * @return conteúdo do CSV em bytes UTF-8
     */
    public byte[] exportarTudo() {
        StringBuilder csv = new StringBuilder();

        csv.append("// Setor\n");
        csv.append("nome,ramal\n");
        for (Setor setor : setorRepository.findAllByOrderByNomeAsc()) {
            csv.append(setor.toCsv()).append('\n');
        }
        csv.append('\n');

        csv.append("// Funcionários\n");
        csv.append("nomeCompleto,cpf,dataNascimento,sexo,salarioBruto,formacao,nomeSetor\n");
        for (Funcionario funcionario : funcionarioRepository.findAllComSetorOrdenados()) {
            csv.append(funcionario.toCsv()).append('\n');
        }
        csv.append('\n');

        csv.append("// Movimentações\n");
        csv.append("tipo,data,valor,descricao,nomeSetor,cpfFuncionario\n");
        for (Movimentacao movimentacao : movimentacaoRepository.findAllByOrderByDataDescIdDesc()) {
            csv.append(movimentacao.toCsv()).append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Lê um CSV por seções e importa os registros.
     *
     * @param arquivo arquivo enviado no formulário
     * @return total de linhas importadas com sucesso
     */
    public int importarTudo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            return 0;
        }

        int totalImportado = 0;
        String secaoAtual = "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String texto = linha.trim();
                if (texto.isBlank()) {
                    continue;
                }

                if (texto.startsWith("//")) {
                    secaoAtual = normalizarSecao(texto);
                    continue;
                }

                if (isCabecalho(secaoAtual, texto)) {
                    continue;
                }

                String[] colunas = texto.split(",", -1);
                boolean importou = false;

                if ("setor".equals(secaoAtual)) {
                    importou = importarSetor(colunas);
                } else if ("funcionario".equals(secaoAtual)) {
                    importou = importarFuncionario(colunas);
                } else if ("movimentacao".equals(secaoAtual)) {
                    importou = importarMovimentacao(colunas);
                }

                if (importou) {
                    totalImportado++;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler CSV.", e);
        }

        return totalImportado;
    }

    /**
     * Importa uma linha da seção Setor.
     *
     * @param colunas colunas da linha CSV
     * @return verdadeiro quando salvou com sucesso
     */
    private boolean importarSetor(String[] colunas) {
        if (colunas.length < 2) {
            return false;
        }

        String nome = valor(colunas, 0);
        String ramal = valor(colunas, 1);
        if (nome.isBlank()) {
            return false;
        }

        Setor setor = setorRepository.findByNomeIgnoreCase(nome).orElseGet(() -> new Setor(nome, ramal));
        setor.setNome(nome);
        setor.setRamal(ramal);
        setorRepository.save(setor);
        return true;
    }

    /**
     * Importa uma linha da seção Funcionários.
     *
     * @param colunas colunas da linha CSV
     * @return verdadeiro quando salvou com sucesso
     */
    private boolean importarFuncionario(String[] colunas) {
        if (colunas.length < 7) {
            return false;
        }

        String cpf = valor(colunas, 1);
        if (cpf.isBlank()) {
            return false;
        }

        Funcionario funcionario = funcionarioRepository.findByCpf(cpf).orElseGet(Funcionario::new);
        funcionario.setNomeCompleto(valor(colunas, 0));
        funcionario.setCpf(cpf);
        funcionario.setDataDeNascimento(parseData(valor(colunas, 2)));
        funcionario.setSexo(valor(colunas, 3));
        funcionario.setSalarioBruto(parseNumero(valor(colunas, 4)));
        funcionario.setFormacao(valor(colunas, 5));
        funcionario.setSetor(obterOuCriarSetor(valor(colunas, 6), ""));
        funcionarioRepository.save(funcionario);
        return true;
    }

    /**
     * Importa uma linha da seção Movimentações.
     *
     * @param colunas colunas da linha CSV
     * @return verdadeiro quando salvou com sucesso
     */
    private boolean importarMovimentacao(String[] colunas) {
        if (colunas.length < 6) {
            return false;
        }

        TipoMovimentacao tipo = parseTipo(valor(colunas, 0));
        if (tipo == null) {
            return false;
        }

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(tipo);
        movimentacao.setData(parseDataHora(valor(colunas, 1)));
        movimentacao.setValor(parseNumero(valor(colunas, 2)));
        movimentacao.setDescricao(valor(colunas, 3));
        movimentacao.setSetor(obterOuCriarSetor(valor(colunas, 4), ""));
        movimentacao.setFuncionario(funcionarioRepository.findByCpf(valor(colunas, 5)).orElse(null));
        movimentacaoRepository.save(movimentacao);
        return true;
    }

    /**
     * Converte o nome da seção para uma chave interna.
     *
     * @param secaoLinha linha da seção (ex.: // Setor)
     * @return setor, funcionario, movimentacao ou vazio
     */
    private String normalizarSecao(String secaoLinha) {
        String secao = secaoLinha.replace("//", "").trim().toLowerCase(Locale.ROOT);
        if (secao.startsWith("setor")) {
            return "setor";
        }
        if (secao.startsWith("funcion")) {
            return "funcionario";
        }
        if (secao.startsWith("moviment")) {
            return "movimentacao";
        }
        return "";
    }

    /**
     * Diz se a linha atual é cabeçalho da seção.
     *
     * @param secaoAtual seção em leitura
     * @param linha texto da linha
     * @return verdadeiro quando for cabeçalho
     */
    private boolean isCabecalho(String secaoAtual, String linha) {
        String texto = linha.toLowerCase(Locale.ROOT);
        if ("setor".equals(secaoAtual)) {
            return texto.startsWith("nome,ramal");
        }
        if ("funcionario".equals(secaoAtual)) {
            return texto.startsWith("nomecompleto,cpf,datanascimento");
        }
        if ("movimentacao".equals(secaoAtual)) {
            return texto.startsWith("tipo,data,valor,descricao");
        }
        return false;
    }

    /**
     * Lê valor de coluna com segurança.
     *
     * @param colunas array de colunas
     * @param indice posição desejada
     * @return valor limpo ou string vazia
     */
    private String valor(String[] colunas, int indice) {
        if (indice < 0 || indice >= colunas.length) {
            return "";
        }
        return colunas[indice].trim();
    }

    /**
     * Busca setor por nome ou cria um novo.
     *
     * @param nome nome do setor
     * @param ramal ramal do setor
     * @return setor existente ou criado
     */
    private Setor obterOuCriarSetor(String nome, String ramal) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        return setorRepository.findByNomeIgnoreCase(nome.trim())
                .orElseGet(() -> setorRepository.save(new Setor(nome.trim(), ramal != null ? ramal.trim() : "")));
    }

    /**
     * Converte texto para número decimal.
     *
     * @param valor texto numérico
     * @return número convertido ou 0.0
     */
    private double parseNumero(String valor) {
        if (valor == null || valor.isBlank()) {
            return 0.0;
        }
        return Double.parseDouble(valor.replace(',', '.').trim());
    }

    /**
     * Converte texto para LocalDate.
     *
     * @param valor texto da data
     * @return data convertida ou null
     */
    private LocalDate parseData(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return LocalDate.parse(valor.trim());
    }

    /**
     * Converte texto para LocalDateTime.
     *
     * @param valor texto da data/hora
     * @return data/hora convertida ou null
     */
    private LocalDateTime parseDataHora(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(valor.trim());
    }

    /**
     * Converte texto para TipoMovimentacao.
     *
     * @param valor texto do tipo
     * @return enum convertido ou nulo
     */
    private TipoMovimentacao parseTipo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return TipoMovimentacao.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
