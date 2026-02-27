package com.atividade001;

import model.Pessoa;
import services.DataFakerGenerator;

public class Main {
    public static void main(String[] args) {
        for (int contador = 1; contador <= 20; contador++) {
            Pessoa pessoaTeste = DataFakerGenerator.gerarFuncionario();
            System.out.println(pessoaTeste.toCsv());
        }
    }
}