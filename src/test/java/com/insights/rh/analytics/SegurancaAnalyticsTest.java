package com.insights.rh.analytics;

import com.insights.security.Role;
import com.insights.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AnalyticsController.class, DetalheFolhaController.class})
@Import(SecurityConfig.class)
class SegurancaAnalyticsTest {
    @Autowired MockMvc mvc;
    @MockitoBean MaoDeObraAnalyticsService analytics;
    @MockitoBean AnalisesPessoas pessoas;
    @MockitoBean ConsultaFolha consulta;
    @MockitoBean DetalheFolhaService detalhe;
    @MockitoBean ExportacaoFolhaService exportacao;

    @Test void carregaRecursosDoLoginAntesDeAutenticar() throws Exception {
        mvc.perform(get("/global/logo_transbom.png")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
        for (String arquivo : List.of("comet-base.css", "acesso.css", "vendor/roboto.woff2", "vendor/roboto-mono.woff2")) {
            mvc.perform(get("/rh/" + arquivo)).andExpect(status().isOk());
        }
        for (String pagina : List.of("/usuarios/novo", "/mao-de-obra/folha", "/mao-de-obra/upload",
                "/mao-de-obra/funcionarios/sem-centro-custo", "/mao-de-obra/guia")) {
            mvc.perform(get(pagina)).andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", endsWith("/login")));
        }
    }

    @Test void exigeLoginEmTodasAsConsultasEExportacoes() throws Exception {
        for (String recurso : List.of("filtros","resumo","detalhes","perfil/1",
                "faixas-salariais/0","rubricas/catalogo","exportar?tipo=funcionarios&formato=xlsx")) {
            mvc.perform(get("/mao-de-obra/api/"+recurso))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location",endsWith("/login")));
        }
    }

    @ParameterizedTest @EnumSource(Role.class)
    void ambosOsPerfisConsultamEExportam(Role role) throws Exception {
        for (String recurso : List.of("filtros", "resumo", "detalhes", "perfil/1", "faixas-salariais/0",
                "rubricas/catalogo", "exportar?tipo=funcionarios&formato=xlsx")) {
            mvc.perform(get("/mao-de-obra/api/" + recurso).with(user("conta").roles(role.name())))
                    .andExpect(status().isOk());
        }
    }
}
