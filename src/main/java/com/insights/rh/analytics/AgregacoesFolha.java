package com.insights.rh.analytics;

import com.insights.rh.importacao.model.CentroDeCusto;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

import static com.insights.rh.analytics.ConsultaFolha.*;
import static com.insights.rh.analytics.IndicadoresFolha.*;

@Repository
class AgregacoesFolha {
    private final NamedParameterJdbcTemplate jdbc;
    AgregacoesFolha(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    static final String MES = "f.competencia";
    static final String ANO = "cast(extract(year from f.competencia) as integer)";
    static final int GERAL = 15, MENSAL = 7, ANUAL = 11, CENTROS = 13, CARGOS = 14,
            CENTROS_MENSAIS = 5, CARGOS_MENSAIS = 6, MATRIZ = 12;

    record Cubo(Map<Integer,List<Map<String,Object>>> linhas) {
        List<Map<String,Object>> lista(int tipo) { return linhas.getOrDefault(tipo,List.of()); }
        Map<String,Object> total() { return lista(GERAL).stream().findFirst().orElseGet(IndicadoresFolha::vazio); }
    }

    Cubo agregar(ConsultaFolha.Escopo e, String medida, boolean detalhado) {
        String dims = String.join(",",MES,ANO,CENTRO,CARGO);
        String grupos = "(),(" + CENTRO + "),(" + CARGO + ")";
        if (detalhado) grupos += ",(" + MES + "),(" + ANO + "),(" + MES + "," + CENTRO + "),("
                + MES + "," + CARGO + "),(" + CENTRO + "," + CARGO + ")";
        // Todas as dimensões da expressão GROUPING precisam pertencer a pelo menos
        // um agrupamento, inclusive na consulta reduzida do período comparativo.
        if (!detalhado) grupos += ",(" + MES + "),(" + ANO + ")";
        String sql = "select grouping(" + dims + ") tipo," + MES + " competencia," + ANO + " ano,"
                + CENTRO + " centro," + CARGO + " cargo," + agregados(medida)
                + e.from() + " group by grouping sets (" + grupos + ")";
        Map<Integer,List<Map<String,Object>>> linhas = new HashMap<>();
        for (var m : jdbc.queryForList(sql,e.params())) {
            int tipo = ((Number)m.remove("tipo")).intValue();
            converterData(m,"competencia");
            switch (tipo) {
                case MENSAL -> m.put("id",m.get("competencia"));
                case ANUAL -> m.put("id",m.get("ano"));
                case CENTROS,CENTROS_MENSAIS,MATRIZ -> {
                    String cc = (String)m.get("centro");
                    m.put("id",cc);
                    m.put("nome",nomeCentro(cc));
                }
                case CARGOS,CARGOS_MENSAIS -> {
                    m.put("id",m.get("cargo"));
                    m.put("nome",PENDENTE.equals(m.get("cargo")) ? "Sem cargo" : m.get("cargo"));
                }
                default -> { }
            }
            linhas.computeIfAbsent(tipo, t -> new ArrayList<>()).add(medias(m));
        }
        return new Cubo(linhas);
    }

    static void converterData(Map<String,Object> m, String campo) {
        if (m.get(campo) instanceof Date data) m.put(campo,data.toLocalDate());
    }
    static String nomeCentro(String cc) {
        return PENDENTE.equals(cc) ? "Sem centro de custo" : CentroDeCusto.valueOf(cc).getDescricao();
    }

    List<Map<String,Object>> funcionarios(ConsultaFolha.Escopo escopo, ConsultaFolha.Escopo base,
                                          String medida, int top, String ordem, boolean comparavel, BigDecimal custo) {
        String criterio = switch (ordem) {
            case "quantidade" -> "a.contratos";
            case "participacao" -> custo.signum()<0 ? "-a.custo" : "a.custo";
            case "variacao" -> comparavel ? "a.valor - coalesce(b.anterior,0)" : "a.valor";
            case "salario" -> "a.salario";
            case "custoIndividual" -> "a.custo";
            default -> "a.valor";
        };
        String queryBase = comparavel
                ? "select f.pessoa," + soma(medida) + " anterior " + base.from().replace(":competencias",":mesesBase") + " group by f.pessoa"
                : "select null::bigint pessoa, null::numeric anterior where false";
        if (comparavel) escopo.params().addValue("mesesBase",base.meses());
        String exigeSalario = "salario".equals(ordem) ? " where a.observacoes_salariais>0 " : " ";
        String sql = "with a as (select f.pessoa id,max(f.nome) nome," + agregados(medida)
                + escopo.from() + " group by f.pessoa), b as (" + queryBase + ") "
                + "select a.*,b.anterior from a left join b on a.id=b.pessoa" + exigeSalario + "order by " + criterio + " desc, a.id limit :limite";
        escopo.params().addValue("limite",top);
        var linhas = jdbc.queryForList(sql,escopo.params());
        for (var m : linhas) {
            medias(m);
            m.put("participacao",percentual(numero(m,"custo"),custo));
            comparar(m,numero(m,"valor"),comparavel ? numero(m,"anterior") : null);
        }
        return linhas;
    }
}

