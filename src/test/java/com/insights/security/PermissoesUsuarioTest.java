package com.insights.security;

import com.insights.rh.Controller;
import com.insights.rh.importacao.ConciliacaoPagamentoService;
import com.insights.rh.importacao.dadosantigos.ImportacaoFolhaExcelService;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({Controller.class, UsuarioController.class})
@Import(SecurityConfig.class)
class PermissoesUsuarioTest {
    private static final String FOLHA = "/mao-de-obra/folha";
    private static final String CONCILIAR = "/mao-de-obra/extracao/processar";
    private static final String CADASTRO = "/usuarios/novo";
    private static final String CLASSIFICAR = "/mao-de-obra/funcionarios/sem-centro-custo";
    private static final List<String> PAGINAS_ADMIN = List.of(FOLHA, "/mao-de-obra/upload", CADASTRO);

    @Autowired MockMvc mvc;
    @MockitoBean UsuarioService usuarios;
    @MockitoBean ImportacaoFolhaExcelService importacao;
    @MockitoBean ConciliacaoPagamentoService conciliacao;
    @MockitoBean FuncionarioImportacaoRepository registros;

    private MockHttpServletRequestBuilder cadastrar() {
        return post(CADASTRO).param("username", "nova.conta").param("senha", "senha-teste")
                .param("confirmacaoSenha", "senha-teste");
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void perfisAcessamFuncoesComunsEVeemSeuMenu(Role role) throws Exception {
        var sessao = user("conta").roles(role.name());
        for (String pagina : List.of("/mao-de-obra", "/mao-de-obra/detalhamento", "/mao-de-obra/guia", CLASSIFICAR, "/minha-conta/senha")) {
            String html = mvc.perform(get(pagina).with(sessao)).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(html).contains("data-classificacao", "href=\"/minha-conta/senha\"",
                    "href=\"/mao-de-obra/guia\"", "src=\"/global/logo_transbom.png\"");
            for (String caminho : PAGINAS_ADMIN) {
                assertThat(html.contains("href=\"" + caminho + "\"")).as(caminho).isEqualTo(role == Role.ADMIN);
            }
        }
        mvc.perform(post(CLASSIFICAR).with(sessao).with(csrf())
                        .param("funcionarioIds", "1").param("centrosDeCusto", "ADM"))
                .andExpect(redirectedUrl(CLASSIFICAR));
    }

    @Test
    void visualizadorRecebe403AoAbrirOuEnviarOperacoesAdministrativas() throws Exception {
        var sessao = user("leitor").roles("VISUALIZADOR");
        for (String pagina : PAGINAS_ADMIN) {
            mvc.perform(get(pagina).with(sessao)).andExpect(status().isForbidden());
        }
        mvc.perform(cadastrar().param("role", "ADMIN").with(sessao).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(multipart(FOLHA).file("arquivo", new byte[]{1}).with(sessao).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(multipart(CONCILIAR).file("folha", new byte[]{1}).file("porFora", new byte[]{2})
                .with(sessao).with(csrf())).andExpect(status().isForbidden());
        verifyNoInteractions(usuarios, importacao, conciliacao);
    }

    @Test
    void administradorPodeAbrirEProcessarAsDuasImportacoes() throws Exception {
        var sessao = user("gestor").roles("ADMIN");
        for (String pagina : PAGINAS_ADMIN) {
            mvc.perform(get(pagina).with(sessao)).andExpect(status().isOk());
        }
        mvc.perform(multipart(FOLHA).file("arquivo", new byte[]{1}).with(sessao).with(csrf())).andExpect(status().isNoContent());
        var registro = new FuncionarioImportacao();
        registro.setNome("Pessoa de teste");
        registro.setCompetencia(LocalDate.of(2025, 1, 1));
        registro.setComissoesPorFora(new BigDecimal("51.27"));
        registro.setValorBestPontos(new BigDecimal("194.83"));
        registro.recalcular();
        when(conciliacao.processar(any(), any())).thenReturn(List.of(registro));
        mvc.perform(multipart(CONCILIAR).file("folha", new byte[]{1}).file("porFora", new byte[]{2})
                .with(sessao).with(csrf())).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comissoesSemProvisao").value(51.27))
                .andExpect(jsonPath("$[0].comissoesPorFora").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[0].valorBestPontos").doesNotHaveJsonPath());
        verify(importacao).importar(any());
        verify(conciliacao).processar(any(), any());
    }

    @Test
    void cadastroUsaVisualizadorPorPadraoEAceitaAdministradorExplicito() throws Exception {
        var sessao = user("gestor").roles("ADMIN");
        mvc.perform(get(CADASTRO).with(sessao)).andExpect(model().attribute("perfilSelecionado", Role.VISUALIZADOR));
        mvc.perform(cadastrar().with(sessao).with(csrf())).andExpect(status().is3xxRedirection());
        verify(usuarios).criarUsuario("nova.conta", "senha-teste", Role.VISUALIZADOR);
        mvc.perform(cadastrar().param("role", "ADMIN").with(sessao).with(csrf())).andExpect(status().is3xxRedirection());
        verify(usuarios).criarUsuario("nova.conta", "senha-teste", Role.ADMIN);
    }

    @Test
    void cadastroRejeitaPerfilInvalidoEPreservaCsrf() throws Exception {
        var sessao = user("gestor").roles("ADMIN");
        mvc.perform(cadastrar().param("role", "OUTRO").with(sessao).with(csrf())).andExpect(status().isBadRequest());
        mvc.perform(cadastrar().with(sessao)).andExpect(status().isForbidden());
        mvc.perform(multipart(FOLHA).file("arquivo", new byte[]{1}).with(sessao)).andExpect(status().isForbidden());
        mvc.perform(multipart(CONCILIAR).file("folha", new byte[]{1}).file("porFora", new byte[]{2}).with(sessao))
                .andExpect(status().isForbidden());
        verifyNoInteractions(usuarios, importacao, conciliacao);
    }
}
