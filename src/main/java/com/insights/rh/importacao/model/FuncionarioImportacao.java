package com.insights.rh.importacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Rubricas importadas da folha de pagamento.
 *
 * Os códigos numéricos correspondem à tabela de origem fornecida; rubricas
 * sem código numérico são identificadas pelo próprio nome textual.
 *
 * ATENÇÃO: nomes de colunas alterados exigem migração de banco (Flyway).
 */
@Getter
@Setter
@Entity
@Table(name = "folha_pagamento")
@NoArgsConstructor
public class FuncionarioImportacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // REMUNERAÇÃO, ADICIONAIS E PROVISÕES

    /** 8781 - SALARIO. */
    @Column(name = "salario_8781")
    private BigDecimal salario;

    /** 9380 - PRO LABORE. */
    @Column(name = "pro_labore_9380")
    private BigDecimal proLabore;

    /** 981 - ADIANTAMENTO DE SALARIO. */
    @Column(name = "adiantamento_salario_981")
    private BigDecimal adiantamentoSalario;

    /** 995 - SALARIO FAMILIA. */
    @Column(name = "salario_familia_995")
    private BigDecimal salarioFamilia;

    /** 285 - HORAS EXTRAS 50% (BANCO DE HORAS). */
    @Column(name = "horas_extras_banco_50_285")
    private BigDecimal bancoHoras50;

    /** Código textual: LICENCA REMUNERADA. */
    @Column(name = "licenca_remunerada")
    private BigDecimal licencaRemunerada;

    /** 873 - PLR. */
    @Column(name = "plr_873")
    private BigDecimal participacaoLucros;

    /** 150 - HORAS EXTRAS 50%. */
    @Column(name = "horas_extras_50_150")
    private BigDecimal horasExtras50;

    /** 264 - HORA EXTRA NOTURNA 100%. */
    @Column(name = "horas_extras_noturnas_100_264")
    private BigDecimal horasExtrasNoturnas100;

    /** 200 - HORAS EXTRAS 100%. */
    @Column(name = "horas_extras_100_200")
    private BigDecimal horasExtras100;

    /** 257 - HORA EXTRA NOTURNA 50%. */
    @Column(name = "horas_extras_noturnas_50_257")
    private BigDecimal horasExtrasNoturnas50;

    /** 226 - ADICIONAL JORNADA NOTURNO 20%. */
    @Column(name = "adicional_jornada_noturna_20_226")
    private BigDecimal adicionalNoturnoHoraReduzida;

    /** 214 - ADICIONAL NOTURNO 20%. */
    @Column(name = "adicional_noturno_20_214")
    private BigDecimal adicionalNoturnoValor;

    /** 255 - DIARIAS DE VIAGENS. */
    @Column(name = "diarias_viagens_255")
    private BigDecimal diariasViagens;

    /** 256 - PERNOITE. */
    @Column(name = "pernoite_256")
    private BigDecimal pernoite;

    /** 16 - INSALUBRIDADE SALARIO. */
    @Column(name = "insalubridade_salario_16")
    private BigDecimal insalubridade;

    /** 3 - D.S.R. GERAL. */
    @Column(name = "dsr_geral_3")
    private BigDecimal dsrGeral;

    /** 260 - ADICIONAL RODOTREN. */
    @Column(name = "adicional_rodotrem_260")
    private BigDecimal adicionalMotoristaRodotrem;

    /** Código textual: Comissões COM Provisão. */
    @Column(name = "comissoes_com_provisao")
    private BigDecimal comissoesComProvisao;

    /** Código textual: Comissões SEM Provisão. */
    @Column(name = "comissoes_sem_provisao")
    private BigDecimal comissoesSemProvisao;

    /** Código textual: CESTA BÁSICA. */
    @Column(name = "cesta_basica")
    private BigDecimal cestaBasica;

    /** Código textual: Provisão de Férias. */
    @Column(name = "provisao_ferias")
    private BigDecimal provisaoFerias;

    /** 805 - DIF. MEDIAS FERIAS. */
    @Column(name = "diferenca_medias_ferias_805")
    private BigDecimal diferencaMediasFerias;

    /** Código textual: Provisão de 13º. */
    @Column(name = "provisao_decimo_terceiro")
    private BigDecimal provisaoDecimoTerceiro;

    /** Código textual: Vale Transporte. */
    @Column(name = "vale_transporte")
    private BigDecimal valeTransporte;

    /** Código textual: Vale Refeição. */
    @Column(name = "vale_refeicao")
    private BigDecimal valeRefeicao;

    /** Código textual: Outros Encargos Sociais e Trab. */
    @Column(name = "outros_encargos_sociais_trabalhistas")
    private BigDecimal outrosEncargosSociaisTrabalhistas;

    /** 250 - REFLEXO EXTRAS DSR. */
    @Column(name = "reflexo_extras_dsr_250")
    private BigDecimal reflexoExtrasDsr;

    /** 854 - DSR VARIAVEL S/ ADICIONAL JORNADA NOTURNO 20%. */
    @Column(name = "dsr_variavel_jornada_noturna_20_854")
    private BigDecimal dsrVariavelAdicionalJornadaNoturna20;

    /** Código textual: D.S.R. S/ ADICIONAL NOTURNO 20%. */
    @Column(name = "dsr_adicional_noturno_20")
    private BigDecimal dsrAdicionalNoturno20;

    /** Código textual: D.S.R. S/ HORA EXTRA NOTURNA 50%. */
    @Column(name = "dsr_hora_extra_noturna_50")
    private BigDecimal dsrHoraExtraNoturna50;

    /** 262 - DSR S/ HORAS EXTRAS 100%. */
    @Column(name = "dsr_horas_extras_100_262")
    private BigDecimal dsrHorasExtras100;

    /** 225 - ADIANTAMENTO DE DIARIAS DE VIAGEM. */
    @Column(name = "adiantamento_diarias_viagem_225")
    private BigDecimal adiantamentoDiariasViagem;

    /** 149 - ADICIONAL PERICULOSIDADE. */
    @Column(name = "adicional_periculosidade_149")
    private BigDecimal periculosidade;

    /** 261 - HORAS NORMAIS SALARIO. */
    @Column(name = "horas_normais_salario_261")
    private BigDecimal horasNormais;

    // RESCISÃO, FÉRIAS E ABONOS

    /** 9591 - R-AVISO PREVIO INDENIZADO. */
    @Column(name = "aviso_previo_indenizado_rescisao_9591")
    private BigDecimal avisoPrevioIndenizadoRescisao;

    /** 808 - ABONO PECUNIARIO. */
    @Column(name = "abono_pecuniario_808")
    private BigDecimal abonoPecuniario;

    /** 932 - 1/3 S/ ABONO PECUNIARIO. */
    @Column(name = "um_terco_abono_pecuniario_932")
    private BigDecimal umTercoAbonoFerias;

    /** 8800 - DIAS ABONO PECUNIARIO (FERIAS). */
    @Column(name = "dias_abono_pecuniario_8800")
    private BigDecimal diasAbonoPecuniarioFerias;

    /** 9180 - R-SALDO DE SALARIO. */
    @Column(name = "saldo_salario_rescisao_9180")
    private BigDecimal saldoSalarioRescisao;

    /** 64 - R-ADICIONAL DE FERIAS. */
    @Column(name = "adicional_ferias_rescisao_64")
    private BigDecimal adicionalFeriasRescisao;

    /** Código textual: R-FERIAS S/ AVISO PREVIO. */
    @Column(name = "ferias_sobre_aviso_previo_rescisao")
    private BigDecimal feriasAvisoPrevioRescisao;

    /** 51 - VALOR LIQUIDO DA RESCISÃO. */
    @Column(name = "valor_liquido_rescisao_51")
    private BigDecimal liquidoRescisao;

    /** 29 - FERIAS PROPORCIONAIS. */
    @Column(name = "ferias_proporcionais_29")
    private BigDecimal feriasProporcionais;

    /** 8552 - R-13o. SALARIO NA RCT. */
    @Column(name = "decimo_terceiro_rescisao_rct_8552")
    private BigDecimal decimoTerceiroRescisaoRct;

    /** 8550 - 13 SALARIO INTEGRAL RESCISAO. */
    @Column(name = "decimo_terceiro_integral_rescisao_8550")
    private BigDecimal decimoTerceiroIntegralRescisao;

    /** 8783 - FERIAS NORMAIS MÊS. */
    @Column(name = "ferias_normais_mes_8783")
    private BigDecimal feriasNormaisMes8783;

    /** 931 - 1/3 ADICIONAL DE FERIAS. */
    @Column(name = "um_terco_adicional_ferias_931")
    private BigDecimal umTercoFerias;

    /** 28 - ADICIONAL DE FERIAS. */
    @Column(name = "adicional_ferias_28")
    private BigDecimal adicionalFerias;

    /** 9162 - FERIAS NORMAIS MÊS. */
    @Column(name = "ferias_normais_mes_9162")
    private BigDecimal feriasNormaisMes9162;

    /** 937 - DESC.ADTO.FERIAS MÊS. */
    @Column(name = "desconto_adiantamento_ferias_mes_937")
    private BigDecimal descontoAdiantamentoFeriasMes;

    /** 8169 - 1/3 ADICIONAL DE FERIAS. */
    @Column(name = "um_terco_adicional_ferias_8169")
    private BigDecimal umTercoAdicionalFerias8169;

    // IMPOSTOS, ENCARGOS E TOTALIZADORES

    /** Código textual: FGTS A RECOLHER. */
    @Column(name = "fgts_a_recolher")
    private BigDecimal fgtsRecolher;

    /** 998 - I.N.S.S.. */
    @Column(name = "inss_998")
    private BigDecimal inss;

    /** 812 - I.N.S.S. DE FERIAS. */
    @Column(name = "inss_ferias_812")
    private BigDecimal inssFerias;

    /** 989 - I.N.S.S. RESCISAO. */
    @Column(name = "inss_rescisao_989")
    private BigDecimal inssRescisao;

    /** 96 - I.R.R.F.. */
    @Column(name = "irrf_96")
    private BigDecimal irrf;

    /** 942 - I.R.R.F. DE FERIAS. */
    @Column(name = "irrf_ferias_942")
    private BigDecimal irrfFerias;

    /** Código textual: DESCONTOS TOTAIS. */
    @Column(name = "descontos_totais")
    private BigDecimal totalDescontos;

    /** Código textual: Custo M.O Total. */
    @Column(name = "custo_mao_obra_total")
    private BigDecimal custoMaoObraTotal;

    /** 9181 - I.N.S.S. DE FERIAS. */
    @Column(name = "inss_ferias_9181")
    private BigDecimal inssFerias9181;

    /** Código textual: SEG.ACIDENTE TRABALHO. */
    @Column(name = "seguro_acidente_trabalho")
    private BigDecimal seguroAcidenteTrabalho;

    /** Código textual: VALOR OUTRAS ENTIDADES. */
    @Column(name = "valor_outras_entidades")
    private BigDecimal valorOutrasEntidades;

    // BENEFÍCIOS E DESCONTOS

    /** 208 - SANTA CASA SAUDE. */
    @Column(name = "santa_casa_saude_208")
    private BigDecimal santaCasaSaude208;

    /** 170 - CONVENIO ODONTOLóGICO. */
    @Column(name = "convenio_odontologico_170")
    private BigDecimal convenioOdontologico170;

    /** 258 - SANTA CASA SAUDE. */
    @Column(name = "santa_casa_saude_258")
    private BigDecimal santaCasaSaude258;

    /** 259 - CONVENIO ODONTOLóGICO. */
    @Column(name = "convenio_odontologico_259")
    private BigDecimal planoOdontologico;

    /** 277 - CONVENIO - ANA COSTA SAÚDE. */
    @Column(name = "convenio_ana_costa_saude_277")
    private BigDecimal convenioAnaCosta;

    /** 282 - CONVENIO SAUDE. */
    @Column(name = "convenio_saude_282")
    private BigDecimal convenioSaudeTitular;

    /** 271 - VALE - DIVERSOS. */
    @Column(name = "vales_diversos_271")
    private BigDecimal valesDiversos;

    /** 275 - PENSAO ALIMENTICIA 25% SAL LÍQ. */
    @Column(name = "pensao_alimenticia_liquido_25_275")
    private BigDecimal pensaoAlimenticiaLiquido25;

    /** 205 - DESCONTO EMPRESTIMO. */
    @Column(name = "desconto_emprestimo_205")
    private BigDecimal emprestimo;

    /** 280 - CONVENIO SAUDE COOPART. */
    @Column(name = "convenio_saude_coparticipacao_280")
    private BigDecimal convenioSaudeCoparticipacao;

    /** 265 - CONVENIO DE FARMÁCIA. */
    @Column(name = "convenio_farmacia_265")
    private BigDecimal descontoFarmacia;

    /** 267 - CONVENIO SEGUROS. */
    @Column(name = "convenio_seguros_267")
    private BigDecimal convenioSeguros;

    /** 242 - MULTAS DE TRANSITO. */
    @Column(name = "multas_transito_242")
    private BigDecimal multaTransito;

    /** 900 - PENSAO ALIMENTICIA 25% SAL. */
    @Column(name = "pensao_alimenticia_salario_25_900")
    private BigDecimal pensaoAlimenticiaSalario25;

    /** 89 - PENHORA. */
    @Column(name = "penhora_89")
    private BigDecimal penhora;

    /** 146 - REEMBOLSO. */
    @Column(name = "reembolso_146")
    private BigDecimal reembolso;

    // AFASTAMENTOS E FALTAS

    /** 8069 - FALTAS INJUST. EM HORAS. */
    @Column(name = "faltas_injustificadas_horas_8069")
    private BigDecimal faltasInjustificadasHoras;

    /** 8785 - DIAS AFAST.INSS (P/DOENCA). */
    @Column(name = "dias_afastamento_inss_doenca_8785")
    private BigDecimal diasAfastamentoInssDoenca;

    /** 9988 - FALTAS INJUST. DSR EM HORAS. */
    @Column(name = "faltas_injustificadas_dsr_horas_9988")
    private BigDecimal faltasInjustificadasDsrHoras;

    /** 9522 - AFASTAMENTO DOENÇAS DIAS SEMPRE. */
    @Column(name = "afastamento_doenca_dias_sempre_9522")
    private BigDecimal afastamentoDoencaDiasSempre;

    // RUBRICAS LEGADAS NÃO PRESENTES NA NOVA TABELA

    /** Rubrica legada 9360. */
    @Column(name = "dsr_horista_variavel_9360")
    private BigDecimal dsrHoristaVariavel;

    /** Rubrica legada 278. */
    @Column(name = "convenio_notredame_278")
    private BigDecimal convenioNotredame;

    /** Rubrica legada 281. */
    @Column(name = "convenio_saude_dependente_281")
    private BigDecimal convenioSaudeDependente;

    /** Rubrica legada 272. */
    @Column(name = "pensao_alimenticia_salario_minimo_272")
    private BigDecimal pensaoAlimenticiaSalarioMinimo;

    /** Rubrica legada 283. */
    @Column(name = "pensao_mensal_liquido_283")
    private BigDecimal pensaoMensalLiquido;

    /** Rubrica legada 292. */
    @Column(name = "contribuicao_espontanea_292")
    private BigDecimal contribuicaoEspontanea;

    /** Rubrica legada 293. */
    @Column(name = "contribuicao_confederativa_293")
    private BigDecimal contribuicaoConfederativa;

    /** Rubrica legada 299. */
    @Column(name = "desconto_material_299")
    private BigDecimal descontoMaterial;

    /** Rubrica legada 990. */
    @Column(name = "estouro_mes_990")
    private BigDecimal estouroMes;

    /** Rubrica legada 991. */
    @Column(name = "estouro_mes_anterior_991")
    private BigDecimal estouroMesAnterior;

    /** Rubrica legada 9750. */
    @Column(name = "desconto_emprestimo_credito_trabalhador_9750")
    private BigDecimal descontoEmprestimoCreditoTrabalhador;

    /** Rubrica legada 806. */
    @Column(name = "media_horas_ferias_806")
    private BigDecimal mediaHorasFerias;

    /** Rubrica legada 807. */
    @Column(name = "vantagens_ferias_807")
    private BigDecimal vantagensFerias;

    /** Rubrica legada 809. */
    @Column(name = "media_horas_abono_809")
    private BigDecimal mediaHorasAbono;

    /** Rubrica legada 810. */
    @Column(name = "vantagens_abono_810")
    private BigDecimal vantagensAbono;

    /** Rubrica legada 815. */
    @Column(name = "media_horas_ferias_proporcionais_815")
    private BigDecimal mediaHorasFeriasProporcionais;

    /** Rubrica legada 816. */
    @Column(name = "media_valor_ferias_proporcionais_816")
    private BigDecimal mediaValorFeriasProporcionais;

    /** Rubrica legada 8112. */
    @Column(name = "diferenca_um_terco_ferias_8112")
    private BigDecimal diferencaUmTercoFerias;

    /** Rubrica legada 8190. */
    @Column(name = "diferenca_media_valor_ferias_8190")
    private BigDecimal diferencaMediaValorFerias;

    /** Rubrica legada 8198. */
    @Column(name = "diferenca_media_valor_abono_ferias_8198")
    private BigDecimal diferencaMediaValorAbonoFerias;

    /** Rubrica legada 8417. */
    @Column(name = "diferenca_um_terco_abono_ferias_8417")
    private BigDecimal diferencaUmTercoAbonoFerias;

    /** Rubrica legada 821. */
    @Column(name = "inss_diferenca_ferias_821")
    private BigDecimal inssDiferencaFerias;

    /** Rubrica legada 826. */
    @Column(name = "inss_rescisao_historico826_826")
    private BigDecimal inssRescisaoHistorico826;

    /** Rubrica legada 828. */
    @Column(name = "irrf_rescisao_828")
    private BigDecimal irrfRescisao;

    /** Rubrica legada 836. */
    @Column(name = "inss_diferenca_ferias_desconto_maior_836")
    private BigDecimal inssDiferencaFeriasDescontoMaior;

    /** Rubrica legada 843. */
    @Column(name = "inss_empregador_843")
    private BigDecimal inssEmpregador;

    /** Rubrica legada 999. */
    @Column(name = "imposto_renda_999")
    private BigDecimal impostoRenda;

    /** Rubrica legada 896. */
    @Column(name = "media_afastamento_doenca_896")
    private BigDecimal mediaAfastamentoDoenca;

    /** Rubrica legada 8054. */
    @Column(name = "periculosidade_acidente_trabalho_8054")
    private BigDecimal periculosidadeAcidenteTrabalho;

    /** Rubrica legada 8058. */
    @Column(name = "periculosidade_doenca_8058")
    private BigDecimal periculosidadeDoenca;

    /** Rubrica legada 8786. */
    @Column(name = "dias_afastamento_acidente_trabalho_8786")
    private BigDecimal diasAfastamentoAcidenteTrabalho;

    /** Rubrica legada 8792. */
    @Column(name = "dias_faltas_8792")
    private BigDecimal diasFaltas;

    /** Rubrica legada 8801. */
    @Column(name = "desconto_dias_afastados_8801")
    private BigDecimal descontoDiasAfastados;

    /** Rubrica legada 8808. */
    @Column(name = "desconto_dias_afastados_acidente_trabalho_8808")
    private BigDecimal descontoDiasAfastadosAcidenteTrabalho;

    /** Rubrica legada 8923. */
    @Column(name = "dias_suspensos_8923")
    private BigDecimal diasSuspensos;

    /** Rubrica legada 9532. */
    @Column(name = "periculosidade_afastamento_ate15_dias_9532")
    private BigDecimal periculosidadeAfastamentoAte15Dias;

    /** Rubrica legada 8551. */
    @Column(name = "media_horas_decimo_terceiro_rescisao_8551")
    private BigDecimal mediaHorasDecimoTerceiroRescisao;

    // BASES E TOTALIZADORES LEGADOS

    /** Código textual: BASE FGTS. */
    @Column(name = "base_fgts")
    private BigDecimal baseFgts;

    /** Código textual: BASE INSS. */
    @Column(name = "base_inss")
    private BigDecimal baseInss;

    /** Código textual: BASE IRRF. */
    @Column(name = "base_irrf")
    private BigDecimal baseIrrf;

    /** Código textual: TOTAL DE VENCIMENTOS. */
    @Column(name = "total_vencimentos")
    private BigDecimal totalVencimentos;

    /** Código textual: TOTAL LIQUIDO. */
    @Column(name = "total_liquido")
    private BigDecimal totalLiquido;

    // Informações internas e pagamentos complementares (não são rubricas da tabela)
    @Column(name = "vale_transporte_por_fora")
    private BigDecimal valeTransportePorFora;

    @Column(name = "vale_refeicao_por_fora")
    private BigDecimal valeRefeicaoPorFora;

    @Column(name = "gratificacao_por_fora")
    private BigDecimal gratificacaoPorFora;

    @Column(name = "comissoes_por_fora")
    private BigDecimal comissoesPorFora;

    @Column(name = "valor_best_pontos")
    private BigDecimal valorBestPontos;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @Column(name = "competencia")
    private LocalDate competencia;

    public void setCompetencia(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.competencia = LocalDate.parse(data, formatter);
    }

    public void setCompetencia(LocalDate data) {
        this.competencia = data;
    }

    /**
     * Normaliza códigos extraídos do Excel (ex.: 264.0) e nomes de rubricas
     * sem código numérico, com ou sem acentos/pontuação.
     */
    private static String normalizarCodigo(String codigo) {
        if (codigo == null) return null;
        String normalizado = codigo.trim().toUpperCase(Locale.ROOT)
                .replaceFirst("\\.0+$", "");
        normalizado = Normalizer.normalize(normalizado, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^A-Z0-9]+", " ")
                .trim()
                .replaceAll("\\bD S R\\b", "DSR");
        return normalizado;
    }

    /** Atribui valor a uma rubrica pelo código numérico ou textual. */
    public void setValorPorCodigo(String codigo, BigDecimal valor) {
        if (codigo == null || valor == null) return;

        switch (normalizarCodigo(codigo)) {

            case "8781" -> this.salario = valor;
            case "9380" -> this.proLabore = valor;
            case "981" -> this.adiantamentoSalario = valor;
            case "995" -> this.salarioFamilia = valor;
            case "285" -> this.bancoHoras50 = valor;
            case "LICENCA REMUNERADA" -> this.licencaRemunerada = valor;
            case "873" -> this.participacaoLucros = valor;
            case "150" -> this.horasExtras50 = valor;
            case "264" -> this.horasExtrasNoturnas100 = valor;
            case "200" -> this.horasExtras100 = valor;
            case "257" -> this.horasExtrasNoturnas50 = valor;
            case "226" -> this.adicionalNoturnoHoraReduzida = valor;
            case "214" -> this.adicionalNoturnoValor = valor;
            case "255" -> this.diariasViagens = valor;
            case "256" -> this.pernoite = valor;
            case "16" -> this.insalubridade = valor;
            case "3" -> this.dsrGeral = valor;
            case "260" -> this.adicionalMotoristaRodotrem = valor;
            case "COMISSOES COM PROVISAO" -> this.comissoesComProvisao = valor;
            case "COMISSOES SEM PROVISAO" -> this.comissoesSemProvisao = valor;
            case "CESTA BASICA" -> this.cestaBasica = valor;
            case "PROVISAO DE FERIAS" -> this.provisaoFerias = valor;
            case "805" -> this.diferencaMediasFerias = valor;
            case "PROVISAO DE 13" -> this.provisaoDecimoTerceiro = valor;
            case "VALE TRANSPORTE" -> this.valeTransporte = valor;
            case "VALE REFEICAO" -> this.valeRefeicao = valor;
            case "OUTROS ENCARGOS SOCIAIS E TRAB" -> this.outrosEncargosSociaisTrabalhistas = valor;
            case "9591" -> this.avisoPrevioIndenizadoRescisao = valor;
            case "250" -> this.reflexoExtrasDsr = valor;
            case "854" -> this.dsrVariavelAdicionalJornadaNoturna20 = valor;
            case "DSR S ADICIONAL NOTURNO 20" -> this.dsrAdicionalNoturno20 = valor;
            case "DSR S HORA EXTRA NOTURNA 50" -> this.dsrHoraExtraNoturna50 = valor;
            case "262" -> this.dsrHorasExtras100 = valor;
            case "808" -> this.abonoPecuniario = valor;
            case "932" -> this.umTercoAbonoFerias = valor;
            case "8800" -> this.diasAbonoPecuniarioFerias = valor;
            case "9180" -> this.saldoSalarioRescisao = valor;
            case "64" -> this.adicionalFeriasRescisao = valor;
            case "R FERIAS S AVISO PREVIO" -> this.feriasAvisoPrevioRescisao = valor;
            case "51" -> this.liquidoRescisao = valor;
            case "29" -> this.feriasProporcionais = valor;
            case "8552" -> this.decimoTerceiroRescisaoRct = valor;
            case "8550" -> this.decimoTerceiroIntegralRescisao = valor;
            case "FGTS A RECOLHER", "FGTS" -> this.fgtsRecolher = valor;
            case "8783" -> this.feriasNormaisMes8783 = valor;
            case "931" -> this.umTercoFerias = valor;
            case "28" -> this.adicionalFerias = valor;
            case "208" -> this.santaCasaSaude208 = valor;
            case "170" -> this.convenioOdontologico170 = valor;
            case "258" -> this.santaCasaSaude258 = valor;
            case "259" -> this.planoOdontologico = valor;
            case "277" -> this.convenioAnaCosta = valor;
            case "282" -> this.convenioSaudeTitular = valor;
            case "271" -> this.valesDiversos = valor;
            case "275" -> this.pensaoAlimenticiaLiquido25 = valor;
            case "205" -> this.emprestimo = valor;
            case "8069" -> this.faltasInjustificadasHoras = valor;
            case "280" -> this.convenioSaudeCoparticipacao = valor;
            case "265" -> this.descontoFarmacia = valor;
            case "267" -> this.convenioSeguros = valor;
            case "242" -> this.multaTransito = valor;
            case "998" -> this.inss = valor;
            case "812" -> this.inssFerias = valor;
            case "8785" -> this.diasAfastamentoInssDoenca = valor;
            case "989" -> this.inssRescisao = valor;
            case "96" -> this.irrf = valor;
            case "942" -> this.irrfFerias = valor;
            case "DESCONTOS TOTAIS", "TOTAL DE DESCONTOS", "TOTAL DESCONTOS" -> this.totalDescontos = valor;
            case "CUSTO M O TOTAL", "CUSTO MO TOTAL" -> this.custoMaoObraTotal = valor;
            case "900" -> this.pensaoAlimenticiaSalario25 = valor;
            case "9162" -> this.feriasNormaisMes9162 = valor;
            case "937" -> this.descontoAdiantamentoFeriasMes = valor;
            case "225" -> this.adiantamentoDiariasViagem = valor;
            case "9181" -> this.inssFerias9181 = valor;
            case "8169" -> this.umTercoAdicionalFerias8169 = valor;
            case "149" -> this.periculosidade = valor;
            case "261" -> this.horasNormais = valor;
            case "89" -> this.penhora = valor;
            case "9988" -> this.faltasInjustificadasDsrHoras = valor;
            case "9522" -> this.afastamentoDoencaDiasSempre = valor;
            case "146" -> this.reembolso = valor;
            case "SEG ACIDENTE TRABALHO" -> this.seguroAcidenteTrabalho = valor;
            case "VALOR OUTRAS ENTIDADES" -> this.valorOutrasEntidades = valor;
            case "9360" -> this.dsrHoristaVariavel = valor;
            case "278" -> this.convenioNotredame = valor;
            case "281" -> this.convenioSaudeDependente = valor;
            case "272" -> this.pensaoAlimenticiaSalarioMinimo = valor;
            case "283" -> this.pensaoMensalLiquido = valor;
            case "292" -> this.contribuicaoEspontanea = valor;
            case "293" -> this.contribuicaoConfederativa = valor;
            case "299" -> this.descontoMaterial = valor;
            case "990" -> this.estouroMes = valor;
            case "991" -> this.estouroMesAnterior = valor;
            case "9750" -> this.descontoEmprestimoCreditoTrabalhador = valor;
            case "806" -> this.mediaHorasFerias = valor;
            case "807" -> this.vantagensFerias = valor;
            case "809" -> this.mediaHorasAbono = valor;
            case "810" -> this.vantagensAbono = valor;
            case "815" -> this.mediaHorasFeriasProporcionais = valor;
            case "816" -> this.mediaValorFeriasProporcionais = valor;
            case "8112" -> this.diferencaUmTercoFerias = valor;
            case "8190" -> this.diferencaMediaValorFerias = valor;
            case "8198" -> this.diferencaMediaValorAbonoFerias = valor;
            case "8417" -> this.diferencaUmTercoAbonoFerias = valor;
            case "821" -> this.inssDiferencaFerias = valor;
            case "826" -> this.inssRescisaoHistorico826 = valor;
            case "828" -> this.irrfRescisao = valor;
            case "836" -> this.inssDiferencaFeriasDescontoMaior = valor;
            case "843" -> this.inssEmpregador = valor;
            case "999" -> this.impostoRenda = valor;
            case "896" -> this.mediaAfastamentoDoenca = valor;
            case "8054" -> this.periculosidadeAcidenteTrabalho = valor;
            case "8058" -> this.periculosidadeDoenca = valor;
            case "8786" -> this.diasAfastamentoAcidenteTrabalho = valor;
            case "8792" -> this.diasFaltas = valor;
            case "8801" -> this.descontoDiasAfastados = valor;
            case "8808" -> this.descontoDiasAfastadosAcidenteTrabalho = valor;
            case "8923" -> this.diasSuspensos = valor;
            case "9532" -> this.periculosidadeAfastamentoAte15Dias = valor;
            case "8551" -> this.mediaHorasDecimoTerceiroRescisao = valor;
            case "BASE FGTS" -> this.baseFgts = valor;
            case "BASE INSS" -> this.baseInss = valor;
            case "BASE IRRF" -> this.baseIrrf = valor;
            case "TOTAL DE VENCIMENTOS" -> this.totalVencimentos = valor;
            case "TOTAL LIQUIDO" -> this.totalLiquido = valor;
            default -> throw new IllegalArgumentException(
                    "Código de rubrica desconhecido: " + codigo);
        }
    }

    /**
     * Obtém o valor de uma rubrica; mantém a convenção anterior:
     * código desconhecido devolve ZERO, e campo conhecido não preenchido
     * devolve null.
     */
    public BigDecimal getValorPorCodigo(String codigo) {
        if (codigo == null) return null;

        return switch (normalizarCodigo(codigo)) {

            case "8781" -> this.salario;
            case "9380" -> this.proLabore;
            case "981" -> this.adiantamentoSalario;
            case "995" -> this.salarioFamilia;
            case "285" -> this.bancoHoras50;
            case "LICENCA REMUNERADA" -> this.licencaRemunerada;
            case "873" -> this.participacaoLucros;
            case "150" -> this.horasExtras50;
            case "264" -> this.horasExtrasNoturnas100;
            case "200" -> this.horasExtras100;
            case "257" -> this.horasExtrasNoturnas50;
            case "226" -> this.adicionalNoturnoHoraReduzida;
            case "214" -> this.adicionalNoturnoValor;
            case "255" -> this.diariasViagens;
            case "256" -> this.pernoite;
            case "16" -> this.insalubridade;
            case "3" -> this.dsrGeral;
            case "260" -> this.adicionalMotoristaRodotrem;
            case "COMISSOES COM PROVISAO" -> this.comissoesComProvisao;
            case "COMISSOES SEM PROVISAO" -> this.comissoesSemProvisao;
            case "CESTA BASICA" -> this.cestaBasica;
            case "PROVISAO DE FERIAS" -> this.provisaoFerias;
            case "805" -> this.diferencaMediasFerias;
            case "PROVISAO DE 13" -> this.provisaoDecimoTerceiro;
            case "VALE TRANSPORTE" -> this.valeTransporte;
            case "VALE REFEICAO" -> this.valeRefeicao;
            case "OUTROS ENCARGOS SOCIAIS E TRAB" -> this.outrosEncargosSociaisTrabalhistas;
            case "9591" -> this.avisoPrevioIndenizadoRescisao;
            case "250" -> this.reflexoExtrasDsr;
            case "854" -> this.dsrVariavelAdicionalJornadaNoturna20;
            case "DSR S ADICIONAL NOTURNO 20" -> this.dsrAdicionalNoturno20;
            case "DSR S HORA EXTRA NOTURNA 50" -> this.dsrHoraExtraNoturna50;
            case "262" -> this.dsrHorasExtras100;
            case "808" -> this.abonoPecuniario;
            case "932" -> this.umTercoAbonoFerias;
            case "8800" -> this.diasAbonoPecuniarioFerias;
            case "9180" -> this.saldoSalarioRescisao;
            case "64" -> this.adicionalFeriasRescisao;
            case "R FERIAS S AVISO PREVIO" -> this.feriasAvisoPrevioRescisao;
            case "51" -> this.liquidoRescisao;
            case "29" -> this.feriasProporcionais;
            case "8552" -> this.decimoTerceiroRescisaoRct;
            case "8550" -> this.decimoTerceiroIntegralRescisao;
            case "FGTS A RECOLHER", "FGTS" -> this.fgtsRecolher;
            case "8783" -> this.feriasNormaisMes8783;
            case "931" -> this.umTercoFerias;
            case "28" -> this.adicionalFerias;
            case "208" -> this.santaCasaSaude208;
            case "170" -> this.convenioOdontologico170;
            case "258" -> this.santaCasaSaude258;
            case "259" -> this.planoOdontologico;
            case "277" -> this.convenioAnaCosta;
            case "282" -> this.convenioSaudeTitular;
            case "271" -> this.valesDiversos;
            case "275" -> this.pensaoAlimenticiaLiquido25;
            case "205" -> this.emprestimo;
            case "8069" -> this.faltasInjustificadasHoras;
            case "280" -> this.convenioSaudeCoparticipacao;
            case "265" -> this.descontoFarmacia;
            case "267" -> this.convenioSeguros;
            case "242" -> this.multaTransito;
            case "998" -> this.inss;
            case "812" -> this.inssFerias;
            case "8785" -> this.diasAfastamentoInssDoenca;
            case "989" -> this.inssRescisao;
            case "96" -> this.irrf;
            case "942" -> this.irrfFerias;
            case "DESCONTOS TOTAIS", "TOTAL DE DESCONTOS", "TOTAL DESCONTOS" -> this.totalDescontos;
            case "CUSTO M O TOTAL", "CUSTO MO TOTAL" -> this.custoMaoObraTotal;
            case "900" -> this.pensaoAlimenticiaSalario25;
            case "9162" -> this.feriasNormaisMes9162;
            case "937" -> this.descontoAdiantamentoFeriasMes;
            case "225" -> this.adiantamentoDiariasViagem;
            case "9181" -> this.inssFerias9181;
            case "8169" -> this.umTercoAdicionalFerias8169;
            case "149" -> this.periculosidade;
            case "261" -> this.horasNormais;
            case "89" -> this.penhora;
            case "9988" -> this.faltasInjustificadasDsrHoras;
            case "9522" -> this.afastamentoDoencaDiasSempre;
            case "146" -> this.reembolso;
            case "SEG ACIDENTE TRABALHO" -> this.seguroAcidenteTrabalho;
            case "VALOR OUTRAS ENTIDADES" -> this.valorOutrasEntidades;
            case "9360" -> this.dsrHoristaVariavel;
            case "278" -> this.convenioNotredame;
            case "281" -> this.convenioSaudeDependente;
            case "272" -> this.pensaoAlimenticiaSalarioMinimo;
            case "283" -> this.pensaoMensalLiquido;
            case "292" -> this.contribuicaoEspontanea;
            case "293" -> this.contribuicaoConfederativa;
            case "299" -> this.descontoMaterial;
            case "990" -> this.estouroMes;
            case "991" -> this.estouroMesAnterior;
            case "9750" -> this.descontoEmprestimoCreditoTrabalhador;
            case "806" -> this.mediaHorasFerias;
            case "807" -> this.vantagensFerias;
            case "809" -> this.mediaHorasAbono;
            case "810" -> this.vantagensAbono;
            case "815" -> this.mediaHorasFeriasProporcionais;
            case "816" -> this.mediaValorFeriasProporcionais;
            case "8112" -> this.diferencaUmTercoFerias;
            case "8190" -> this.diferencaMediaValorFerias;
            case "8198" -> this.diferencaMediaValorAbonoFerias;
            case "8417" -> this.diferencaUmTercoAbonoFerias;
            case "821" -> this.inssDiferencaFerias;
            case "826" -> this.inssRescisaoHistorico826;
            case "828" -> this.irrfRescisao;
            case "836" -> this.inssDiferencaFeriasDescontoMaior;
            case "843" -> this.inssEmpregador;
            case "999" -> this.impostoRenda;
            case "896" -> this.mediaAfastamentoDoenca;
            case "8054" -> this.periculosidadeAcidenteTrabalho;
            case "8058" -> this.periculosidadeDoenca;
            case "8786" -> this.diasAfastamentoAcidenteTrabalho;
            case "8792" -> this.diasFaltas;
            case "8801" -> this.descontoDiasAfastados;
            case "8808" -> this.descontoDiasAfastadosAcidenteTrabalho;
            case "8923" -> this.diasSuspensos;
            case "9532" -> this.periculosidadeAfastamentoAte15Dias;
            case "8551" -> this.mediaHorasDecimoTerceiroRescisao;
            case "BASE FGTS" -> this.baseFgts;
            case "BASE INSS" -> this.baseInss;
            case "BASE IRRF" -> this.baseIrrf;
            case "TOTAL DE VENCIMENTOS" -> this.totalVencimentos;
            case "TOTAL LIQUIDO" -> this.totalLiquido;
            default -> BigDecimal.ZERO;
        };
    }
}
