package com.insights.rh.analytics;

import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.insights.CalculoService;
import jakarta.persistence.Column;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnabledIfEnvironmentVariable(named="FOLHA_TEST_URL", matches="jdbc:postgresql:.*")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FolhaPostgresTest {
    private SingleConnectionDataSource conexao;
    private AnnotationConfigApplicationContext contexto;
    private final List<FuncionarioImportacao> folha = new ArrayList<>();
    private MaoDeObraAnalyticsService painel;
    private DetalheFolhaService detalhe;

    @BeforeAll void preparar() throws Exception {
        conexao = new SingleConnectionDataSource(System.getenv("FOLHA_TEST_URL"),
                System.getenv("FOLHA_TEST_USER"), System.getenv("FOLHA_TEST_PASSWORD"), true);
        var jdbc = new NamedParameterJdbcTemplate(conexao);
        var campos = Arrays.stream(FuncionarioImportacao.class.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class)).toList();
        var ddl = new StringJoiner(", ", "create temporary table funcionario_calculo(id bigint primary key, ", ")");
        for (Field f : campos) {
            var tipo = f.getType();
            ddl.add(f.getAnnotation(Column.class).name() + " " + (tipo == BigDecimal.class ? "numeric(19,2)" :
                    tipo == LocalDate.class ? "date" : tipo == Integer.class ? "integer" : "text"));
        }
        jdbc.getJdbcTemplate().execute(ddl.toString());
        adicionar(1, "2024-01-01", 7, "Ana", "Motorista", CentroDeCusto.ADM, "900", null);
        adicionar(2, "2025-01-01", 7, "Ana", "Motorista", CentroDeCusto.ADM, "1000", "12345678909");
        adicionar(3, "2025-01-01", 8, "Ana", "Auxiliar", null, "500", null);
        adicionar(4, "2025-01-01", 7, "Bia", "Auxiliar", CentroDeCusto.COM, "800", null);
        adicionar(5, "2025-02-01", 7, "Ana", "Supervisora", CentroDeCusto.COM, "1200", null);
        adicionar(6, "2025-02-01", 7, "Ana", "Supervisora", CentroDeCusto.COM, "100", null);
        adicionar(7, "2025-04-01", null, "Sem identificação", null, null, null, null);
        for (FuncionarioImportacao f : folha) {
            var valores = new LinkedHashMap<String,Object>();
            valores.put("id", f.getId());
            for (Field campo : campos) {
                campo.setAccessible(true);
                Object v = campo.get(f);
                valores.put(campo.getAnnotation(Column.class).name(), v instanceof Enum<?> e ? e.name() : v);
            }
            jdbc.update("insert into pg_temp.funcionario_calculo ("+String.join(",",valores.keySet())
                    +") values (:"+String.join(",:",valores.keySet())+")", valores);
        }
        contexto = new AnnotationConfigApplicationContext();
        contexto.registerBean(NamedParameterJdbcTemplate.class, () -> jdbc);
        contexto.scan("com.insights.rh.analytics");
        contexto.refresh();
        painel = contexto.getBean(MaoDeObraAnalyticsService.class);
        detalhe = contexto.getBean(DetalheFolhaService.class);
    }

    private void adicionar(long id, String data, Integer codigo, String nome, String cargo, CentroDeCusto centro,
                           String salario, String cpf) {
        var f = new FuncionarioImportacao();
        f.setId(id);
        f.setCompetencia(LocalDate.parse(data));
        f.setCodigoExterno(codigo);
        f.setNome(nome);
        f.setCargo(cargo);
        f.setCentroDeCusto(centro);
        f.setDataAdmissao(LocalDate.of(2020,1,1));
        f.setSalario(salario == null ? null : new BigDecimal(salario));
        f.setCpf(cpf);
        f.setFgts(new BigDecimal("1.37"));
        f.setComissoesPorFora(new BigDecimal("51.27"));
        f.setValorBestPontos(new BigDecimal("9876.54"));
        f.recalcular();
        folha.add(f);
    }

    @AfterAll void fechar() {
        if (contexto != null) contexto.close();
        if (conexao != null) conexao.destroy();
    }
    static MultiValueMap<String,String> filtros(String... pares) {
        var m = new LinkedMultiValueMap<String,String>();
        for (int i=0;i<pares.length;i+=2) m.add(pares[i],pares[i+1]);
        return m;
    }
    @SuppressWarnings("unchecked")
    static Map<String,Object> mapa(Map<String,Object> r,String k) { return (Map<String,Object>)r.get(k); }
    @SuppressWarnings("unchecked")
    static List<Map<String,Object>> linhas(Map<String,Object> r,String k) { return (List<Map<String,Object>>)r.get(k); }
    static BigDecimal n(Map<String,Object> r,String k) {return new BigDecimal(r.get(k).toString());}
    BigDecimal total(long... ids) {
        var servico = new CalculoService(null);
        var registros = folha.stream().filter(f->Arrays.stream(ids).anyMatch(id->f.getId()==id))
                .map(servico::converterParaRecord).toList();
        return servico.calcularTotal(registros).custoMoTotal();
    }

    @Test void conciliaAgregadosComServicoEDetalhes() {
        var p = filtros("competencia","2025-01-22","comparar","anual");
        var r = painel.resumo(p);
        var totais = detalhe.listar(p).totais();
        assertThat(n(totais,"custo")).isEqualByComparingTo(total(2,3,4));
        for(String k : List.of("centros","cargos","funcionarios","mensal","anual")) {
            var soma = linhas(r,k).stream().map(l->n(l,"custo")).reduce(BigDecimal.ZERO,BigDecimal::add);
            assertThat(soma).as(k).isEqualByComparingTo(total(2,3,4));
        }
        assertThat(n(linhas(r,"mensal").getFirst(),"quantidade")).isEqualByComparingTo("3");
        assertThat(n(mapa(r,"estatisticas"),"mediana")).isEqualByComparingTo("800");
        var adm = linhas(r,"centros").stream().filter(l->"ADM".equals(l.get("id"))).findFirst().orElseThrow();
        assertThat(n(adm,"diferenca")).isEqualByComparingTo(total(2).subtract(total(1)));
    }

    @Test void duplicaLancamentoFinanceiroMasContaPessoaUmaVez() {
        var r = painel.resumo(filtros("competencia","2025-02-01","centro","COM","cargo","Supervisora",
                "funcionario","1","rubrica","fgts"));
        var f = linhas(r,"funcionarios").getFirst();
        assertThat(n(f,"custo")).isEqualByComparingTo(total(5,6));
        assertThat(n(f,"quantidade")).isEqualByComparingTo("1");
        assertThat(n(f,"custoMedio")).isEqualByComparingTo(total(5,6));
        assertThat(n(f,"valor")).isEqualByComparingTo("2.74");
    }

    @Test void publicaSomenteAComissaoCalculadaNoResumoDetalheEPerfil() {
        var p = filtros("competencia", "2025-01", "funcionario", "1", "agrupar", "cargo", "rubrica", "comissoesSemProvisao");
        var filtros = painel.filtros(p);
        assertThat((List<?>) filtros.get("rubricas")).isEqualTo(CatalogoRubricas.todas());
        assertThat(((List<?>) filtros.get("rubricasDisponiveis")).stream().map(Object::toString).toList())
                .contains("comissoesSemProvisao").doesNotContain("comissoesPorFora", "valorBestPontos");
        var resumo = painel.resumo(p);
        assertThat(linhas(resumo, "rubricas")).extracting(r -> r.get("id"))
                .containsOnlyOnce("comissoesSemProvisao").doesNotContain("comissoesPorFora", "valorBestPontos");
        assertThat(n(mapa(resumo, "atual"), "valor")).isEqualByComparingTo("51.27");
        assertThat(n(mapa(resumo, "atual"), "custo")).isEqualByComparingTo(total(2));
        var pagina = detalhe.listar(p);
        var perfil = linhas(detalhe.perfil(1, p), "historico").getFirst();
        for (var valores : List.of(pagina.totais(), pagina.subtotais().getFirst(), mapa(pagina.linhas().getFirst(), "rubricas"), mapa(perfil, "rubricas"))) {
            assertThat(valores).doesNotContainKeys("comissoesPorFora", "valorBestPontos");
            assertThat(n(valores, "comissoesSemProvisao")).isEqualByComparingTo("51.27");
        }
    }

    @Test void camposDeOrigemNaoPodemSerSolicitadosPorFiltroColunaOuOrdenacao() {
        for (String origem : List.of("comissoesPorFora", "valorBestPontos")) {
            assertThatThrownBy(() -> painel.filtros(filtros("rubrica", origem))).isInstanceOf(ResponseStatusException.class);
            assertThatThrownBy(() -> detalhe.listar(filtros("colunas", origem))).isInstanceOf(ResponseStatusException.class);
            assertThatThrownBy(() -> detalhe.listar(filtros("ordenar", origem))).isInstanceOf(ResponseStatusException.class);
            assertThatThrownBy(() -> ExportacaoFolhaService.colunas("funcionarios", filtros("colunas", origem)))
                    .isInstanceOf(ResponseStatusException.class);
        }
    }

    @Test void csvEXlsxDeFuncionariosERubricasPublicamSomenteAComissaoCalculada() throws Exception {
        var exportacao = contexto.getBean(ExportacaoFolhaService.class);
        var p = filtros("competencia", "2025-01", "funcionario", "1");
        for (String tipo : List.of("funcionarios", "rubricas")) {
            var csv = new ByteArrayOutputStream();
            exportacao.escrever(tipo, "csv", p, csv);
            assertThat(csv.toString(StandardCharsets.UTF_8)).contains("Comissões sem provisão", "\"51,27\"")
                    .doesNotContain("Comissões por fora", "Valor Best Pontos", "9876,54");
            var xlsx = new ByteArrayOutputStream();
            exportacao.escrever(tipo, "xlsx", p, xlsx);
            try (var livro = new XSSFWorkbook(new ByteArrayInputStream(xlsx.toByteArray()))) {
                var textos = new ArrayList<String>();
                var numeros = new ArrayList<Double>();
                livro.getSheetAt(0).forEach(linha -> linha.forEach(celula -> {
                    if (celula.getCellType() == CellType.STRING) textos.add(celula.getStringCellValue());
                    if (celula.getCellType() == CellType.NUMERIC) numeros.add(celula.getNumericCellValue());
                }));
                assertThat(textos).contains("Comissões sem provisão").doesNotContain("Comissões por fora", "Valor Best Pontos");
                assertThat(numeros).contains(51.27).doesNotContain(9876.54);
            }
        }
    }

    @Test void mesesAusentesEHomonimosPreservadosNoHistorico() {
        var p = filtros("inicio","2025-01","fim","2025-04");
        var r = painel.resumo(p);
        var anual = linhas(r,"anual").getFirst();
        assertThat(n(anual,"quantidade")).isEqualByComparingTo("4");
        assertThat(n(anual,"custo")).isEqualByComparingTo(total(2,3,4,5,6,7));
        var mes = linhas(r,"mensal").stream().filter(l->l.get("competencia").toString().startsWith("2025-03")).findFirst().orElseThrow();
        assertThat(mes.get("importado")).isEqualTo(false);
        var perfil = linhas(detalhe.perfil(1,p),"historico");
        assertThat(perfil).hasSize(2);
        assertThat(perfil).extracting(l->l.get("cargo")).containsExactly("Motorista","Supervisora");
        assertThat(perfil).extracting(l->l.get("centro")).containsExactly("Administrativo","Comercial");
    }

    @Test void comparaAnoAcumuladoMesEExplicaDiferencaAoCentavo() {
        var r = painel.resumo(filtros("competencia","2025-01","comparar","anual"));
        BigDecimal atual = total(2,3,4), base = total(1), delta = atual.subtract(base);
        assertThat(r.get("comparavel")).isEqualTo(true);
        assertThat(n(mapa(r,"anterior"),"custo")).isEqualByComparingTo(base);
        assertThat(n(linhas(r,"anual").getFirst(),"diferenca")).isEqualByComparingTo(delta);
        var comps = linhas(r,"comparacoes");
        assertThat(comps.getFirst().get("anterior")).isNull();
        for (var linha : comps.subList(1,3)) {
            assertThat(n(linha,"diferenca")).isEqualByComparingTo(delta);
            assertThat(n(linha,"variacao")).isEqualByComparingTo(delta.movePointRight(2).divide(base,2,java.math.RoundingMode.HALF_UP));
        }
        assertThat(linhas(r,"grupos").stream().map(m->n(m,"diferenca")).reduce(BigDecimal.ZERO,BigDecimal::add))
                .isEqualByComparingTo(delta);
        assertThat(linhas(r,"rubricas").stream().filter(m->Boolean.TRUE.equals(m.get("aditiva")))
                .map(m->n(m,"valor")).reduce(BigDecimal.ZERO,BigDecimal::add)).isEqualByComparingTo(atual);
        var fev = painel.resumo(filtros("competencia","2025-02"));
        assertThat(n(linhas(fev,"comparacoes").getFirst(),"diferenca")).isEqualByComparingTo(total(5,6).subtract(atual));
        assertThat(n(linhas(fev,"comparacoes").getLast(),"atual")).isEqualByComparingTo(total(2,3,4,5,6));
        assertThat(linhas(fev,"comparacoes").getLast().get("anterior")).isNull();
    }

    @Test void explicitaRegistrosPendentesDuplicadosIdentidadeAusenteEMesesSemImportacao() {
        var r = painel.resumo(filtros("inicio","2025-01","fim","2025-04"));
        var q = mapa(r,"qualidade");
        assertThat(n(q,"semCpf")).isEqualByComparingTo("5");
        assertThat(n(q,"semCentro")).isEqualByComparingTo("2");
        assertThat(n(q,"semIdentificador")).isEqualByComparingTo("1");
        assertThat(n(q,"semCargo")).isEqualByComparingTo("1");
        assertThat(n(q,"duplicados")).isEqualByComparingTo("1");
        assertThat(n(q,"pendente")).isEqualByComparingTo(total(3,7));
        assertThat(n(q,"classificado").add(n(q,"pendente"))).isEqualByComparingTo(n(mapa(r,"atual"),"custo"));
        assertThat((List<?>)q.get("ausentes")).isEqualTo(List.of(LocalDate.of(2025,3,1)));
        assertThat((List<?>)q.get("rubricasNulas")).isNotEmpty();
        assertThat(r.get("comparavel")).isEqualTo(false);
    }

    @Test void admissaoInstavelNaoFragmentaLegadoECpfsConflitantesIsolamLinhasSemDocumento() throws Exception {
        var jdbc = new NamedParameterJdbcTemplate(conexao);
        var conn = conexao.getConnection();
        conn.setAutoCommit(false);
        try {
            jdbc.getJdbcTemplate().execute("""
                    insert into pg_temp.funcionario_calculo(id,competencia,nome,cpf,codigo_externo,data_admissao) values
                    (101,'2028-01-01','Colaborador A',null,71,'2019-01-01'),
                    (102,'2028-02-01','COLABORADOR A',null,71,'2026-01-01'),
                    (103,'2028-01-01','Pessoa B','32176294885',72,null),
                    (104,'2028-02-01','Pessoa B','63057213834',72,null),
                    (105,'2028-01-01','Pessoa B',null,72,null),
                    (106,'2028-02-01','Pessoa B',null,72,null)
                    """);
            var r = painel.resumo(filtros("inicio","2028-01","fim","2028-02"));
            assertThat(n(mapa(r,"atual"),"quantidade")).isEqualByComparingTo("5");
            var q = mapa(r,"qualidade");
            assertThat(n(q,"admissaoInstavel")).isEqualByComparingTo("2");
            assertThat(n(q,"identidadeAmbigua")).isEqualByComparingTo("4");
            assertThat(n(q,"semIdentificador")).isEqualByComparingTo("2");
            assertThat(linhas(q,"porCompetencia")).hasSize(2);
        } finally {
            conn.rollback();
            conn.setAutoCommit(true);
        }
    }

    @Test void paginacaoBuscaSubtotaisEOrdenacaoUsamAMesmaSelecao() {
        var p = filtros("periodo","historico","busca","Ana","tamanho","1","ordenar","salario","direcao","desc","agrupar","cargo");
        var pagina = detalhe.listar(p);
        assertThat(pagina.totalRegistros()).isEqualTo(5);
        assertThat(pagina.linhas()).hasSize(1);
        p.set("pagina","1");
        var segunda = detalhe.listar(p);
        assertThat(segunda.linhas().getFirst().get("id")).isNotEqualTo(pagina.linhas().getFirst().get("id"));
        assertThat(segunda.totais()).isEqualTo(pagina.totais());
        assertThat(pagina.subtotais().stream().map(l->n(l,"custo")).reduce(BigDecimal.ZERO,BigDecimal::add))
                .isEqualByComparingTo(total(1,2,3,5,6));
        assertThat(detalhe.listar(filtros("competencia","2030-01")).totalRegistros()).isZero();
    }

    @Test void conciliaFaixaSalarialDoHistogramaComObservacoesIndividuais() {
        var escopo = contexto.getBean(ConsultaFolha.class).filtrar(filtros("competencia","2025-02"));
        var pessoas = contexto.getBean(AnalisesPessoas.class);
        var observacoes = pessoas.observacoes(escopo,1,0);
        assertThat(observacoes).hasSize(1);
        assertThat(n(observacoes.getFirst(),"salario")).isEqualByComparingTo("1300");
        var faixas = pessoas.histograma(escopo);
        assertThat(faixas).hasSize(1);
        assertThat(n(faixas.getFirst(),"quantidade")).isEqualByComparingTo("1");
    }
}
