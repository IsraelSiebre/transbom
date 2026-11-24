package com.insights.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InicializadorUsuario implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializadorUsuario.class);

    private final UsuarioRepository repository;
    private final UsuarioService service;
    private final String username;
    private final String senha;

    public InicializadorUsuario(UsuarioRepository repository, UsuarioService service,
                                @Value("${app.bootstrap.username:}") String username,
                                @Value("${app.bootstrap.password:}") String senha) {
        this.repository = repository;
        this.service = service;
        this.username = UsuarioService.normalizarUsername(username);
        this.senha = senha;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Serializa a escolha do administrador quando duas instâncias iniciam juntas.
        repository.bloquearParaInicializacao();
        if (repository.existsByRole(Role.ADMIN)) {
            return;
        }
        if (repository.count() == 0) {
            if (username.isBlank() && senha.isBlank()) {
                log.warn("Defina APP_BOOTSTRAP_USERNAME e APP_BOOTSTRAP_PASSWORD para criar o primeiro administrador.");
                return;
            }
            if (username.isBlank() || senha.isBlank()) {
                throw new IllegalStateException("Defina APP_BOOTSTRAP_USERNAME e APP_BOOTSTRAP_PASSWORD juntos.");
            }
            service.criarUsuario(username, senha, Role.ADMIN);
            log.info("Administrador inicial criado.");
            return;
        }
        var conta = repository.findByUsername(username);
        if (conta.isPresent() && service.senhaConfere(conta.get(), senha)) {
            conta.get().tornarAdministrador();
            log.info("Perfil administrativo atribuído à conta de bootstrap existente.");
        } else {
            log.warn("Banco sem administrador. Configure APP_BOOTSTRAP_USERNAME e APP_BOOTSTRAP_PASSWORD com as credenciais atuais da conta escolhida e reinicie a aplicação.");
        }
    }
}
