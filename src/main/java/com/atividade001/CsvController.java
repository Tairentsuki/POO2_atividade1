package com.atividade001;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.atividade001.services.*;

/**
 * Controlador das rotas de CSV e administracao rapida do banco.
 */
@Controller
public class CsvController {
    private final CsvDatabaseService csvDatabaseService;
    private final DatabaseSeedService databaseSeedService;

    /**
     * Cria o controlador com os servicos usados na tela inicial.
     *
     * @param csvDatabaseService servico de importacao e exportacao CSV
     * @param databaseSeedService servico para limpar banco e gerar dados
     */
    public CsvController(CsvDatabaseService csvDatabaseService, DatabaseSeedService databaseSeedService) {
        this.csvDatabaseService = csvDatabaseService;
        this.databaseSeedService = databaseSeedService;
    }

    /**
     * Exporta todos os dados em um arquivo CSV.
     *
     * @return resposta HTTP com o arquivo para download
     */
    @GetMapping("/csv/exportar")
    public ResponseEntity<ByteArrayResource> exportarCsv() {
        byte[] csv = csvDatabaseService.exportarTudo();
        ByteArrayResource arquivo = new ByteArrayResource(csv);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=todas.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csv.length)
                .body(arquivo);
    }

    /**
     * Importa um arquivo CSV com todas as secoes.
     *
     * @param arquivo arquivo enviado pelo formulario
     * @param redirectAttributes usado para mensagem de retorno
     * @return redirecionamento para o index
     */
    @PostMapping("/csv/importar")
    public String importarCsv(@RequestParam("arquivo") MultipartFile arquivo, RedirectAttributes redirectAttributes) {
        try {
            int totalImportado = csvDatabaseService.importarTudo(arquivo);
            redirectAttributes.addFlashAttribute("csvMensagem",
                    "Importacao concluida: " + totalImportado + " registro(s).");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("csvMensagem", "Falha ao importar arquivo CSV.");
        }
        return "redirect:/index";
    }

    /**
     * Limpa todo o banco e reinicia as tabelas.
     *
     * @param redirectAttributes usado para mensagem de retorno
     * @return redirecionamento para o index
     */
    @PostMapping("/dados/limpar")
    public String limparBanco(RedirectAttributes redirectAttributes) {
        databaseSeedService.limparBanco();
        redirectAttributes.addFlashAttribute("csvMensagem", "Banco limpo com sucesso.");
        return "redirect:/index";
    }

    /**
     * Gera uma quantidade informada de dados ficticios.
     *
     * @param quantidade total de funcionarios para gerar
     * @param redirectAttributes usado para mensagem de retorno
     * @return redirecionamento para o index
     */
    @PostMapping("/dados/gerar")
    public String gerarDados(@RequestParam("quantidade") int quantidade, RedirectAttributes redirectAttributes) {
        if (quantidade < 1) {
            redirectAttributes.addFlashAttribute("csvMensagem", "Informe uma quantidade maior que zero.");
            return "redirect:/index";
        }

        DatabaseSeedService.ResultadoGeracao resultado = databaseSeedService.gerarDados(quantidade);
        redirectAttributes.addFlashAttribute("csvMensagem",
                "Geracao concluida: "
                        + resultado.funcionariosGerados()
                        + " funcionario(s) e "
                        + resultado.movimentacoesGeradas()
                        + " movimentacao(oes).");
        return "redirect:/index";
    }
}
