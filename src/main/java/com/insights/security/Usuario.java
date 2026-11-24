package com.insights.security;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "role", nullable = false, length = 32)
    @ColumnDefault("'VISUALIZADOR'")
    @Enumerated(EnumType.STRING)
    private Role role = Role.VISUALIZADOR;

    protected Usuario() {
    }

    public Usuario(String username, String senhaHash) {
        this(username, senhaHash, Role.VISUALIZADOR);
    }

    public Usuario(String username, String senhaHash, Role role) {
        this.username = username;
        this.senhaHash = senhaHash;
        this.role = Objects.requireNonNull(role);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Role getRole() {
        return role;
    }

    void tornarAdministrador() {
        this.role = Role.ADMIN;
    }

    void alterarSenhaHash(String novoHash) {
        this.senhaHash = novoHash;
    }
}
