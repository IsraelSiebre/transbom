package com.insights.rh.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.insights.rh.analytics.IndicadoresFolha.*;
import static com.insights.rh.analytics.AgregacoesFolha.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class MaoDeObraAnalyticsService {
    private final ConsultaFolha consulta;
    private final NamedParameterJdbcTemplate jdbc;
    private final AgregacoesFolha agregacoes;
    private final EvolucaoFolha evolucao;
    private final ComposicaoFolha composicao;
    private final AnalisesPessoas pessoas;

    public Map<String,Object> filtros(MultiValueMap<String,String> p) {
        List<LocalDate> disponiveis = consulta.competencias();
        List<LocalDate> meses = PeriodoFolha.resolver(p,disponiveis).meses();
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("competencias",disponiveis);
        var e = faceta(p,meses,"centro");
        var centros = jdbc.queryForList("select distinct " + ConsultaFolha.CENTRO + " id " + e.fromSimples() + " order by 1",e.params());
        centros.forEach(c -> c.put("nome",nomeCentro((String)c.get("id"))));
        out.put("centros",centros);
        e = faceta(p,meses,"cargo");
        out.put("cargos",jdbc.queryForList("select distinct " + ConsultaFolha.CARGO + e.fromSimples() + " order by 1",e.params(),String.class));
        e = faceta(p,meses,"funcionario");
        String pesquisa = Objects.requireNonNullElse(p.getFirst("q"),"").trim();
        if (pesquisa.length()>255) throw PeriodoFolha.invalido("Pesquisa muito longa.");
        e.params().addValue("q",pesquisa);
        var ids = PeriodoFolha.valores(p,"funcionario").stream().map(Long::valueOf).toList();
        String prioridade = ids.isEmpty() ? "" : "(f.pessoa in (:selecionados)) desc,";
        String nomes = "position(lower(:q) in lower(coalesce(f.nome,'')))>0";
        if (!ids.isEmpty()) {
            e.params().addValue("selecionados",ids);
            nomes = "(" + nomes + " or f.pessoa in (:selecionados))";
        }
        out.put("funcionarios",jdbc.queryForList("select f.pessoa id,max(f.nome) nome " + e.from() + " and " + nomes
                + " group by f.pessoa order by " + prioridade + " nome,id limit " + (100+ids.size()),e.params()));
        out.put("rubricas",CatalogoRubricas.todas());
        var recorte = consulta.escopo(p,meses);
        String campos = CatalogoRubricas.todas().stream()
                .map(r -> "count(f." + r.coluna() + ")>0 as \"" + r.campo() + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        var preenchidas = jdbc.queryForMap("select " + campos + recorte.fromSimples(),recorte.params());
        out.put("rubricasDisponiveis",CatalogoRubricas.todas().stream().map(CatalogoRubricas.Rubrica::campo)
                .filter(c -> Boolean.TRUE.equals(preenchidas.get(c))).toList());
        return out;
    }

    private ConsultaFolha.Escopo faceta(MultiValueMap<String,String> p,List<LocalDate> meses,String ignorar) {
        var copia = new LinkedMultiValueMap<>(p);
        copia.remove(ignorar);
        return consulta.escopo(copia,meses);
    }

    public Map<String,Object> resumo(MultiValueMap<String,String> p) {
        PeriodoFolha periodo = PeriodoFolha.resolver(p,consulta.competencias());
        var e = consulta.escopo(p,periodo.meses());
        var b = consulta.escopo(p,periodo.base());
        String medida = selecao(p);
        int top = PeriodoFolha.numero(Objects.requireNonNullElse(p.getFirst("top"),"10"),1,100);
        String ordem = Objects.requireNonNullElse(p.getFirst("ordem"),"custo");
        if (!List.of("custo","quantidade","participacao","variacao").contains(ordem)) throw PeriodoFolha.invalido("Ordenação desconhecida.");
        boolean comparavel = periodo.comparavel();
        var atual = agregacoes.agregar(e,medida,true);
        var base = comparavel ? agregacoes.agregar(b,medida,false) : new Cubo(Map.of());
        BigDecimal custo = numero(atual.total(),"custo"), custoAnterior = numero(base.total(),"custo");
        var comp = composicao.calcular(e,b,comparavel,custo,custoAnterior);
        var series = evolucao.montar(p,periodo,atual);
        var centros = dimensao(atual.lista(CENTROS),base.lista(CENTROS),custo,custoAnterior,comparavel,ordem);
        for (var c : centros) {
            var valores = series.custosCentro().getOrDefault(c.get("id"),Map.of());
            c.put("variacaoMensal",valores.get("mensal"));
            c.put("variacaoAnual",valores.get("anual"));
        }
        var cargos = dimensao(atual.lista(CARGOS),base.lista(CARGOS),custo,custoAnterior,comparavel,ordem);
        var qualidade = composicao.qualidade(e,periodo,comp);
        var out = new LinkedHashMap<String,Object>();
        out.put("atual",atual.total());
        out.put("decomposicaoQuadro",comparavel ? decomporQuadro(atual.total(),base.total()) : Map.of());
        String rubrica = p.getFirst("rubrica"), grupo = p.getFirst("grupo");
        boolean selecaoAditiva = rubrica != null ? CatalogoRubricas.obter(rubrica).aditiva() : grupo != null && TITULOS.containsKey(grupo);
        out.put("saldoComposicao",selecaoAditiva ? custo.subtract(numero(atual.total(),"valor")) : null);
        out.put("anterior",comparavel ? base.total() : null);
        out.put("variacoes",variacoes(atual.total(),base.total(),comparavel));
        out.put("comparavel",comparavel);
        out.put("meses",periodo.meses());
        out.put("base",periodo.base());
        out.put("mensal",series.mensal());
        out.put("anual",series.anual());
        out.put("comparacoes",series.comparacoes());
        out.put("centros",centros);
        out.put("cargos",cargos);
        out.put("centrosMensais",atual.lista(CENTROS_MENSAIS));
        out.put("cargosMensais",atual.lista(CARGOS_MENSAIS));
        out.put("matriz",atual.lista(MATRIZ));
        var ranking = agregacoes.funcionarios(e,b,medida,top,ordem,comparavel,custo);
        out.put("funcionarios",ranking);
        boolean custoSelecionado = medida.equals(EXPRESSOES.get("custo")) && ("custo".equals(ordem) || "participacao".equals(ordem));
        out.put("custosFuncionarios",custoSelecionado ? ranking
                : agregacoes.funcionarios(e,b,EXPRESSOES.get("custo"),top,"custoIndividual",comparavel,custo));
        out.put("salarios",agregacoes.funcionarios(e,b,medida,top,"salario",comparavel,custo));
        out.put("histograma",pessoas.histograma(e));
        out.put("estatisticas",pessoas.estatisticas(e));
        out.put("rubricas",comp.rubricas());
        out.put("grupos",comp.grupos());
        out.put("qualidade",qualidade);
        out.put("insights",InsightsFolha.gerar(atual.total(),comparavel ? base.total() : null,centros,comp.grupos(),qualidade,series.mensal()));
        return out;
    }

    private static Map<String,Object> variacoes(Map<String,Object> atual,Map<String,Object> base,boolean comparavel) {
        var campos = new ArrayList<>(EXPRESSOES.keySet());
        campos.addAll(List.of("valor","quantidade","custoMedio","salarioMedio"));
        Map<String,Object> resultado = new LinkedHashMap<>();
        for (String campo : campos) {
            var linha = new LinkedHashMap<String,Object>();
            boolean temBase = comparavel && atual.get(campo)!=null && base.get(campo)!=null;
            comparar(linha,numero(atual,campo),temBase ? numero(base,campo) : null);
            resultado.put(campo,linha);
        }
        return resultado;
    }

    private static List<Map<String,Object>> dimensao(List<Map<String,Object>> atual, List<Map<String,Object>> base,
                                                      BigDecimal custo,BigDecimal custoAnterior,boolean comparavel,String ordem) {
        var antigos = EvolucaoFolha.indice(base);
        var mapa = EvolucaoFolha.indice(atual);
        // Centros que perderam todo o custo também explicam a diferença entre os períodos.
        for (var anterior : base) mapa.computeIfAbsent(anterior.get("id"),k -> {
            var m = vazio();
            m.put("id",k);
            m.put("nome",anterior.get("nome"));
            return m;
        });
        var linhas = new ArrayList<>(mapa.values());
        for (var m : linhas) {
            var antigo = antigos.getOrDefault(m.get("id"),Map.of());
            BigDecimal share = percentual(numero(m,"custo"),custo);
            BigDecimal shareAnterior = percentual(numero(antigo,"custo"),custoAnterior);
            m.put("participacao",share);
            m.put("mudancaParticipacao",comparavel && share!=null && shareAnterior!=null ? share.subtract(shareAnterior) : null);
            comparar(m,numero(m,"custo"),comparavel ? numero(antigo,"custo") : null);
            m.put("variacaoValor",comparavel ? numero(m,"valor").subtract(numero(antigo,"valor")) : null);
        }
        String campo = switch (ordem) {
            case "quantidade" -> "quantidade";
            case "variacao" -> "diferenca";
            case "participacao" -> "participacao";
            default -> "custo";
        };
        linhas.sort(Comparator.comparing((Map<String,Object> m) -> numero(m,campo)).reversed()
                .thenComparing(m -> String.valueOf(m.get("nome"))));
        return linhas;
    }
}

