package com.insights.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class UsuarioService implements UserDetailsService {
    private static final int MAX_BYTES_SENHA = 72;
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criarUsuario(String username, String senha) {
        return criarUsuario(username, senha, Role.VISUALIZADOR);
    }

    @Transactional
    public Usuario criarUsuario(String username, String senha, Role role) {
        String nome = normalizarUsername(username);
        if (!nome.matches("[a-z0-9._-]{3,50}")) {
            throw new IllegalArgumentException("O usuário deve ter de 3 a 50 letras, números ou os caracteres . _ -.");
        }
        validarSenha(senha);
        if (repository.existsByUsername(nome)) {
            throw new IllegalArgumentException("Este usuário já está cadastrado.");
        }
        return repository.save(new Usuario(nome, passwordEncoder.encode(senha), role));
    }

    @Transactional
    public void alterarSenha(String username, String senhaAtual, String novaSenha) {
        validarSenha(novaSenha);
        Usuario usuario = repository.findLockedByUsername(normalizarUsername(username))
                .orElseThrow(() -> new IllegalArgumentException("A conta desta sessão não está mais disponível."));
        if (!senhaConfere(usuario, senhaAtual)) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
        if (novaSenha.equals(senhaAtual)) {
            throw new IllegalArgumentException("Escolha uma senha diferente da atual.");
        }
        usuario.alterarSenhaHash(passwordEncoder.encode(novaSenha));
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 8 || senha.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES_SENHA) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes.");
        }
    }

    boolean senhaConfere(Usuario usuario, String senha) {
        return senha != null && senha.getBytes(StandardCharsets.UTF_8).length <= MAX_BYTES_SENHA
                && passwordEncoder.matches(senha, usuario.getSenhaHash());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByUsername(normalizarUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        return User.withUsername(usuario.getUsername())
                .password(usuario.getSenhaHash())
                .roles(usuario.getRole().name())
                .build();
    }

    static String normalizarUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
