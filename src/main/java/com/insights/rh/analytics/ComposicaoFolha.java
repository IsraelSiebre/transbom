package com.insights.rh.analytics;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.insights.rh.analytics.IndicadoresFolha.*;

@Repository
class ComposicaoFolha {
    private final NamedParameterJdbcTemplate jdbc;
    ComposicaoFolha(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    private Map<String,Object> totais(ConsultaFolha.Escopo e) {
        String campos = CatalogoRubricas.todas().stream()
                .map(r -> "sum(f." + r.coluna() + ") as \"" + r.campo() + "\"")
                .collect(Collectors.joining(","));
        return jdbc.queryForMap("select " + campos + e.fromSimples(),e.params());
    }

    record Composicao(List<Map<String,Object>> rubricas, List<Map<String,Object>> grupos,
                      List<String> nulas, BigDecimal residual) {}

    Composicao calcular(ConsultaFolha.Escopo e, ConsultaFolha.Escopo base, boolean comparavel, BigDecimal custo,
                        BigDecimal custoAnterior) {
        var valores = totais(e);
        Map<String,Object> anteriores = comparavel ? totais(base) : Map.of();
        var rubricas = new ArrayList<Map<String,Object>>();
        var nulas = new ArrayList<String>();
        var grupos = new LinkedHashMap<String,BigDecimal>();
        var gruposBase = new LinkedHashMap<String,BigDecimal>();
        for (var r : CatalogoRubricas.todas()) {
            BigDecimal valor = numero(valores,r.campo());
            BigDecimal anterior = numero(anteriores,r.campo());
            if (valores.get(r.campo()) == null) nulas.add(r.nome());
            var m = linha(r.campo(),r.nome(),valor,comparavel ? anterior : null,r.aditiva() ? percentual(valor,custo) : null);
            m.put("grupo",r.grupo());
            m.put("natureza",r.natureza());
            m.put("aditiva",r.aditiva());
            m.put("derivada",r.derivada());
            m.put("codigo",r.codigo());
            rubricas.add(m);
            if (r.aditiva()) {
                grupos.merge(r.grupo(),valor,BigDecimal::add);
                gruposBase.merge(r.grupo(),anterior,BigDecimal::add);
            }
        }
        var decomposicao = new ArrayList<Map<String,Object>>();
        grupos.forEach((k,v) -> decomposicao.add(linha(k,TITULOS.get(k),v,comparavel ? gruposBase.get(k) : null,percentual(v,custo))));
        BigDecimal residual = custo.subtract(somaGrupos(grupos));
        BigDecimal residualBase = comparavel ? custoAnterior.subtract(somaGrupos(gruposBase)) : null;
        if (residual.signum()!=0 || (residualBase!=null && residualBase.signum()!=0))
            decomposicao.add(linha("residual","Diferença entre custo persistido e componentes",residual,residualBase,percentual(residual,custo)));
        rubricas.sort(Comparator.comparing((Map<String,Object> m) -> numero(m,"valor")).reversed());
        decomposicao.sort(Comparator.comparing((Map<String,Object> m) -> numero(m,"valor")).reversed());
        return new Composicao(rubricas,decomposicao,nulas,residual);
    }

    private static BigDecimal somaGrupos(Map<String,BigDecimal> m) {
        return m.values().stream().reduce(BigDecimal.ZERO,BigDecimal::add);
    }
    private static Map<String,Object> linha(String id,String nome,BigDecimal valor,BigDecimal base,BigDecimal participacao) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id",id);
        m.put("nome",nome);
        m.put("valor",valor);
        m.put("participacao",participacao);
        comparar(m,valor,base);
        return m;
    }

    Map<String,Object> qualidade(ConsultaFolha.Escopo e, PeriodoFolha periodo, Composicao composicao) {
        String sql = """
             select f.competencia,count(*) as registros,count(*) filter (where f.centro_de_custo is null) as "semCentro",
                    count(*) filter (where nullif(trim(f.cargo),'') is null) as "semCargo",
                    count(*) filter (where nullif(trim(f.cpf),'') is null) as "semCpf",
                    count(*) filter (where f.isolado) as "semIdentificador",
                    count(*) filter (where f.ambiguo) as "identidadeAmbigua",
                    count(*) filter (where f.usa_legado) as "identidadeLegada",
                    count(*) filter (where f.admissao_instavel) as "admissaoInstavel",
                    count(*) filter (where f.competencia < date_trunc('month',f.data_admissao)) as "antesAdmissao",
                    count(*) - count(distinct (f.competencia,f.pessoa)) as duplicados,
                    min(f.competencia) filter (where f.centro_de_custo is null) as "competenciaPendente",
                    coalesce(sum(f.custo_mo_total) filter (where f.centro_de_custo is null),0) as pendente,
                    coalesce(sum(f.custo_mo_total) filter (where f.centro_de_custo is not null),0) as classificado
             """ + e.from() + " group by grouping sets ((),(f.competencia)) order by competencia nulls first";
        var resultados = jdbc.queryForList(sql,e.params());
        var m = resultados.getFirst();
        AgregacoesFolha.converterData(m,"competenciaPendente");
        var porCompetencia = resultados.subList(1,resultados.size());
        porCompetencia.forEach(r -> {
            AgregacoesFolha.converterData(r,"competencia");
            AgregacoesFolha.converterData(r,"competenciaPendente");
        });
        m.put("porCompetencia",porCompetencia);
        m.put("ultima",periodo.importados().isEmpty() ? null : periodo.importados().getLast());
        m.put("competencias",periodo.importados().size());
        m.put("ausentes",periodo.meses().stream().filter(d -> !periodo.importados().contains(d)).toList());
        m.put("rubricasNulas",composicao.nulas());
        m.put("diferencaComposicao",composicao.residual());
        return m;
    }
}

