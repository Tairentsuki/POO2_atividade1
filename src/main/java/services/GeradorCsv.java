package services;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GeradorCsv {
    private static final int BUFFER_SIZE = 1 << 20; // 1 MB
    private static final long PROGRESS_INTERVAL = 1_000_000L;

    public static void exportarFuncionarios(long quantidade, String nomeDoArquivo) {
        System.out.println("Iniciando a geracao de " + quantidade + " registros...");
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
                DataFakerGenerator.RegistroFuncionario registro = DataFakerGenerator.gerarRegistroFuncionario();
                appendLinhaCsvFuncionario(linha, registro);
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

    private static void appendLinhaCsvFuncionario(StringBuilder linha, DataFakerGenerator.RegistroFuncionario registro) {
        linha.setLength(0);

        linha.append(registro.getIdFuncionario()).append(',');
        linha.append(registro.getNomeCompleto()).append(',');
        linha.append(registro.getDataNascimento()).append(',');
        linha.append(registro.getCpf()).append(',');
        linha.append(registro.getSexo()).append(',');
        appendValorComDuasCasas(linha, registro.getSalarioBruto());
        linha.append(',');
        linha.append(registro.getFormacao()).append(',');
        linha.append(registro.getIdSetor()).append(',');
        linha.append(registro.getNomeSetor()).append(',');
        linha.append(registro.getRamal());
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
