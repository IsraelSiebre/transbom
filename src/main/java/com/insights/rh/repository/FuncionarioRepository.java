package com.insights.rh.repository;

import com.insights.rh.importacao.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    Optional<Funcionario> findByCpf(String cpf);

    @Query("SELECT f FROM Funcionario f WHERE f.centroDeCusto IS NULL OR TRIM(f.centroDeCusto) = ''")
    List<Funcionario> findByCentroDeCustoEmptyOrNull();

    Optional<Funcionario> findById(Long funcionarioId);
}
