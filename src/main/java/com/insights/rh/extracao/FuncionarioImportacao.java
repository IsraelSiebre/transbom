package com.insights.rh.extracao;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class FuncionarioImportacao {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "participacao_lucros")
    private BigDecimal participacaoLucros;

    @Column(name = "estouro_mes")
    private BigDecimal estouroMes;

    @Column(name = "periculosidade_acidente_trabalho")
    private BigDecimal periculosidadeAcidenteTrabalho;

    @Column(name = "dias_afastados_acidente_trabalho")
    private Integer diasAfastadosAcidenteTrabalho;

    @Column(name = "plano_odontologico")
    private BigDecimal planoOdontologico;

    @Column(name = "desconto_farmacia")
    private BigDecimal descontoFarmacia;

    @Column(name = "convenio_saude_coparticipacao")
    private BigDecimal convenioSaudeCoparticipacao;

    @Column(name = "convenio_saude_dependente")
    private BigDecimal convenioSaudeDependente;

    @Column(name = "convenio_saude_titular")
    private BigDecimal convenioSaudeTitular;

    @Column(name = "desconto_dias_afastados_acidente_trabalho")
    private BigDecimal descontoDiasAfastadosAcidenteTrabalho;

    @Column(name = "base_irrf")
    private BigDecimal baseIrrf;

    @Column(name = "base_fgts")
    private BigDecimal baseFgts;

    @Column(name = "fgts_recolher")
    private BigDecimal fgtsRecolher;

    @Column(name = "seguro_acidente_trabalho")
    private BigDecimal seguroAcidenteTrabalho;

    @Column(name = "valor_outras_entidades")
    private BigDecimal valorOutrasEntidades;

    @Column(name = "total_vencimentos")
    private BigDecimal totalVencimentos;

    @Column(name = "total_descontos")
    private BigDecimal totalDescontos;

    @Column(name = "data_importacao")
    LocalDateTime dataImportacao = LocalDateTime.now();

}
