package com.insights.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, UsuarioService.class})
class AlterarSenhaTest {
    private static final String PAGINA = "/minha-conta/senha";
    private static final String ATUAL = "anterior-teste";
    private static final String NOVA = "nova-senha-teste";

    @Autowired MockMvc mvc;
    @Autowired PasswordEncoder encoder;
    @MockitoBean UsuarioRepository repository;
    private Usuario ana;

    @BeforeEach
    void prepararConta() {
        ana = new Usuario("ana", encoder.encode(ATUAL));
        when(repository.findByUsername("ana")).thenReturn(Optional.of(ana));
        when(repository.findLockedByUsername("ana")).thenReturn(Optional.of(ana));
    }

    private MockHttpServletRequestBuilder enviar(String atual, String nova, String confirmacao) {
        return post(PAGINA).param("senhaAtual", atual).param("novaSenha", nova).param("confirmacaoSenha", confirmacao);
    }

    @Test
    void exigeLoginECsrf() throws Exception {
        String hashAnterior = ana.getSenhaHash();
        mvc.perform(get(PAGINA)).andExpect(header().string("Location", endsWith("/login")));
        mvc.perform(enviar(ATUAL, NOVA, NOVA).with(csrf()))
                .andExpect(header().string("Location", endsWith("/login")));
        mvc.perform(enviar(ATUAL, NOVA, NOVA).with(user("ana"))).andExpect(status().isForbidden());
        assertThat(ana.getSenhaHash()).isEqualTo(hashAnterior);
    }

    @Test
    void alteraSomenteAContaDaSessaoEAceitaSenhaNoLimiteEmBytes() throws Exception {
        Usuario bruno = new Usuario("bruno", encoder.encode(ATUAL));
        when(repository.findLockedByUsername("bruno")).thenReturn(Optional.of(bruno));
        String hashBruno = bruno.getSenhaHash();
        String senhaLimite = "á".repeat(36);

        mvc.perform(get(PAGINA).with(user("ana")))
                .andExpect(status().isOk()).andExpect(model().attribute("usuarioAtual", "ana"))
                .andExpect(content().string(containsString("autocomplete=\"current-password\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
        mvc.perform(enviar(ATUAL, senhaLimite, senhaLimite).param("username", "bruno")
                        .with(user("ana")).with(csrf()))
                .andExpect(redirectedUrl(PAGINA)).andExpect(flash().attributeExists("sucesso"));
        assertThat(bruno.getSenhaHash()).isEqualTo(hashBruno);
        assertThat(encoder.matches(senhaLimite, ana.getSenhaHash())).isTrue();
        mvc.perform(formLogin().user("ana").password(ATUAL)).andExpect(unauthenticated());
        mvc.perform(formLogin().user("ana").password(senhaLimite)).andExpect(authenticated().withUsername("ana"));
    }

    static Stream<Arguments> entradasInvalidas() {
        String longa = "á".repeat(37);
        return Stream.of(
                Arguments.of("outra-senha", NOVA, NOVA, "A senha atual está incorreta."),
                Arguments.of(longa, NOVA, NOVA, "A senha atual está incorreta."),
                Arguments.of(ATUAL, NOVA, "confirmacao-errada", "A confirmação deve ser igual à nova senha."),
                Arguments.of(ATUAL, ATUAL, ATUAL, "Escolha uma senha diferente da atual."),
                Arguments.of(ATUAL, "curta", "curta", "A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes."),
                Arguments.of(ATUAL, longa, longa, "A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes."));
    }

    @ParameterizedTest(name = "validação de credenciais {index}")
    @MethodSource("entradasInvalidas")
    void recusaAlteracaoEDevolveCamposVazios(String atual, String nova, String confirmacao, String erro) throws Exception {
        String hashAnterior = ana.getSenhaHash();
        String html = mvc.perform(enviar(atual, nova, confirmacao).with(user("ana")).with(csrf()))
                .andExpect(status().isOk()).andExpect(view().name("seguranca/alterar-senha"))
                .andExpect(model().attribute("erro", erro)).andReturn().getResponse().getContentAsString();
        assertThat(ana.getSenhaHash()).isEqualTo(hashAnterior);
        assertThat(html).doesNotContain("value=\"" + atual + "\"", "value=\"" + nova + "\"", "value=\"" + confirmacao + "\"");
    }
}
