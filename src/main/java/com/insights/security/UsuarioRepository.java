package com.insights.security;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuario> findLockedByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByRole(Role role);

    @Modifying
    @Query(value = "lock table usuarios in share row exclusive mode", nativeQuery = true)
    void bloquearParaInicializacao();
}
