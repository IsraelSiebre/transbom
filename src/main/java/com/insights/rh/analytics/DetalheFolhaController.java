package com.insights.rh.analytics;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/mao-de-obra/api")
@RequiredArgsConstructor
public class DetalheFolhaController {
    private final DetalheFolhaService detalhes;
    private final ExportacaoFolhaService exportacao;

    @GetMapping("/rubricas/catalogo")
    public List<CatalogoRubricas.Rubrica> catalogo() {
        return CatalogoRubricas.todas();
    }

    @GetMapping("/detalhes")
    public DetalheFolhaService.Pagina detalhes(@RequestParam MultiValueMap<String,String> filtros) {
        return detalhes.listar(filtros);
    }

    @GetMapping("/perfil/{pessoa}")
    public Map<String,Object> perfil(@PathVariable long pessoa, @RequestParam MultiValueMap<String,String> filtros) {
        return detalhes.perfil(pessoa, filtros);
    }

    @GetMapping("/exportar")
    public void exportar(@RequestParam(defaultValue = "funcionarios") String tipo,
                         @RequestParam(defaultValue = "csv") String formato,
                         @RequestParam MultiValueMap<String,String> filtros,
                         HttpServletResponse resposta) throws IOException {
        if (!Set.of("xlsx","csv").contains(formato)) throw DetalheFolhaService.invalido("Formato desconhecido.");
        ExportacaoFolhaService.colunas(tipo, filtros);
        resposta.setContentType(formato.equals("csv") ? "text/csv; charset=UTF-8" :
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resposta.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mao-de-obra-" + tipo + "." + formato + "\"");
        exportacao.escrever(tipo, formato, filtros, resposta.getOutputStream());
    }
}
