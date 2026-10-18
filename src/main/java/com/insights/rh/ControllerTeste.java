package com.insights.rh;

import com.insights.rh.extracao.ExtracaoService;
import com.insights.rh.extracao.FuncionarioImportacao;
import com.insights.rh.extracao.LinhaMaoDeObra;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ControllerTeste {

    private final ExtracaoService extracaoService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/extracao/processar")
    @ResponseBody
    public ResponseEntity<List<FuncionarioImportacao>> processar(
            @RequestParam("arquivo") MultipartFile arquivo
    ) throws IOException {

        Path temporario = Files.createTempFile(
                "extrato-",
                ".xlsx"
        );

        try {
            List<FuncionarioImportacao> resultado =
                    extracaoService.processar(arquivo);

            resultado.forEach(f -> {
                System.out.println(f.toString());
            });
            return ResponseEntity.ok(resultado);

        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}