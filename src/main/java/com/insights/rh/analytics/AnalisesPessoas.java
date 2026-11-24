package com.insights.rh.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalisesPessoas {
    private final NamedParameterJdbcTemplate jdbc;

    // Uma observação salarial por pessoa e competência. Preserva todos os lançamentos
    // financeiros e exclui do cálculo estatístico observações sem salário informado.
    private String salarios(ConsultaFolha.Escopo escopo) {
        return "select f.competencia, f.pessoa, max(f.nome) nome, sum(f.salario_8781) salario " + escopo.from()
                + " group by f.competencia, f.pessoa having count(f.salario_8781) > 0";
    }

    public Map<String, Object> estatisticas(ConsultaFolha.Escopo escopo) {
        return jdbc.queryForMap("""
                select count(*) observacoes, round(avg(salario), 2) media,
                       min(salario) minimo, max(salario) maximo,
                       round(avg(salario) filter (where posicao between (n + 1) / 2 and (n + 2) / 2), 2) mediana
                from (select salario, row_number() over (order by salario) posicao, count(*) over () n
                      from (%s) salarios) ordenados
                """.formatted(salarios(escopo)), escopo.params());
    }

    public List<Map<String, Object>> histograma(ConsultaFolha.Escopo escopo) {
        return jdbc.queryForList("select faixa, count(*) quantidade from ("+faixas(escopo)
                +") faixas group by faixa order by faixa", escopo.params());
    }

    private String faixas(ConsultaFolha.Escopo escopo) {
        return """
                select salarios.*, case when salario < 0 then -1 when salario = 0 then 0
                       when salario < 2000 then 1 when salario < 4000 then 2
                       when salario < 6000 then 3 when salario < 10000 then 4 else 5 end faixa
                from (%s) salarios
                """.formatted(salarios(escopo));
    }

    public List<Map<String,Object>> observacoes(ConsultaFolha.Escopo escopo, int faixa, int pagina) {
        if (faixa < -1 || faixa > 5 || pagina < 0) throw PeriodoFolha.invalido("Faixa ou página inválida.");
        var parametros = escopo.params().addValue("faixa",faixa).addValue("pular",25L * pagina);
        var registros = jdbc.queryForList("select competencia,pessoa,nome,salario,count(*) over() as registros from ("
                + faixas(escopo)+") faixas where faixa=:faixa order by competencia desc, salario desc, pessoa"
                +" limit 25 offset :pular", parametros);
        registros.forEach(r -> AgregacoesFolha.converterData(r,"competencia"));
        return registros;
    }
}
