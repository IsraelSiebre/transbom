package com.insights.rh.analytics;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.insights.rh.analytics.IndicadoresFolha.*;
import static com.insights.rh.analytics.AgregacoesFolha.*;

@Repository
class EvolucaoFolha {
    private final ConsultaFolha consulta;
    private final NamedParameterJdbcTemplate jdbc;
    EvolucaoFolha(ConsultaFolha consulta, NamedParameterJdbcTemplate jdbc) {
        this.consulta = consulta;
        this.jdbc = jdbc;
    }

    record Series(List<Map<String,Object>> mensal, List<Map<String,Object>> anual,
                  List<Map<String,Object>> comparacoes, Map<String,Map<String,Object>> custosCentro) {}

    Series montar(MultiValueMap<String,String> p, PeriodoFolha periodo, Cubo cubo) {
        if (periodo.meses().isEmpty()) return new Series(List.of(),List.of(),List.of(),Map.of());
        LocalDate ultimo = periodo.meses().getLast();
        var contexto = new TreeSet<LocalDate>();
        periodo.meses().forEach(d -> {
            contexto.add(d);
            contexto.add(d.minusMonths(1));
            contexto.add(d.minusYears(1));
        });
        List<LocalDate> ytd = new ArrayList<>();
        for (LocalDate d = ultimo.withMonth(1); !d.isAfter(ultimo); d = d.plusMonths(1)) {
            ytd.add(d);
            contexto.add(d);
            contexto.add(d.minusYears(1));
        }
        var e = consulta.escopo(p,List.copyOf(contexto));
        String sql = "select f.competencia, " + ConsultaFolha.CENTRO + " centro,"
                + "coalesce(sum(f.custo_mo_total),0) custo, count(distinct f.pessoa) quantidade "
                + e.from() + " group by grouping sets ((f.competencia),(f.competencia," + ConsultaFolha.CENTRO + "))";
        Map<LocalDate,Map<String,Object>> totais = new HashMap<>();
        Map<String,Map<LocalDate,Map<String,Object>>> porCentro = new HashMap<>();
        for (var m : jdbc.queryForList(sql,e.params())) {
            converterData(m,"competencia");
            LocalDate d = (LocalDate)m.get("competencia");
            if (m.get("centro")==null) totais.put(d,m);
            else porCentro.computeIfAbsent((String)m.get("centro"),k -> new HashMap<>()).put(d,m);
        }
        List<Map<String,Object>> mensal = new ArrayList<>();
        Map<Object,Map<String,Object>> encontrados = indice(cubo.lista(MENSAL));
        for (LocalDate d : periodo.meses()) {
            boolean importado = periodo.importados().contains(d);
            var m = encontrados.getOrDefault(d,vazio());
            m.put("id",d);
            m.put("competencia",d);
            m.put("importado",importado);
            if (importado) {
                var base = totais.getOrDefault(d.minusMonths(1),Map.of());
                boolean temBase = periodo.importados().contains(d.minusMonths(1));
                comparar(m,numero(m,"custo"),temBase ? numero(base,"custo") : null);
                m.put("mudancaQuadro",temBase ? numero(m,"quantidade").subtract(numero(base,"quantidade")) : null);
            } else {
                EXPRESSOES.keySet().forEach(k -> m.put(k,null));
                for (String k : List.of("quantidade","valor","contratos","custoMedio","salarioMedio","registros")) m.put(k,null);
            }
            mensal.add(m);
        }
        List<Map<String,Object>> anual = new ArrayList<>();
        var anos = indice(cubo.lista(ANUAL));
        for (int ano : periodo.meses().stream().map(LocalDate::getYear).distinct().toList()) {
            var datas = periodo.meses().stream().filter(d -> d.getYear()==ano).toList();
            var importadas = datas.stream().filter(periodo.importados()::contains).toList();
            if (importadas.isEmpty()) continue;
            var m = anos.getOrDefault(ano,vazio());
            m.put("id",ano);
            BigDecimal n = BigDecimal.valueOf(importadas.size());
            m.put("mediaMensal",dividir(numero(m,"custo"),n));
            m.put("quadroMedio",dividir(numero(m,"contratos"),n));
            m.put("encerramento",numero(totais.getOrDefault(importadas.getLast(),Map.of()),"quantidade"));
            m.put("ultimaCompetencia",importadas.getLast());
            m.put("mesesImportados",importadas.size());
            m.put("mesesSolicitados",datas.size());
            boolean completo = periodo.importados().containsAll(datas);
            var anterior = somar(datas.stream().map(d -> d.minusYears(1)).toList(),totais,periodo.importados());
            comparar(m,numero(m,"custo"),completo ? anterior : null);
            anual.add(m);
        }
        anual.sort(Comparator.comparingInt(m -> ((Number)m.get("id")).intValue()));
        var comparacoes = List.of(
                par("Mês contra mês anterior",List.of(ultimo),List.of(ultimo.minusMonths(1)),totais,periodo),
                par("Mesmo mês do ano anterior",List.of(ultimo),List.of(ultimo.minusYears(1)),totais,periodo),
                par("Acumulado do ano",ytd,ytd.stream().map(d -> d.minusYears(1)).toList(),totais,periodo));
        Map<String,Map<String,Object>> custosCentro = new HashMap<>();
        porCentro.forEach((cc,datas) -> {
            var comp = new HashMap<String,Object>();
            BigDecimal atual = somar(List.of(ultimo),datas,periodo.importados());
            for (var ref : Map.of("mensal",ultimo.minusMonths(1),"anual",ultimo.minusYears(1)).entrySet()) {
                BigDecimal anterior = somar(List.of(ref.getValue()),datas,periodo.importados());
                comp.put(ref.getKey(),atual==null || anterior==null ? null : percentual(atual.subtract(anterior),anterior.abs()));
            }
            custosCentro.put(cc,comp);
        });
        return new Series(mensal,anual,comparacoes,custosCentro);
    }

    static Map<Object, Map<String,Object>> indice(List<Map<String,Object>> linhas) {
        Map<Object, Map<String,Object>> m = new LinkedHashMap<>();
        linhas.forEach(l -> m.put(l.get("id"),l));
        return m;
    }

    private static Map<String,Object> par(String nome,List<LocalDate> meses,List<LocalDate> ref,
                                         Map<LocalDate,Map<String,Object>> dados,PeriodoFolha periodo) {
        BigDecimal atual = somar(meses,dados,periodo.importados()), anterior = somar(ref,dados,periodo.importados());
        var m = new LinkedHashMap<String,Object>();
        m.put("nome",nome);
        m.put("atual",atual);
        m.put("inicio",meses.getFirst());
        m.put("fim",meses.getLast());
        m.put("baseInicio",ref.getFirst());
        m.put("baseFim",ref.getLast());
        comparar(m,atual==null ? BigDecimal.ZERO : atual,atual==null ? null : anterior);
        return m;
    }

    private static BigDecimal somar(List<LocalDate> meses,Map<LocalDate,Map<String,Object>> totais,List<LocalDate> importados) {
        if (!importados.containsAll(meses)) return null;
        return meses.stream().map(d -> numero(totais.getOrDefault(d,Map.of()),"custo")).reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}

