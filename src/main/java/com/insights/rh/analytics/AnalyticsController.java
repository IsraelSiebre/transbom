package com.insights.rh.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mao-de-obra/api")
public class AnalyticsController {
    private final MaoDeObraAnalyticsService analytics;
    private final AnalisesPessoas pessoas;
    private final ConsultaFolha consulta;

    @GetMapping("/faixas-salariais/{faixa}")
    public List<Map<String,Object>> faixa(@PathVariable int faixa, @RequestParam(defaultValue="0") int paginaFaixa,
                                          @RequestParam MultiValueMap<String,String> parametros) {
        return pessoas.observacoes(consulta.filtrar(parametros), faixa, paginaFaixa);
    }

    @GetMapping("/resumo")
    public Map<String, Object> resumo(@RequestParam MultiValueMap<String, String> parametros) {
        return analytics.resumo(parametros);
    }

    @GetMapping("/filtros")
    public Map<String, Object> filtros(@RequestParam MultiValueMap<String, String> parametros) {
        return analytics.filtros(parametros);
    }
}
