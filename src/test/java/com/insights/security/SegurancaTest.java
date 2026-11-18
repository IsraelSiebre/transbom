package com.insights.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class SegurancaTest {
    @Autowired MockMvc mvc;
    @Autowired PasswordEncoder encoder;
    @MockitoBean UsuarioService usuarioService;

    @Test
    void paginasProtegidasEcriacaoExigemLogin() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk())
                .andExpect(view().name("seguranca/login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")));
        mvc.perform(get("/mao-de-obra/upload")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mvc.perform(get("/usuarios/novo")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mvc.perform(post("/usuarios/novo").with(csrf())
                        .param("username", "novo").param("senha", "senha123")
                        .param("confirmacaoSenha", "senha123"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        verify(usuarioService, never()).criarUsuario(anyString(), anyString());
    }

    @Test
    void loginVerificaSenha() throws Exception {
        mvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuário ou senha inválidos.")));
        mvc.perform(get("/login").param("logout", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Você saiu da aplicação.")));
        when(usuarioService.loadUserByUsername("teste")).thenReturn(User.withUsername("teste")
                .password(encoder.encode("senha123")).roles("USER").build());
        mvc.perform(post("/login").with(csrf()).param("username", "teste").param("password", "errada"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login?error"));
        mvc.perform(post("/login").with(csrf()).param("username", "teste").param("password", "senha123"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/mao-de-obra"));
    }

    @Test
    void somenteCriacaoAutenticadaComCsrfESenhasCoincidentes() throws Exception {
        mvc.perform(post("/usuarios/novo").with(user("teste"))
                        .param("username", "novo").param("senha", "senha123")
                        .param("confirmacaoSenha", "senha123"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/usuarios/novo").with(user("teste")).with(csrf())
                        .param("username", "novo").param("senha", "senha123")
                        .param("confirmacaoSenha", "diferente"))
                .andExpect(status().isOk()).andExpect(model().attributeExists("erro"));
        verify(usuarioService, never()).criarUsuario(anyString(), anyString());
        mvc.perform(post("/usuarios/novo").with(user("teste")).with(csrf())
                        .param("username", "novo").param("senha", "senha123")
                        .param("confirmacaoSenha", "senha123"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/usuarios/novo?sucesso"));
        verify(usuarioService).criarUsuario("novo", "senha123");
    }
}
