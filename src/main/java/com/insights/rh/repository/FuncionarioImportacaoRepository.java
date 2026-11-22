package com.insights.rh.repository;

import com.insights.rh.importacao.model.FuncionarioImportacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioImportacaoRepository extends JpaRepository<FuncionarioImportacao, Long> {

    List<FuncionarioImportacao> findAllByCompetencia(LocalDate competencia);

    Optional<FuncionarioImportacao> findByNomeAndCompetencia(String nome, LocalDate competencia);


}
