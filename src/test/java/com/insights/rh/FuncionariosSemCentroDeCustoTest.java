package com.insights.rh;

import com.insights.rh.importacao.ConciliacaoPagamentoService;
import com.insights.rh.importacao.dadosantigos.ImportacaoFolhaExcelService;
import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.insights.CalculoService;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
@Import(CalculoService.class)
@WithMockUser
class FuncionariosSemCentroDeCustoTest {
    @Autowired MockMvc mvc;
    @MockitoBean FuncionarioImportacaoRepository repository;
    @MockitoBean ConciliacaoPagamentoService conciliacao;
    @MockitoBean ImportacaoFolhaExcelService importacaoExcel;

    @Test
    void mantemCompetenciaDoDashboardAoListarESalvarPendencias() throws Exception {
        LocalDate mes = LocalDate.of(2024, 2, 1);
        String pagina = "/mao-de-obra/funcionarios/sem-centro-custo";
        String link = pagina + "?competencia=" + mes;
        FuncionarioImportacao pendente = new FuncionarioImportacao();
        pendente.setId(12L);
        pendente.setNome("Ana Pendente");
        pendente.setCompetencia(mes);
        FuncionarioImportacao alocado = new FuncionarioImportacao();
        alocado.setNome("Pedro Alocado");
        alocado.setCentroDeCusto(CentroDeCusto.COM);
        when(repository.findAllByCompetencia(mes)).thenReturn(List.of(pendente, alocado));
        when(repository.countByCompetenciaAndCentroDeCustoIsNull(mes)).thenReturn(1L);

        mvc.perform(get("/mao-de-obra").param("competencia", mes.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("qntFuncionariosSemCC", 1L))
                .andExpect(content().string(containsString("href=\"" + link + "\"")));

        String html = mvc.perform(get(link))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(pendente.getNome())))
                .andExpect(content().string(not(containsString(alocado.getNome()))))
                .andReturn().getResponse().getContentAsString();
        assertThat(html).containsPattern("<input[^>]*name=\"competencia\"[^>]*value=\"" + mes + "\"");
        mvc.perform(get(pagina).param("mes", "2024-02"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("competencia", mes))
                .andExpect(content().string(containsString(pendente.getNome())));

        when(repository.findAllById(List.of(12L))).thenReturn(List.of(pendente));
        mvc.perform(post(pagina).with(csrf()).param("competencia", mes.toString())
                        .param("funcionarioIds", "12").param("centrosDeCusto", "ADM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(link));
        verify(repository).saveAll(List.of(pendente));
        assertThat(pendente.getCentroDeCusto()).isEqualTo(CentroDeCusto.ADM);
        mvc.perform(get(link))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(pendente.getNome()))));
    }
}
