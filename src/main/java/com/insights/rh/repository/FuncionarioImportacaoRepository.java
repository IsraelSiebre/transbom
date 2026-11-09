package com.insights.rh.repository;

import com.insights.rh.importacao.model.FuncionarioImportacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FuncionarioImportacaoRepository extends JpaRepository<FuncionarioImportacao, Long> {

    List<FuncionarioImportacao> findAllByCompetencia(LocalDate competencia);

}
