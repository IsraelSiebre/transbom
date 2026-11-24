package com.insights.rh.analytics;

import com.insights.rh.importacao.model.CentroDeCusto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class DetalheFolhaService {
    private final NamedParameterJdbcTemplate jdbc;
    private final ConsultaFolha consulta;

    private static final Map<String, String> METRICAS = Map.of(
            "encargos", CatalogoRubricas.sqlGrupo("encargos"), "beneficios", CatalogoRubricas.sqlGrupo("beneficios"),
            "descontos", CatalogoRubricas.obter("descontosTotais").sql(), "custo", CatalogoRubricas.obter("custoMoTotal").sql(),
            "provisoes", CatalogoRubricas.sqlGrupo("provisoes"));
    static final Map<String, String> CAMPOS = campos();
    static final List<String> BASE = List.of("nome", "competencia", "cargo", "centro", "dataAdmissao", "salario",
            "fgts", "encargos", "beneficios", "descontos", "custo");

    private static Map<String, String> campos() {
        Map<String, String> campos = new LinkedHashMap<>();
        campos.put("nome", "f.nome");
        campos.put("competencia", "f.competencia");
        campos.put("cargo", "f.cargo");
        campos.put("centro", "f.centro_de_custo");
        campos.put("dataAdmissao", "f.data_admissao");
        campos.putAll(METRICAS);
        CatalogoRubricas.todas().forEach(r -> campos.put(r.campo(), r.sql()));
        return Collections.unmodifiableMap(campos);
    }

    public record Pagina(List<Map<String,Object>> linhas, long totalRegistros, int pagina, int tamanho,
                         Map<String,Object> totais, List<Map<String,Object>> subtotais) {}

    public Pagina listar(MultiValueMap<String,String> p) {
        validarColunas(p.getOrDefault("colunas", List.of()));
        int pagina = inteiro(p, "pagina", 0, 0, Integer.MAX_VALUE);
        int tamanho = inteiro(p, "tamanho", 25, 1, 100);
        var escopo = comBusca(p);
        String grupo = grupo(p);
        var params = escopo.params();
        String agregados = agregados();
        Map<String,Object> totais = jdbc.queryForMap("select count(*) as registros," + agregados + escopo.fromSimples(), params);
        List<Map<String,Object>> subtotais = grupo == null ? List.of() : jdbc.queryForList("select " + grupo +
                " as grupo,count(*) as registros," + agregados + escopo.fromSimples() + " group by 1 order by 1 nulls last", params);
        if ("centro".equals(p.getFirst("agrupar"))) {
            subtotais.forEach(row -> row.put("grupo", descricaoCentro((String)row.get("grupo"))));
        }
        List<Map<String,Object>> linhas = jdbc.query(selecao() + escopo.from() + ordem(p) + " limit :tam offset :inicioPagina",
                params.addValue("tam", tamanho).addValue("inicioPagina", (long) pagina * tamanho),
                (rs, i) -> lerLinha(rs));
        return new Pagina(linhas, ((Number)totais.get("registros")).longValue(), pagina, tamanho, totais, subtotais);
    }

    /** O cursor JDBC percorre a mesma seleção da grade, em uma transação de leitura. */
    public void percorrer(MultiValueMap<String,String> p, List<String> colunas, Consumer<List<Object>> destino) {
        var escopo = comBusca(p);
        String sql = "select " + colunas.stream().map(c -> BASE.contains(c) || METRICAS.containsKey(c)
                ? CAMPOS.get(c) : "f." + CatalogoRubricas.obter(c).coluna()).collect(Collectors.joining(","))
                + escopo.fromSimples() + ordem(p);
        jdbc.execute(sql, escopo.params(), ps -> {
            ps.setFetchSize(250);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    List<Object> linha = new ArrayList<>(colunas.size());
                    for (int i = 0; i < colunas.size(); i++) {
                        Object valor = rs.getObject(i + 1);
                        linha.add(colunas.get(i).equals("centro") ? descricaoCentro((String) valor) : valor);
                    }
                    destino.accept(linha);
                }
            }
            return null;
        });
    }

    private record ConsultaDetalhe(String from, String fromSimples, MapSqlParameterSource params) {}

    private ConsultaDetalhe comBusca(MultiValueMap<String,String> p) {
        var escopo = consulta.filtrar(p);
        String busca = p.getFirst("busca");
        String predicado = "";
        if (busca != null && !busca.isBlank()) {
            if (busca.length() > 160) throw invalido("Busca muito longa.");
            String literal = busca.strip().replace("!", "!!").replace("%", "!%").replace("_", "!_");
            escopo.params().addValue("buscaDetalhes", "%" + literal + "%");
            predicado = " and (f.nome ilike :buscaDetalhes escape '!' or f.cargo ilike :buscaDetalhes escape '!')";
        }
        return new ConsultaDetalhe(escopo.from() + predicado, escopo.fromSimples() + predicado, escopo.params());
    }

    /** Médias da equipe com a mesma competência e a mesma lotação, incluindo a própria pessoa. */
    public Map<String,Object> perfil(long pessoa, MultiValueMap<String,String> p) {
        var parametros = new LinkedMultiValueMap<>(p);
        parametros.remove("funcionario");
        var escopo = consulta.filtrar(parametros);
        String consultaPares = """
                with base as materialized (
                    select f.id, f.pessoa, f.competencia, f.cargo, f.centro_de_custo, f.custo_mo_total %s
                ), por_cargo as (
                    select competencia, cargo, round(sum(coalesce(custo_mo_total,0)) / count(distinct pessoa),2) media
                    from base group by competencia, cargo
                ), por_centro as (
                    select competencia, centro_de_custo, round(sum(coalesce(custo_mo_total,0)) / count(distinct pessoa),2) media
                    from base group by competencia, centro_de_custo
                )
                select f.competencia, f.cargo, f.centro_de_custo, max(f.nome) nome, min(f.data_admissao) admissao,
                       count(*) registros, c.media media_cargo, cc.media media_centro, %s
                from base identificada
                join funcionario_calculo f on f.id=identificada.id
                join por_cargo c on c.competencia=f.competencia and c.cargo is not distinct from f.cargo
                join por_centro cc on cc.competencia=f.competencia and cc.centro_de_custo is not distinct from f.centro_de_custo
                where identificada.pessoa=:perfilPessoa
                group by f.competencia, f.cargo, f.centro_de_custo, c.media, cc.media
                order by f.competencia, f.cargo, f.centro_de_custo
                """.formatted(escopo.from(), agregados());
        List<Map<String,Object>> historico = jdbc.query(consultaPares, escopo.params().addValue("perfilPessoa", pessoa),
                (rs, i) -> {
                    Map<String,Object> linha = new LinkedHashMap<>();
                    linha.put("competencia", rs.getObject("competencia", LocalDate.class));
                    linha.put("cargo", rs.getString("cargo"));
                    linha.put("centro", descricaoCentro(rs.getString("centro_de_custo")));
                    linha.put("dataAdmissao", rs.getObject("admissao", LocalDate.class));
                    linha.put("nome", rs.getString("nome"));
                    linha.put("registros", rs.getLong("registros"));
                    linha.put("salario", rs.getBigDecimal("salario"));
                    linha.put("mediaCargo", rs.getBigDecimal("media_cargo"));
                    linha.put("mediaCentro", rs.getBigDecimal("media_centro"));
                    for (String chave : METRICAS.keySet()) linha.put(chave, rs.getBigDecimal(chave));
                    Map<String,BigDecimal> rubricas = new LinkedHashMap<>();
                    for (var r : CatalogoRubricas.todas()) rubricas.put(r.campo(), rs.getBigDecimal(r.campo()));
                    linha.put("rubricas", rubricas);
                    return linha;
                });
        return Map.of("historico", historico);
    }

    private static String selecao() {
        String rubricas = CatalogoRubricas.todas().stream().map(r -> "f." + r.coluna() + " as \"" + r.campo() + "\"")
                .collect(Collectors.joining(","));
        return "select f.id," + ConsultaFolha.PESSOA + " as pessoa,f.nome,f.cargo,f.competencia,f.centro_de_custo,"
                + "f.data_admissao," + rubricas + "," + metricas(false) + " ";
    }

    private static String agregados() {
        return CatalogoRubricas.todas().stream().map(r -> "coalesce(sum(f." + r.coluna() + "),0) as \"" + r.campo() + "\"")
                .collect(Collectors.joining(",")) + "," + metricas(true) + " ";
    }

    private static String metricas(boolean soma) {
        return METRICAS.entrySet().stream().map(e -> (soma ? "coalesce(sum(" + e.getValue() + "),0)" : e.getValue())
                + " as " + e.getKey()).collect(Collectors.joining(","));
    }

    private static Map<String,Object> lerLinha(ResultSet rs) throws SQLException {
        Map<String,Object> row = new LinkedHashMap<>();
        row.put("id", rs.getLong("id"));
        row.put("pessoa", rs.getString("pessoa"));
        row.put("nome", rs.getString("nome"));
        row.put("cargo", rs.getString("cargo"));
        row.put("centro", descricaoCentro(rs.getString("centro_de_custo")));
        row.put("competencia", rs.getObject("competencia", LocalDate.class));
        row.put("dataAdmissao", rs.getObject("data_admissao", LocalDate.class));
        row.put("salario", Objects.requireNonNullElse(rs.getBigDecimal("salario"), BigDecimal.ZERO));
        row.put("fgts", Objects.requireNonNullElse(rs.getBigDecimal("fgts"), BigDecimal.ZERO));
        for (String campo : METRICAS.keySet()) row.put(campo, rs.getBigDecimal(campo));
        Map<String,Object> rubricas = new LinkedHashMap<>();
        for (var r : CatalogoRubricas.todas()) rubricas.put(r.campo(), rs.getBigDecimal(r.campo()));
        row.put("rubricas", rubricas);
        return row;
    }

    static String descricaoCentro(String codigo) {
        if (codigo == null) return "Sem centro de custo";
        return CentroDeCusto.valueOf(codigo).getDescricao();
    }

    static void validarColunas(List<String> colunas) {
        if (!CAMPOS.keySet().containsAll(colunas)) throw invalido("Coluna desconhecida.");
    }

    private static String ordem(MultiValueMap<String,String> p) {
        String campo = Objects.requireNonNullElse(p.getFirst("ordenar"), "competencia");
        String direcao = Objects.requireNonNullElse(p.getFirst("direcao"), "desc");
        if (!CAMPOS.containsKey(campo) || !Set.of("asc","desc").contains(direcao)) throw invalido("Ordenação inválida.");
        String grupo = grupo(p);
        return " order by " + (grupo == null ? "" : grupo + " nulls last,") + CAMPOS.get(campo) + " " + direcao
                + " nulls last, f.id";
    }

    private static String grupo(MultiValueMap<String,String> p) {
        String chave = Objects.requireNonNullElse(p.getFirst("agrupar"), "");
        return switch (chave) {
            case "" -> null;
            case "cargo", "centro" -> CAMPOS.get(chave);
            default -> throw invalido("Agrupamento inválido.");
        };
    }

    private static int inteiro(MultiValueMap<String,String> p, String nome, int padrao, int min, int max) {
        try {
            int valor = Integer.parseInt(Objects.requireNonNullElse(p.getFirst(nome), String.valueOf(padrao)));
            if (valor >= min && valor <= max) return valor;
        } catch (NumberFormatException ignored) {}
        throw invalido("Paginação inválida.");
    }

    static ResponseStatusException invalido(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
