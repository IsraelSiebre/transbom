package com.insights.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InicializadorUsuario {

    private static final Logger log = LoggerFactory.getLogger(InicializadorUsuario.class);

    @Bean
    ApplicationRunner criarPrimeiroUsuario(
            UsuarioRepository repository,
            UsuarioService service,
            @Value("${app.bootstrap.username:}") String username,
            @Value("${app.bootstrap.password:}") String senha) {
        return args -> {
            if (repository.count() != 0) {
                return;
            }
            if (username.isBlank() && senha.isBlank()) {
                log.warn("Nenhum usuário cadastrado. Defina APP_BOOTSTRAP_USERNAME e APP_BOOTSTRAP_PASSWORD para criar o primeiro usuário.");
                return;
            }
            if (username.isBlank() || senha.isBlank()) {
                throw new IllegalStateException("Defina APP_BOOTSTRAP_USERNAME e APP_BOOTSTRAP_PASSWORD juntos.");
            }
            service.criarUsuario(username, senha);
            log.info("Primeiro usuário criado. Remova as variáveis de bootstrap após a inicialização.");
        };
    }
}
