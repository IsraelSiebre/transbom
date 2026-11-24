package com.insights.rh.analytics;

import com.insights.rh.importacao.model.CentroDeCusto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.*;

@Service
public class ConsultaFolha {
    public static final String PESSOA = "f.pessoa";
    public static final String PENDENTE = "__pendente__";
    static final String CENTRO = "coalesce(f.centro_de_custo,'__pendente__')";
    static final String CARGO = "case when nullif(trim(f.cargo),'') is null then '__pendente__' else f.cargo end";

    // Códigos da planilha antiga são reutilizados. CPF só atravessa uma chave legada
    // (código + nome) quando existe exatamente um CPF associado a ela.
    // A admissão varia entre competências históricas para a mesma chave de folha.
    // Somente o menor ID interno do grupo sai do banco.
    private static final String IDENTIDADES = """
        (with chaves as (
            select id, nullif(regexp_replace(coalesce(cpf,''),'[^0-9]','','g'),'') as digitos,
                   case when codigo_externo is not null and nullif(trim(nome),'') is not null
                        then concat('legado:', codigo_externo, '|', upper(regexp_replace(trim(nome),'[[:space:]]+',' ','g'))) end as legado,
                        data_admissao
            from funcionario_calculo
        ), documentos as (
            select id, legado, data_admissao, case when length(digitos)=11 and digitos <> repeat(left(digitos,1),11)
                                    then digitos end as documento from chaves
        ), vinculos as (
            select legado,min(documento) as documento,count(distinct documento)>1 as ambiguo,
                   count(distinct data_admissao)>1 as admissao_instavel from documentos
            where legado is not null group by legado
        ), chaves_resolvidas as (
            select d.id,coalesce('cpf:' || d.documento,
                     case when not coalesce(v.ambiguo,false) then coalesce('cpf:' || v.documento,d.legado) end) as chave,
                     coalesce(v.ambiguo,false) as ambiguo,coalesce(v.admissao_instavel,false) as admissao_instavel,
                     (d.documento is null and d.legado is not null) as usa_legado
            from documentos d left join vinculos v on d.legado=v.legado
        ), pessoas as (
            select id,min(id) over (partition by coalesce(chave,'linha:' || id)) as pessoa,
                   chave is null as isolado,ambiguo,admissao_instavel,usa_legado from chaves_resolvidas
        )
        select folha.*,p.pessoa,p.isolado,p.ambiguo,p.admissao_instavel,p.usa_legado from funcionario_calculo folha join pessoas p on folha.id=p.id) f
        """;
    private final NamedParameterJdbcTemplate jdbc;
    public ConsultaFolha(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Escopo(String from, String fromSimples, MapSqlParameterSource params, List<LocalDate> meses) {}

    List<LocalDate> competencias() {
        return jdbc.query("select distinct competencia from funcionario_calculo order by competencia", Map.of(),
                (rs, i) -> rs.getObject(1, LocalDate.class));
    }

    public Escopo filtrar(MultiValueMap<String,String> p) {
        return escopo(p, PeriodoFolha.resolver(p, competencias()).meses());
    }

    Escopo escopo(MultiValueMap<String,String> p, List<LocalDate> meses) {
        PeriodoFolha.valores(p, "rubrica").forEach(CatalogoRubricas::obter);
        var params = new MapSqlParameterSource();
        StringBuilder where = new StringBuilder(" where ");
        if (meses.isEmpty()) where.append("false");
        else {
            where.append("f.competencia in (:competencias)");
            params.addValue("competencias", meses);
        }
        List<String> centros = PeriodoFolha.valores(p, "centro");
        if (centros.stream().anyMatch(c -> !PENDENTE.equals(c) && Arrays.stream(CentroDeCusto.values()).noneMatch(e -> e.name().equals(c))))
            throw PeriodoFolha.invalido("Centro de custo desconhecido.");
        adicionar(where, params, CENTRO, "centros", centros);
        adicionar(where, params, CARGO, "cargos", PeriodoFolha.valores(p, "cargo"));
        List<Long> pessoas;
        try {
            pessoas = PeriodoFolha.valores(p, "funcionario").stream().map(Long::valueOf).toList();
        } catch (NumberFormatException e) {
            throw PeriodoFolha.invalido("Identificador de funcionário inválido.");
        }
        adicionar(where, params, PESSOA, "pessoas", pessoas);
        String from = " from " + IDENTIDADES + where;
        String fromSimples = pessoas.isEmpty() ? " from funcionario_calculo f " + where : from;
        return new Escopo(from, fromSimples, params, meses);
    }

    private static void adicionar(StringBuilder sql, MapSqlParameterSource params, String expressao, String chave, List<?> valores) {
        if (valores.isEmpty()) return;
        sql.append(" and ").append(expressao).append(" in (:").append(chave).append(")");
        params.addValue(chave, valores);
    }
}

