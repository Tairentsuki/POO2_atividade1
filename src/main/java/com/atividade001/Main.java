package com.atividade001;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.atividade001.services.GeradorCsv;
import org.springframework.context.annotation.Bean;
import com.atividade001.repository.FuncionarioRepository;
import com.atividade001.services.DataFakerGenerator;
import com.atividade001.model.Funcionario;

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
                System.err.println("Quantidade invalida: " + args[0]);
                return;
            }
        }

        if (args.length >= 2) {
            nomeArquivo = args[1];
        }
        GeradorCsv.exportarFuncionarios(quantidade, nomeArquivo);
    }

    @Bean
    public CommandLineRunner seedDatabase(FuncionarioRepository repo) {
        return args -> {
            for (int contador = 0; contador < 5; contador++) {
                Funcionario funcionario = DataFakerGenerator.gerarFuncionarioPersistivel();
                repo.save(funcionario);
            }
            System.out.println("Banco inicializado com " + repo.count() + " funcionários.");
        };
    }
}
