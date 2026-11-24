package com.insights.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InicializadorUsuarioTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final UsuarioService service = new UsuarioService(repository, encoder);

    @Test
    void bancoVazioRecebeAdministradorInicial() {
        new InicializadorUsuario(repository, service, " Inicial ", "teste-inicial").run(null);
        var cadastro = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(cadastro.capture());
        Usuario administrador = cadastro.getValue();
        assertThat(administrador.getRole()).isEqualTo(Role.ADMIN);
        assertThat(administrador.getUsername()).isEqualTo("inicial");
        assertThat(encoder.matches("teste-inicial", administrador.getSenhaHash())).isTrue();
    }

    @Test
    void promoveContaLegadaIdentificadaSemRegravarSenha() {
        String hash = encoder.encode("teste-atual");
        Usuario existente = new Usuario("inicial", hash);
        when(repository.count()).thenReturn(2L);
        when(repository.findByUsername("inicial")).thenReturn(Optional.of(existente));
        new InicializadorUsuario(repository, service, " Inicial ", "teste-atual").run(null);
        assertThat(existente.getRole()).isEqualTo(Role.ADMIN);
        assertThat(existente.getSenhaHash()).isEqualTo(hash);
        verify(repository, never()).save(any());
    }

    @ParameterizedTest @ValueSource(strings = {"", "teste-incorreta"})
    void credenciaisDesatualizadasNaoConcedemPermissoes(String senha) {
        Usuario existente = new Usuario("inicial", encoder.encode("teste-atual"));
        when(repository.count()).thenReturn(2L);
        when(repository.findByUsername("inicial")).thenReturn(Optional.of(existente));
        new InicializadorUsuario(repository, service, "inicial", senha).run(null);
        assertThat(existente.getRole()).isEqualTo(Role.VISUALIZADOR);
        verify(repository, never()).save(any());
    }

    @Test
    void preservaAtribuicoesQuandoJaExisteAdministrador() {
        when(repository.existsByRole(Role.ADMIN)).thenReturn(true);
        new InicializadorUsuario(repository, service, "outra-conta", "teste-atual").run(null);
        verify(repository, never()).findByUsername(any());
        verify(repository, never()).save(any());
    }

    @ParameterizedTest @EnumSource(Role.class)
    void loginCarregaOPerfilPersistido(Role role) {
        Usuario conta = new Usuario("conta", "hash-para-leitura", role);
        when(repository.findByUsername("conta")).thenReturn(Optional.of(conta));
        assertThat(service.loadUserByUsername("Conta").getAuthorities())
                .extracting("authority").containsExactly("ROLE_" + role.name());
    }
}
