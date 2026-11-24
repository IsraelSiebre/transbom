package com.insights.rh.analytics;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import static com.insights.rh.analytics.IndicadoresFolha.*;

final class InsightsFolha {
    private InsightsFolha() {}

    static List<Map<String,Object>> gerar(Map<String,Object> atual, Map<String,Object> base, List<Map<String,Object>> centros,
                                          List<Map<String,Object>> grupos,Map<String,Object> qualidade,List<Map<String,Object>> meses) {
        var lista = new ArrayList<Map<String,Object>>();
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        NumberFormat n = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        n.setMaximumFractionDigits(2);
        if (numero(qualidade,"semCentro").signum()>0) {
            lista.add(insight("Classificação pendente",qualidade.get("semCentro") + " registro(s) representam " + moeda.format(qualidade.get("pendente"))
                    + " do custo do recorte.","Alta","qualidade"));
        }
        if (numero(atual,"registros").signum()==0) return lista;
        if (base!=null) {
            BigDecimal custo = numero(atual,"custo");
            BigDecimal custoBase = numero(base,"custo");
            BigDecimal variacao = percentual(custo.subtract(custoBase),custoBase.abs());
            lista.add(insight("Evolução do custo",moeda.format(custo) + " no período selecionado e " + moeda.format(custoBase)
                    + " na base comparável. Diferença de " + moeda.format(custo.subtract(custoBase))
                    + (variacao==null ? "." : " (" + n.format(variacao) + "%)."),"Gerencial","evolucao"));
            grupos.stream().filter(g -> numero(g,"diferenca").signum()!=0)
                    .max(Comparator.comparing(g -> numero(g,"diferenca").abs())).ifPresent(g -> {
                        var i = insight("Maior parcela da variação",g.get("nome") + " apresentou diferença de " + moeda.format(g.get("diferenca"))
                                + " em relação ao período comparável.","Gerencial","composicao");
                        if (!"residual".equals(g.get("id"))) i.put("grupo",g.get("id"));
                        lista.add(i);
                    });
            BigDecimal deltaQuadro = numero(atual,"contratos").subtract(numero(base,"contratos"));
            BigDecimal custoMedio = numero(atual,"custoMedio"), mediaBase = numero(base,"custoMedio");
            if (atual.get("custoMedio")!=null && base.get("custoMedio")!=null && deltaQuadro.signum()==0 && custoMedio.compareTo(mediaBase)>0)
                lista.add(insight("Custo médio em alta","Com " + n.format(atual.get("contratos")) + " pessoas-mês nos dois períodos, o custo médio passou de "
                        + moeda.format(mediaBase) + " para " + moeda.format(custoMedio) + ".","Gerencial","pessoas"));
        }
        centros.stream().filter(c -> c.get("participacao")!=null)
                .max(Comparator.comparing(c -> numero(c,"custo"))).ifPresent(c -> {
                    var i = insight("Concentração por operação",c.get("nome") + " representa " + n.format(c.get("participacao")) + "% do custo do período ("
                            + moeda.format(c.get("custo")) + ").","Informativa","centros");
                    i.put("centro",c.get("id"));
                    lista.add(i);
                });
        meses.stream().filter(m -> numero(m,"variacao").abs().compareTo(BigDecimal.valueOf(20))>=0)
                .max(Comparator.comparing(m -> numero(m,"variacao").abs())).ifPresent(m ->
                    lista.add(insight("Oscilação mensal a revisar",m.get("competencia") + " teve variação de " + n.format(m.get("variacao"))
                            + "% frente ao mês imediatamente anterior. Critério de revisão: magnitude de 20% ou mais.","Atenção","evolucao")));
        return lista;
    }

    private static Map<String,Object> insight(String titulo,String evidencia,String relevancia,String visao) {
        return new LinkedHashMap<>(Map.of("titulo",titulo,"evidencia",evidencia,"relevancia",relevancia,"visao",visao));
    }
}

