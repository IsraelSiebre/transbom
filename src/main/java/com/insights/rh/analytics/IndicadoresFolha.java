package com.insights.rh.analytics;

import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

final class IndicadoresFolha {
    private IndicadoresFolha() {}

    static final Map<String,String> EXPRESSOES;
    static {
        Map<String,String> metricas = new LinkedHashMap<>();
        metricas.put("custo",CatalogoRubricas.obter("custoMoTotal").sql());
        metricas.put("salario",CatalogoRubricas.obter("salario").sql());
        metricas.put("fgts",CatalogoRubricas.obter("fgts").sql());
        metricas.put("descontos",CatalogoRubricas.obter("descontosTotais").sql());
        for (String g : List.of("encargos","provisoes","comissoes","beneficios")) metricas.put(g, CatalogoRubricas.sqlGrupo(g));
        metricas.put("extras",CatalogoRubricas.sqlHorasExtras());
        EXPRESSOES = Collections.unmodifiableMap(metricas);
    }

    static String selecao(MultiValueMap<String,String> p) {
        if (p.getFirst("rubrica") != null) {
            var rubrica = CatalogoRubricas.obter(p.getFirst("rubrica"));
            if (p.getFirst("grupo")!=null && !rubrica.grupo().equals(p.getFirst("grupo")))
                throw PeriodoFolha.invalido("A rubrica selecionada não pertence ao grupo.");
            return rubrica.sql();
        }
        if (p.getFirst("grupo") != null) return CatalogoRubricas.sqlGrupo(p.getFirst("grupo"));
        return EXPRESSOES.get("custo");
    }

    static String agregados(String selecao) {
        return EXPRESSOES.entrySet().stream().map(e -> soma(e.getValue()) + " as " + e.getKey()).collect(Collectors.joining(","))
                + "," + soma(selecao) + " as valor,count(*) registros,count(distinct f.pessoa) quantidade,"
                + "count(distinct (f.competencia,f.pessoa)) as contratos,"
                + "count(distinct (f.competencia,f.pessoa)) filter (where f.salario_8781 is not null) as observacoes_salariais";
    }

    static String soma(String expressao) { return "coalesce(sum(" + expressao + "),0)"; }

    static Map<String,Object> vazio() {
        Map<String,Object> m = new LinkedHashMap<>();
        EXPRESSOES.keySet().forEach(k -> m.put(k, BigDecimal.ZERO));
        for (String k : List.of("registros","quantidade","contratos","observacoes_salariais","valor")) m.put(k, BigDecimal.ZERO);
        return medias(m);
    }

    static Map<String,Object> medias(Map<String,Object> linha) {
        linha.put("custoMedio", dividir(numero(linha,"custo"),numero(linha,"contratos")));
        linha.put("salarioMedio",dividir(numero(linha,"salario"),numero(linha,"observacoes_salariais")));
        return linha;
    }

    static BigDecimal numero(Map<String,Object> linha, String campo) {
        Object objeto = linha.get(campo);
        return objeto == null ? BigDecimal.ZERO : new BigDecimal(objeto.toString());
    }

    static BigDecimal dividir(BigDecimal valor, BigDecimal base) {
        return base.signum()==0 ? null : valor.divide(base, 2, RoundingMode.HALF_UP);
    }

    static BigDecimal percentual(BigDecimal valor, BigDecimal base) {
        return dividir(valor.movePointRight(2),base);
    }

    /** C1 - C0 = (N1 - N0) * C0/N0 + N1 * (C1/N1 - C0/N0).
     * Calcula com os totais originais e conserva os centavos da diferença observada. */
    static Map<String,BigDecimal> decomporQuadro(Map<String,Object> atual, Map<String,Object> base) {
        var n0 = numero(base,"contratos");
        var n1 = numero(atual,"contratos");
        if (n0.signum() == 0 || n1.signum() == 0) return Map.of();
        var custoBase = numero(base,"custo");
        var diferenca = numero(atual,"custo").subtract(custoBase);
        var volume = n1.subtract(n0).multiply(custoBase).divide(n0,2,RoundingMode.HALF_UP);
        return Map.of("volume",volume, "media",diferenca.subtract(volume), "diferenca",diferenca,
                "pessoasMesBase",n0, "pessoasMesAtual",n1);
    }

    static void comparar(Map<String,Object> linha, BigDecimal atual, BigDecimal base) {
        BigDecimal delta = base == null ? null : atual.subtract(base);
        linha.put("anterior",base);
        linha.put("diferenca",delta);
        linha.put("variacao",delta == null ? null : percentual(delta,base.abs()));
    }
}

