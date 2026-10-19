
package com.insights.rh;

import com.insights.rh.importacao.ConciliacaoPagamentoService;
import com.insights.rh.importacao.model.FuncionarioImportacao;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ControllerTeste {

    private final ConciliacaoPagamentoService conciliacaoService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/extracao/processar")
    @ResponseBody
    public ResponseEntity<List<FuncionarioImportacao>> processar(
            @RequestParam("folha") MultipartFile folha,
            @RequestParam("porFora") MultipartFile porFora
    ) throws IOException {

        if (folha.isEmpty() || porFora.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<FuncionarioImportacao> funcionarios =
                conciliacaoService.processar(folha, porFora);

        System.out.println(
                "Funcionários importados: " + funcionarios.size()
        );

        return ResponseEntity.ok(funcionarios);
    }
}
