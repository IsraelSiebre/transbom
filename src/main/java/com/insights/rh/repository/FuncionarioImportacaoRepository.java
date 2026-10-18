package com.insights.rh.repository;

import com.insights.rh.extracao.FuncionarioImportacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioImportacaoRepository extends JpaRepository<FuncionarioImportacao, Long> {
}
