package com.insights.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InicializadorUsuarioTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final UsuarioService service = mock(UsuarioService.class);
    private final InicializadorUsuario inicializador = new InicializadorUsuario();

    @Test
    void criaPrimeiroUsuarioSomenteQuandoBancoEstaVazio() throws Exception {
        when(repository.count()).thenReturn(0L, 1L);
        var runner = inicializador.criarPrimeiroUsuario(repository, service, "admin", "senha123");
        runner.run(null);
        runner.run(null);
        verify(service, times(1)).criarUsuario("admin", "senha123");
    }

    @Test
    void credenciaisAusentesNaoCriamUsuarioEParcialFalha() throws Exception {
        when(repository.count()).thenReturn(0L);
        inicializador.criarPrimeiroUsuario(repository, service, "", "").run(null);
        assertThrows(IllegalStateException.class,
                () -> inicializador.criarPrimeiroUsuario(repository, service, "admin", "").run(null));
        verifyNoInteractions(service);
    }
}
