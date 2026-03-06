package com.atividade001;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.atividade001.services.DatabaseSeedService;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(DatabaseSeedService databaseSeedService) {
        return args -> {
            databaseSeedService.limparBanco();
            DatabaseSeedService.ResultadoGeracao resultado = databaseSeedService.gerarDados(100);

            System.out.println(
                    "Banco inicializado com " + resultado.funcionariosGerados() + " funcionarios e "
                            + resultado.movimentacoesGeradas() + " movimentacoes.");
        };
    }
}
