package com.atividade001;

import services.GeradorCsv;

public class Main {
    public static void main(String[] args) {
        long quantidade = 1_000_0L;
        String nomeArquivo = "funcionarios.csv";

        if (args.length >= 1) {
            try {
                quantidade = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Quantidade invalida: " + args[0]);
                return;
            }
        }

        if (args.length >= 2) {
            nomeArquivo = args[1];
        }
        GeradorCsv.exportarFuncionarios(quantidade, nomeArquivo);
    }
}