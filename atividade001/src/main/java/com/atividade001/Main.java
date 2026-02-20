package com.atividade001;

import com.atividade001.Models.Pessoa;
import com.atividade001.Services.DataFakerGenerator;

public class Main {
    public static void main(String[] args) {

        for (int contador = 1; contador <= 20; contador++) {
            Pessoa pessoaTeste = DataFakerGenerator.gerarPessoa();
            System.out.println(pessoaTeste.toCsv());
        }
    }
}