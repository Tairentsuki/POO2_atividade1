package com.atividade001.services;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.atividade001.model.Funcionario;

public class GeradorCsv {
    private static final int BUFFER_SIZE = 1 << 20;
    private static final long PROGRESS_INTERVAL = 1_000_000L;

    public static void exportarFuncionarios(long quantidade, String nomeDoArquivo) {
        System.out.println("Iniciando a geração de " + quantidade + " registros...");
        Path caminho = Path.of(nomeDoArquivo);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new BufferedOutputStream(
                                Files.newOutputStream(
                                        caminho,
                                        StandardOpenOption.CREATE,
                                        StandardOpenOption.TRUNCATE_EXISTING,
                                        StandardOpenOption.WRITE),
                                BUFFER_SIZE),
                        StandardCharsets.UTF_8),
                BUFFER_SIZE)) {
            writer.write("IdFuncionario,NomeCompleto,DataNascimento,CPF,Sexo,SalarioBruto,Cargo,idSetor,Setor,Ramal");
            writer.newLine();

            StringBuilder linha = new StringBuilder(192);
            for (long i = 0; i < quantidade; i++) {
                Funcionario funcionario = DataFakerGenerator.gerarFuncionario();
                appendLinhaCsvFuncionario(linha, funcionario);
                writer.append(linha);
                writer.newLine();

                if ((i + 1) % PROGRESS_INTERVAL == 0) {
                    writer.flush();
                    System.out.println("Gerados: " + (i + 1));
                }
            }

            System.out.println("Sucesso! Arquivo '" + nomeDoArquivo + "' gerado na raiz do projeto.");
        } catch (Exception e) {
            System.err.println("Erro ao salvar o arquivo CSV: " + e.getMessage());
        }
    }

    private static void appendLinhaCsvFuncionario(StringBuilder linha, Funcionario funcionario) {
        linha.setLength(0);

        linha.append(funcionario.getId()).append(',');
        linha.append(funcionario.getNomeCompleto()).append(',');
        linha.append(funcionario.getDataDeNascimento()).append(',');
        linha.append(funcionario.getCpf()).append(',');
        linha.append(funcionario.getSexo()).append(',');
        appendValorComDuasCasas(linha, funcionario.getSalarioBruto());
        linha.append(',');
        linha.append(funcionario.getFormacao()).append(',');
        linha.append(funcionario.getSetor().getId()).append(',');
        linha.append(funcionario.getSetor().getNome()).append(',');
        linha.append(funcionario.getSetor().getRamal());
    }

    private static void appendValorComDuasCasas(StringBuilder destino, double valor) {
        long valorEmCentavos = Math.round(valor * 100.0);
        long inteiro = valorEmCentavos / 100;
        long centavos = Math.abs(valorEmCentavos % 100);

        destino.append(inteiro).append('.');
        if (centavos < 10) {
            destino.append('0');
        }
        destino.append(centavos);
    }
}
