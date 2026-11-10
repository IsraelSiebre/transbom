package com.insights.rh;

import com.insights.rh.importacao.ConciliacaoPagamentoService;
import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.Funcionario;
import com.insights.rh.insights.CalculoService;
import com.insights.rh.insights.model.FuncionarioCalculo;
import com.insights.rh.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ControllerMvcTest {
    private final ConciliacaoPagamentoService conciliacao = mock(ConciliacaoPagamentoService.class);
    private final CalculoService calculo = mock(CalculoService.class);
    private final FuncionarioRepository repository = mock(FuncionarioRepository.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new Controller(conciliacao, calculo, repository), new DetalhamentoController(calculo)).build();
    }

    @Test
    void paginasUsamViewsNoDiretorioRh() throws Exception {
        mvc.perform(get("/mao-de-obra/upload"))
                .andExpect(status().isOk()).andExpect(view().name("rh/upload"));
        mvc.perform(get("/mao-de-obra/funcionarios/sem-centro-custo"))
                .andExpect(status().isOk()).andExpect(view().name("rh/funcionarios-sem-cc"));
        mvc.perform(get("/mao-de-obra").param("competencia", "2025-01-01"))
                .andExpect(status().isOk()).andExpect(view().name("rh/dashboard"))
                .andExpect(model().attribute("competencia", LocalDate.of(2025, 1, 1)));
    }

    @Test
    void detalhamentoUsaCompetenciaSelecionadaEIncluiFuncionarioSemCentro() throws Exception {
        LocalDate competencia = LocalDate.of(2025, 1, 1);
        FuncionarioCalculo semCentro = mock(FuncionarioCalculo.class);
        FuncionarioCalculo comCentro = mock(FuncionarioCalculo.class);
        when(comCentro.centroDeCusto()).thenReturn(CentroDeCusto.TERM);
        List<FuncionarioCalculo> todos = List.of(semCentro, comCentro);
        when(calculo.calcularListaMOFuncionarios(competencia)).thenReturn(todos);

        mvc.perform(get("/mao-de-obra/detalhamento").param("competencia", "2025-01-01"))
                .andExpect(status().isOk()).andExpect(view().name("rh/detalhamento"))
                .andExpect(model().attribute("competencia", competencia))
                .andExpect(model().attribute("calculos", todos));
    }

    @Test
    void detalhamentoRenderizaValoresELinks() throws Exception {
        var resolver = new org.thymeleaf.templateresolver.ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        var engine = new org.thymeleaf.spring6.SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        var views = new org.thymeleaf.spring6.view.ThymeleafViewResolver();
        views.setTemplateEngine(engine);
        views.setCharacterEncoding("UTF-8");
        MockMvc htmlMvc = MockMvcBuilders.standaloneSetup(new DetalhamentoController(calculo))
                .setViewResolvers(views).build();
        FuncionarioCalculo funcionario = mock(FuncionarioCalculo.class);
        when(funcionario.nome()).thenReturn("Maria Teste");
        when(funcionario.salario()).thenReturn(new java.math.BigDecimal("1234.56"));
        when(calculo.calcularListaMOFuncionarios(LocalDate.of(2025, 1, 1))).thenReturn(List.of(funcionario));
        htmlMvc.perform(get("/mao-de-obra/detalhamento").param("competencia", "2025-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Maria Teste")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("1.234,56")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/mao-de-obra")));
    }
    @Test
    void camposVaziosPreservamCorrespondenciaDosFuncionarios() throws Exception {
        Funcionario segundo = new Funcionario();
        when(repository.findById(2L)).thenReturn(Optional.of(segundo));
        mvc.perform(post("/mao-de-obra/funcionarios/sem-centro-custo")
                        .param("funcionarioIds", "1", "2")
                        .param("centrosDeCusto", "", "TERM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mao-de-obra/funcionarios/sem-centro-custo"));
        org.junit.jupiter.api.Assertions.assertEquals(CentroDeCusto.TERM, segundo.getCentroDeCusto());
        verify(repository, never()).findById(1L);
        verify(repository).saveAll(List.of(segundo));
    }
}
