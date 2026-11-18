package com.insights.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UsuarioService service = new UsuarioService(repository, encoder);

    @Test
    void normalizaNomeEArmazenaSenhaComHash() {
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Usuario usuario = service.criarUsuario("  Fulano  ", "senha123");
        assertEquals("fulano", usuario.getUsername());
        assertNotEquals("senha123", usuario.getSenhaHash());
        assertTrue(encoder.matches("senha123", usuario.getSenhaHash()));
    }

    @Test
    void rejeitaSenhaCurtaOuUsuarioDuplicado() {
        assertThrows(IllegalArgumentException.class, () -> service.criarUsuario("fulano", "curta"));
        when(repository.existsByUsername("fulano")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.criarUsuario("Fulano", "senha123"));
        verify(repository, never()).save(any());
    }
}
