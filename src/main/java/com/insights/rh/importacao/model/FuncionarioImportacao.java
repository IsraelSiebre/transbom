package com.insights.rh.importacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Entity
@Table(name = "funcionario_calculo", indexes = {
        @Index(name = "idx_funcionario_calculo_competencia", columnList = "competencia")
})
@Getter
@Setter
@NoArgsConstructor
public class FuncionarioImportacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "competencia", nullable = false)
    private LocalDate competencia;

    @Column(name = "nome")
    private  String nome;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "codigo_externo")
    private Integer codigoExterno;

    @Column(name = "cargo")
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "centro_de_custo")
    private CentroDeCusto centroDeCusto;

    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    // Rubricas importadas e valores adicionais, monetários, em reais.
    @Column(name = "salario_8781", precision = 19, scale = 2)
    private BigDecimal salario;

    @Column(name = "pro_labore_9380", precision = 19, scale = 2)
    private BigDecimal proLabore;

    @Column(name = "adiantamento_de_salario_981", precision = 19, scale = 2)
    private BigDecimal adiantamentoDeSalario;

    @Column(name = "salario_familia_995", precision = 19, scale = 2)
    private BigDecimal salarioFamilia;

    @Column(name = "horas_extras50_banco_285", precision = 19, scale = 2)
    private BigDecimal horasExtras50Banco;

    @Column(name = "licenca_remunerada", precision = 19, scale = 2)
    private BigDecimal licencaRemunerada;

    @Column(name = "plr_873", precision = 19, scale = 2)
    private BigDecimal plr;

    @Column(name = "horas_extras50_150", precision = 19, scale = 2)
    private BigDecimal horasExtras50;

    @Column(name = "horas_extras_noturnas100_264", precision = 19, scale = 2)
    private BigDecimal horasExtrasNoturnas100;

    @Column(name = "horas_extras100_200", precision = 19, scale = 2)
    private BigDecimal horasExtras100;

    @Column(name = "hora_extra_noturna50_257", precision = 19, scale = 2)
    private BigDecimal horaExtraNoturna50;

    @Column(name = "adicional_jornada_noturno20_226", precision = 19, scale = 2)
    private BigDecimal adicionalJornadaNoturno20;

    @Column(name = "adicional_noturno20_214", precision = 19, scale = 2)
    private BigDecimal adicionalNoturno20;

    @Column(name = "diarias_de_viagens_255", precision = 19, scale = 2)
    private BigDecimal diariasDeViagens;

    @Column(name = "pernoite_256", precision = 19, scale = 2)
    private BigDecimal pernoite;

    @Column(name = "insalubridade_salario_16", precision = 19, scale = 2)
    private BigDecimal insalubridadeSalario;

    @Column(name = "dsr_geral_3", precision = 19, scale = 2)
    private BigDecimal dsrGeral;

    @Column(name = "adicional_rodotren_260", precision = 19, scale = 2)
    private BigDecimal adicionalRodotren;

    @Column(name = "comissoes_com_provisao", precision = 19, scale = 2)
    private BigDecimal comissoesComProvisao;

    @Column(name = "comissoes_sem_provisao", precision = 19, scale = 2)
    private BigDecimal comissoesSemProvisao;

    @Column(name = "cesta_basica", precision = 19, scale = 2)
    private BigDecimal cestaBasica;

    @Column(name = "provisao_de_ferias", precision = 19, scale = 2)
    private BigDecimal provisaoDeFerias;

    @Column(name = "dif_medias_ferias_805", precision = 19, scale = 2)
    private BigDecimal difMediasFerias;

    @Column(name = "provisao13_salario", precision = 19, scale = 2)
    private BigDecimal provisao13Salario;

    @Column(name = "vale_transporte", precision = 19, scale = 2)
    private BigDecimal valeTransporte;

    @Column(name = "vale_refeicao", precision = 19, scale = 2)
    private BigDecimal valeRefeicao;

    @Column(name = "outros_encargos_sociais_trab", precision = 19, scale = 2)
    private BigDecimal outrosEncargosSociaisTrab;

    @Column(name = "r_aviso_previo_indenizado_9591", precision = 19, scale = 2)
    private BigDecimal rAvisoPrevioIndenizado;

    @Column(name = "reflexo_extras_dsr_250", precision = 19, scale = 2)
    private BigDecimal reflexoExtrasDsr;

    @Column(name = "dsr_variavel_sadic_jornada_noturno20_854", precision = 19, scale = 2)
    private BigDecimal dsrVariavelSAdicJornadaNoturno20;

    @Column(name = "dsr_sadicional_noturno20", precision = 19, scale = 2)
    private BigDecimal dsrSAdicionalNoturno20;

    @Column(name = "dsr_shora_extra_noturna50", precision = 19, scale = 2)
    private BigDecimal dsrSHoraExtraNoturna50;

    @Column(name = "dsr_shoras_extras100_262", precision = 19, scale = 2)
    private BigDecimal dsrSHorasExtras100;

    @Column(name = "abono_pecuniario_808", precision = 19, scale = 2)
    private BigDecimal abonoPecuniario;

    @Column(name = "um_terco_sabono_pecuniario_932", precision = 19, scale = 2)
    private BigDecimal umTercoSAbonoPecuniario;

    @Column(name = "dias_abono_pecuniario_ferias_8800", precision = 19, scale = 2)
    private BigDecimal diasAbonoPecuniarioFerias;

    @Column(name = "r_saldo_de_salario_9180", precision = 19, scale = 2)
    private BigDecimal rSaldoDeSalario;

    @Column(name = "r_adicional_de_ferias_64", precision = 19, scale = 2)
    private BigDecimal rAdicionalDeFerias;

    @Column(name = "r_ferias_saviso_previo", precision = 19, scale = 2)
    private BigDecimal rFeriasSAvisoPrevio;

    @Column(name = "valor_liquido_da_rescisao_51", precision = 19, scale = 2)
    private BigDecimal valorLiquidoDaRescisao;

    @Column(name = "ferias_proporcionais_29", precision = 19, scale = 2)
    private BigDecimal feriasProporcionais;

    @Column(name = "r13_salario_na_rct_8552", precision = 19, scale = 2)
    private BigDecimal r13SalarioNaRct;

    @Column(name = "treze_salario_integral_rescisao_8550", precision = 19, scale = 2)
    private BigDecimal trezeSalarioIntegralRescisao;

    @Column(name = "fgts", precision = 19, scale = 2)
    private BigDecimal fgts;

    @Column(name = "ferias_normais_mes_8783", precision = 19, scale = 2)
    private BigDecimal feriasNormaisMes;

    @Column(name = "um_terco_adicional_de_ferias_931", precision = 19, scale = 2)
    private BigDecimal umTercoAdicionalDeFerias;

    @Column(name = "adicional_de_ferias_28", precision = 19, scale = 2)
    private BigDecimal adicionalDeFerias;

    @Column(name = "santa_casa_saude_adic", precision = 19, scale = 2)
    private BigDecimal santaCasaSaudeAdic;

    @Column(name = "convenio_odontologico_adic", precision = 19, scale = 2)
    private BigDecimal convenioOdontologicoAdic;

    @Column(name = "santa_casa_saude_desc_258", precision = 19, scale = 2)
    private BigDecimal santaCasaSaudeDesc;

    @Column(name = "convenio_odontologico_desc_259", precision = 19, scale = 2)
    private BigDecimal convenioOdontologicoDesc;

    @Column(name = "convenio_ana_costa_saude_277", precision = 19, scale = 2)
    private BigDecimal convenioAnaCostaSaude;

    @Column(name = "convenio_saude_282", precision = 19, scale = 2)
    private BigDecimal convenioSaude;

    @Column(name = "vale_diversos_271", precision = 19, scale = 2)
    private BigDecimal valeDiversos;

    @Column(name = "pensao_alimenticia25_sal_liq_275", precision = 19, scale = 2)
    private BigDecimal pensaoAlimenticia25SalLiq;

    @Column(name = "desconto_emprestimo_205", precision = 19, scale = 2)
    private BigDecimal descontoEmprestimo;

    @Column(name = "faltas_injust_em_horas_8069", precision = 19, scale = 2)
    private BigDecimal faltasInjustEmHoras;

    @Column(name = "convenio_saude_coopart_280", precision = 19, scale = 2)
    private BigDecimal convenioSaudeCoopart;

    @Column(name = "convenio_de_farmacia_265", precision = 19, scale = 2)
    private BigDecimal convenioDeFarmacia;

    @Column(name = "convenio_seguros_267", precision = 19, scale = 2)
    private BigDecimal convenioSeguros;

    @Column(name = "multas_de_transito_242", precision = 19, scale = 2)
    private BigDecimal multasDeTransito;

    @Column(name = "inss_998", precision = 19, scale = 2)
    private BigDecimal inss;

    @Column(name = "inss_de_ferias_812", precision = 19, scale = 2)
    private BigDecimal inssDeFerias;

    @Column(name = "dias_afast_inss_pdoenca_8785", precision = 19, scale = 2)
    private BigDecimal diasAfastInssPDoenca;

    @Column(name = "inss_rescisao_989", precision = 19, scale = 2)
    private BigDecimal inssRescisao;

    @Column(name = "irrf_96", precision = 19, scale = 2)
    private BigDecimal irrf;

    @Column(name = "irrf_de_ferias_942", precision = 19, scale = 2)
    private BigDecimal irrfDeFerias;

    @Column(name = "descontos_totais", precision = 19, scale = 2)
    private BigDecimal descontosTotais;

    @Column(name = "custo_mo_total", precision = 19, scale = 2)
    private BigDecimal custoMoTotal;

    @Column(name = "pensao_alimenticia25_sal_900", precision = 19, scale = 2)
    private BigDecimal pensaoAlimenticia25Sal;

    @Column(name = "ferias_normais_mes_ref_9162", precision = 19, scale = 2)
    private BigDecimal feriasNormaisMesRef;

    @Column(name = "desc_adto_ferias_mes_937", precision = 19, scale = 2)
    private BigDecimal descAdtoFeriasMes;

    @Column(name = "adiantamento_de_diarias_de_viagem_225", precision = 19, scale = 2)
    private BigDecimal adiantamentoDeDiariasDeViagem;

    @Column(name = "inss_de_ferias_ref_9181", precision = 19, scale = 2)
    private BigDecimal inssDeFeriasRef;

    @Column(name = "um_terco_adicional_de_ferias_ref_8169", precision = 19, scale = 2)
    private BigDecimal umTercoAdicionalDeFeriasRef;

    @Column(name = "adicional_periculosidade_149", precision = 19, scale = 2)
    private BigDecimal adicionalPericulosidade;

    @Column(name = "horas_normais_salario_261", precision = 19, scale = 2)
    private BigDecimal horasNormaisSalario;

    @Column(name = "hora_extra_noturna100_264", precision = 19, scale = 2)
    private BigDecimal horaExtraNoturna100;

    @Column(name = "penhora_89", precision = 19, scale = 2)
    private BigDecimal penhora;

    @Column(name = "faltas_injust_dsr_em_horas_9988", precision = 19, scale = 2)
    private BigDecimal faltasInjustDsrEmHoras;

    @Column(name = "afastamento_doencas_dias_sempre_9522", precision = 19, scale = 2)
    private BigDecimal afastamentoDoencasDiasSempre;

    @Column(name = "reembolso_146", precision = 19, scale = 2)
    private BigDecimal reembolso;

    @Column(name = "seg_acidente_trabalho", precision = 19, scale = 2)
    private BigDecimal segAcidenteTrabalho;

    @Column(name = "valor_outras_entidades", precision = 19, scale = 2)
    private BigDecimal valorOutrasEntidades;

    @Column(name = "gratificacao_por_fora", precision = 19, scale = 2)
    private BigDecimal gratificacaoPorFora;

    @Column(name = "comissoes_por_fora", precision = 19, scale = 2)
    private BigDecimal comissoesPorFora;

    @Column(name = "valor_best_pontos", precision = 19, scale = 2)
    private BigDecimal valorBestPontos;

    /** Códigos e descrições utilizados por FuncionarioImportacao.getValorPorCodigo(). */
    public static List<String> codigosRubricasImportacao() {
        return CODIGOS_RUBRICAS_IMPORTACAO;
    }

    private static final List<String> CODIGOS_RUBRICAS_IMPORTACAO = List.of(
            "8781",
            "9380",
            "981",
            "995",
            "285",
            "LICENCA REMUNERADA",
            "873",
            "150",
            "200",
            "264",
            "257",
            "226",
            "214",
            "255",
            "256",
            "16",
            "3",
            "260",
            "805",
            "9591",
            "250",
            "854",
            "D.S.R. S/ ADICIONAL NOTURNO 20%",
            "D.S.R. S/ HORA EXTRA NOTURNA 50%",
            "262",
            "808",
            "932",
            "8800",
            "9180",
            "64",
            "R-FERIAS S/ AVISO PREVIO",
            "51",
            "29",
            "8552",
            "8550",
            "FGTS A RECOLHER",
            "8783",
            "931",
            "28",
            "258",
            "259",
            "282",
            "277",
            "271",
            "275",
            "205",
            "8069",
            "280",
            "265",
            "267",
            "242",
            "998",
            "812",
            "8785",
            "989",
            "96",
            "942",
            "900",
            "9162",
            "937",
            "225",
            "9181",
            "8169",
            "149",
            "261",
            "89",
            "9988",
            "9522",
            "146",
            "SEG.ACIDENTE TRABALHO",
            "VALOR OUTRAS ENTIDADES"
    );

    /**
     * Define um valor por código da folha ou pelo nome Java do atributo.
     * Códigos não mapeados provocam exceção para impedir importação silenciosa incorreta.
     * A rubrica 264 alimenta os dois campos usados no serviço original.
     */
    public void setValorPorCodigo(String codigo, BigDecimal valor) {
        Objects.requireNonNull(valor, "valor da rubrica não pode ser nulo");
        switch (normalizarCodigo(codigo)) {
            case "8781", "SALARIO" -> this.salario = valor;
            case "9380", "PROLABORE" -> this.proLabore = valor;
            case "981", "ADIANTAMENTODESALARIO" -> this.adiantamentoDeSalario = valor;
            case "995", "SALARIOFAMILIA" -> this.salarioFamilia = valor;
            case "285", "HORASEXTRAS50BANCO" -> this.horasExtras50Banco = valor;
            case "LICENCA REMUNERADA", "LICENCAREMUNERADA" -> this.licencaRemunerada = valor;
            case "873", "PLR" -> this.plr = valor;
            case "150", "HORASEXTRAS50" -> this.horasExtras50 = valor;
            case "200", "HORASEXTRAS100" -> this.horasExtras100 = valor;
            case "264" -> {
                this.horasExtrasNoturnas100 = valor;
                this.horaExtraNoturna100 = valor;
            }
            case "257", "HORAEXTRANOTURNA50" -> this.horaExtraNoturna50 = valor;
            case "226", "ADICIONALJORNADANOTURNO20" -> this.adicionalJornadaNoturno20 = valor;
            case "214", "ADICIONALNOTURNO20" -> this.adicionalNoturno20 = valor;
            case "255", "DIARIASDEVIAGENS" -> this.diariasDeViagens = valor;
            case "256", "PERNOITE" -> this.pernoite = valor;
            case "16", "INSALUBRIDADESALARIO" -> this.insalubridadeSalario = valor;
            case "3", "DSRGERAL" -> this.dsrGeral = valor;
            case "260", "ADICIONALRODOTREN" -> this.adicionalRodotren = valor;
            case "805", "DIFMEDIASFERIAS" -> this.difMediasFerias = valor;
            case "9591", "RAVISOPREVIOINDENIZADO" -> this.rAvisoPrevioIndenizado = valor;
            case "250", "REFLEXOEXTRASDSR" -> this.reflexoExtrasDsr = valor;
            case "854", "DSRVARIAVELSADICJORNADANOTURNO20" -> this.dsrVariavelSAdicJornadaNoturno20 = valor;
            case "D.S.R. S/ ADICIONAL NOTURNO 20%", "DSRSADICIONALNOTURNO20" -> this.dsrSAdicionalNoturno20 = valor;
            case "D.S.R. S/ HORA EXTRA NOTURNA 50%", "DSRSHORAEXTRANOTURNA50" -> this.dsrSHoraExtraNoturna50 = valor;
            case "262", "DSRSHORASEXTRAS100" -> this.dsrSHorasExtras100 = valor;
            case "808", "ABONOPECUNIARIO" -> this.abonoPecuniario = valor;
            case "932", "UMTERCOSABONOPECUNIARIO" -> this.umTercoSAbonoPecuniario = valor;
            case "8800", "DIASABONOPECUNIARIOFERIAS" -> this.diasAbonoPecuniarioFerias = valor;
            case "9180", "RSALDODESALARIO" -> this.rSaldoDeSalario = valor;
            case "64", "RADICIONALDEFERIAS" -> this.rAdicionalDeFerias = valor;
            case "R-FERIAS S/ AVISO PREVIO", "RFERIASSAVISOPREVIO" -> this.rFeriasSAvisoPrevio = valor;
            case "51", "VALORLIQUIDODARESCISAO" -> this.valorLiquidoDaRescisao = valor;
            case "29", "FERIASPROPORCIONAIS" -> this.feriasProporcionais = valor;
            case "8552", "R13SALARIONARCT" -> this.r13SalarioNaRct = valor;
            case "8550", "TREZESALARIOINTEGRALRESCISAO" -> this.trezeSalarioIntegralRescisao = valor;
            case "FGTS A RECOLHER", "FGTS" -> this.fgts = valor;
            case "8783", "FERIASNORMAISMES" -> this.feriasNormaisMes = valor;
            case "931", "UMTERCOADICIONALDEFERIAS" -> this.umTercoAdicionalDeFerias = valor;
            case "28", "ADICIONALDEFERIAS" -> this.adicionalDeFerias = valor;
            case "258", "SANTACASASAUDEDESC" -> this.santaCasaSaudeDesc = valor;
            case "259", "CONVENIOODONTOLOGICODESC" -> this.convenioOdontologicoDesc = valor;
            case "282", "CONVENIOSAUDE" -> this.convenioSaude = valor;
            case "277", "CONVENIOANACOSTASAUDE" -> this.convenioAnaCostaSaude = valor;
            case "271", "VALEDIVERSOS" -> this.valeDiversos = valor;
            case "275", "PENSAOALIMENTICIA25SALLIQ" -> this.pensaoAlimenticia25SalLiq = valor;
            case "205", "DESCONTOEMPRESTIMO" -> this.descontoEmprestimo = valor;
            case "8069", "FALTASINJUSTEMHORAS" -> this.faltasInjustEmHoras = valor;
            case "280", "CONVENIOSAUDECOOPART" -> this.convenioSaudeCoopart = valor;
            case "265", "CONVENIODEFARMACIA" -> this.convenioDeFarmacia = valor;
            case "267", "CONVENIOSEGUROS" -> this.convenioSeguros = valor;
            case "242", "MULTASDETRANSITO" -> this.multasDeTransito = valor;
            case "998", "INSS" -> this.inss = valor;
            case "812", "INSSDEFERIAS" -> this.inssDeFerias = valor;
            case "8785", "DIASAFASTINSSPDOENCA" -> this.diasAfastInssPDoenca = valor;
            case "989", "INSSRESCISAO" -> this.inssRescisao = valor;
            case "96", "IRRF" -> this.irrf = valor;
            case "942", "IRRFDEFERIAS" -> this.irrfDeFerias = valor;
            case "900", "PENSAOALIMENTICIA25SAL" -> this.pensaoAlimenticia25Sal = valor;
            case "9162", "FERIASNORMAISMESREF" -> this.feriasNormaisMesRef = valor;
            case "937", "DESCADTOFERIASMES" -> this.descAdtoFeriasMes = valor;
            case "225", "ADIANTAMENTODEDIARIASDEVIAGEM" -> this.adiantamentoDeDiariasDeViagem = valor;
            case "9181", "INSSDEFERIASREF" -> this.inssDeFeriasRef = valor;
            case "8169", "UMTERCOADICIONALDEFERIASREF" -> this.umTercoAdicionalDeFeriasRef = valor;
            case "149", "ADICIONALPERICULOSIDADE" -> this.adicionalPericulosidade = valor;
            case "261", "HORASNORMAISSALARIO" -> this.horasNormaisSalario = valor;
            case "89", "PENHORA" -> this.penhora = valor;
            case "9988", "FALTASINJUSTDSREMHORAS" -> this.faltasInjustDsrEmHoras = valor;
            case "9522", "AFASTAMENTODOENCASDIASSEMPRE" -> this.afastamentoDoencasDiasSempre = valor;
            case "146", "REEMBOLSO" -> this.reembolso = valor;
            case "SEG.ACIDENTE TRABALHO", "SEGACIDENTETRABALHO" -> this.segAcidenteTrabalho = valor;
            case "VALOR OUTRAS ENTIDADES", "VALOROUTRASENTIDADES" -> this.valorOutrasEntidades = valor;
            case "HORASEXTRASNOTURNAS100" -> this.horasExtrasNoturnas100 = valor;
            case "COMISSOESCOMPROVISAO" -> this.comissoesComProvisao = valor;
            case "COMISSOESSEMPROVISAO" -> this.comissoesSemProvisao = valor;
            case "CESTABASICA" -> this.cestaBasica = valor;
            case "VALETRANSPORTE" -> this.valeTransporte = valor;
            case "VALEREFEICAO" -> this.valeRefeicao = valor;
            case "HORAEXTRANOTURNA100" -> this.horaExtraNoturna100 = valor;
            case "CONVENIOODONTOLOGICOADIC", "CUSTOMOTOTAL", "DESCONTOSTOTAIS", "OUTROSENCARGOSSOCIAISTRAB", "PROVISAO13SALARIO", "PROVISAODEFERIAS", "SANTACASASAUDEADIC" ->
                    throw new IllegalArgumentException("Campo calculado: " + codigo + ". Use recalcular().");
            default -> throw new IllegalArgumentException("Rubrica não mapeada: " + codigo);
        }
    }

    /** Lê por código ou nome Java; campos ainda não preenchidos retornam zero. */
    public BigDecimal getValorPorCodigo(String codigo) {
        return switch (normalizarCodigo(codigo)) {
            case "8781", "SALARIO" -> n(this.salario);
            case "9380", "PROLABORE" -> n(this.proLabore);
            case "981", "ADIANTAMENTODESALARIO" -> n(this.adiantamentoDeSalario);
            case "995", "SALARIOFAMILIA" -> n(this.salarioFamilia);
            case "285", "HORASEXTRAS50BANCO" -> n(this.horasExtras50Banco);
            case "LICENCA REMUNERADA", "LICENCAREMUNERADA" -> n(this.licencaRemunerada);
            case "873", "PLR" -> n(this.plr);
            case "150", "HORASEXTRAS50" -> n(this.horasExtras50);
            case "200", "HORASEXTRAS100" -> n(this.horasExtras100);
            case "264" -> n(this.horasExtrasNoturnas100);
            case "257", "HORAEXTRANOTURNA50" -> n(this.horaExtraNoturna50);
            case "226", "ADICIONALJORNADANOTURNO20" -> n(this.adicionalJornadaNoturno20);
            case "214", "ADICIONALNOTURNO20" -> n(this.adicionalNoturno20);
            case "255", "DIARIASDEVIAGENS" -> n(this.diariasDeViagens);
            case "256", "PERNOITE" -> n(this.pernoite);
            case "16", "INSALUBRIDADESALARIO" -> n(this.insalubridadeSalario);
            case "3", "DSRGERAL" -> n(this.dsrGeral);
            case "260", "ADICIONALRODOTREN" -> n(this.adicionalRodotren);
            case "805", "DIFMEDIASFERIAS" -> n(this.difMediasFerias);
            case "9591", "RAVISOPREVIOINDENIZADO" -> n(this.rAvisoPrevioIndenizado);
            case "250", "REFLEXOEXTRASDSR" -> n(this.reflexoExtrasDsr);
            case "854", "DSRVARIAVELSADICJORNADANOTURNO20" -> n(this.dsrVariavelSAdicJornadaNoturno20);
            case "D.S.R. S/ ADICIONAL NOTURNO 20%", "DSRSADICIONALNOTURNO20" -> n(this.dsrSAdicionalNoturno20);
            case "D.S.R. S/ HORA EXTRA NOTURNA 50%", "DSRSHORAEXTRANOTURNA50" -> n(this.dsrSHoraExtraNoturna50);
            case "262", "DSRSHORASEXTRAS100" -> n(this.dsrSHorasExtras100);
            case "808", "ABONOPECUNIARIO" -> n(this.abonoPecuniario);
            case "932", "UMTERCOSABONOPECUNIARIO" -> n(this.umTercoSAbonoPecuniario);
            case "8800", "DIASABONOPECUNIARIOFERIAS" -> n(this.diasAbonoPecuniarioFerias);
            case "9180", "RSALDODESALARIO" -> n(this.rSaldoDeSalario);
            case "64", "RADICIONALDEFERIAS" -> n(this.rAdicionalDeFerias);
            case "R-FERIAS S/ AVISO PREVIO", "RFERIASSAVISOPREVIO" -> n(this.rFeriasSAvisoPrevio);
            case "51", "VALORLIQUIDODARESCISAO" -> n(this.valorLiquidoDaRescisao);
            case "29", "FERIASPROPORCIONAIS" -> n(this.feriasProporcionais);
            case "8552", "R13SALARIONARCT" -> n(this.r13SalarioNaRct);
            case "8550", "TREZESALARIOINTEGRALRESCISAO" -> n(this.trezeSalarioIntegralRescisao);
            case "FGTS A RECOLHER", "FGTS" -> n(this.fgts);
            case "8783", "FERIASNORMAISMES" -> n(this.feriasNormaisMes);
            case "931", "UMTERCOADICIONALDEFERIAS" -> n(this.umTercoAdicionalDeFerias);
            case "28", "ADICIONALDEFERIAS" -> n(this.adicionalDeFerias);
            case "258", "SANTACASASAUDEDESC" -> n(this.santaCasaSaudeDesc);
            case "259", "CONVENIOODONTOLOGICODESC" -> n(this.convenioOdontologicoDesc);
            case "282", "CONVENIOSAUDE" -> n(this.convenioSaude);
            case "277", "CONVENIOANACOSTASAUDE" -> n(this.convenioAnaCostaSaude);
            case "271", "VALEDIVERSOS" -> n(this.valeDiversos);
            case "275", "PENSAOALIMENTICIA25SALLIQ" -> n(this.pensaoAlimenticia25SalLiq);
            case "205", "DESCONTOEMPRESTIMO" -> n(this.descontoEmprestimo);
            case "8069", "FALTASINJUSTEMHORAS" -> n(this.faltasInjustEmHoras);
            case "280", "CONVENIOSAUDECOOPART" -> n(this.convenioSaudeCoopart);
            case "265", "CONVENIODEFARMACIA" -> n(this.convenioDeFarmacia);
            case "267", "CONVENIOSEGUROS" -> n(this.convenioSeguros);
            case "242", "MULTASDETRANSITO" -> n(this.multasDeTransito);
            case "998", "INSS" -> n(this.inss);
            case "812", "INSSDEFERIAS" -> n(this.inssDeFerias);
            case "8785", "DIASAFASTINSSPDOENCA" -> n(this.diasAfastInssPDoenca);
            case "989", "INSSRESCISAO" -> n(this.inssRescisao);
            case "96", "IRRF" -> n(this.irrf);
            case "942", "IRRFDEFERIAS" -> n(this.irrfDeFerias);
            case "900", "PENSAOALIMENTICIA25SAL" -> n(this.pensaoAlimenticia25Sal);
            case "9162", "FERIASNORMAISMESREF" -> n(this.feriasNormaisMesRef);
            case "937", "DESCADTOFERIASMES" -> n(this.descAdtoFeriasMes);
            case "225", "ADIANTAMENTODEDIARIASDEVIAGEM" -> n(this.adiantamentoDeDiariasDeViagem);
            case "9181", "INSSDEFERIASREF" -> n(this.inssDeFeriasRef);
            case "8169", "UMTERCOADICIONALDEFERIASREF" -> n(this.umTercoAdicionalDeFeriasRef);
            case "149", "ADICIONALPERICULOSIDADE" -> n(this.adicionalPericulosidade);
            case "261", "HORASNORMAISSALARIO" -> n(this.horasNormaisSalario);
            case "89", "PENHORA" -> n(this.penhora);
            case "9988", "FALTASINJUSTDSREMHORAS" -> n(this.faltasInjustDsrEmHoras);
            case "9522", "AFASTAMENTODOENCASDIASSEMPRE" -> n(this.afastamentoDoencasDiasSempre);
            case "146", "REEMBOLSO" -> n(this.reembolso);
            case "SEG.ACIDENTE TRABALHO", "SEGACIDENTETRABALHO" -> n(this.segAcidenteTrabalho);
            case "VALOR OUTRAS ENTIDADES", "VALOROUTRASENTIDADES" -> n(this.valorOutrasEntidades);
            case "HORASEXTRASNOTURNAS100" -> n(this.horasExtrasNoturnas100);
            case "COMISSOESCOMPROVISAO" -> n(this.comissoesComProvisao);
            case "COMISSOESSEMPROVISAO" -> n(this.comissoesSemProvisao);
            case "CESTABASICA" -> n(this.cestaBasica);
            case "VALETRANSPORTE" -> n(this.valeTransporte);
            case "VALEREFEICAO" -> n(this.valeRefeicao);
            case "HORAEXTRANOTURNA100" -> n(this.horaExtraNoturna100);
            case "CONVENIOODONTOLOGICOADIC" -> n(this.convenioOdontologicoAdic);
            case "CUSTOMOTOTAL" -> n(this.custoMoTotal);
            case "DESCONTOSTOTAIS" -> n(this.descontosTotais);
            case "OUTROSENCARGOSSOCIAISTRAB" -> n(this.outrosEncargosSociaisTrab);
            case "PROVISAO13SALARIO" -> n(this.provisao13Salario);
            case "PROVISAODEFERIAS" -> n(this.provisaoDeFerias);
            case "SANTACASASAUDEADIC" -> n(this.santaCasaSaudeAdic);
            default -> throw new IllegalArgumentException("Rubrica não mapeada: " + codigo);
        };
    }

    /** Normaliza códigos com zeros à esquerda, espaços e diferenças de caixa. */
    private static String normalizarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código não pode ser vazio.");
        }
        String normalizado = codigo.strip().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        return normalizado.matches("[0-9]+")
                ? normalizado.replaceFirst("^0+(?!$)", "")
                : normalizado;
    }

    @PrePersist
    @PreUpdate
    private void antesDeSalvar() {
        recalcular();
    }

    /**
     * Também chamar manualmente ao usar o objeto antes de persistir no banco.
     * Mantém os mesmos critérios e arredondamentos do serviço original.
     */
    public void recalcular() {
        if (this.competencia == null) {
            throw new IllegalStateException("Informe a competência antes do cálculo.");
        }

        this.competencia = this.competencia.withDayOfMonth(1);

        BigDecimal baseProvisoes = somar(
                this.salario,
                this.plr,
                this.horasExtras50,
                this.horasExtrasNoturnas100,
                this.horasExtras100,
                this.horaExtraNoturna50,
                this.adicionalNoturno20,
                this.insalubridadeSalario
        );

        if (!this.getNome().equalsIgnoreCase("VERA LUCIA DE LOURDES MORETTI BOM") && !this.getNome().equalsIgnoreCase("FRANCISCO RICIERI BOM")){
            if (this.cestaBasica == null) {
                this.cestaBasica = new BigDecimal("180.00");
            }

            this.comissoesSemProvisao = CentroDeCusto.TRANSF.equals(this.centroDeCusto) ? this.valorBestPontos : this.comissoesPorFora;

            this.provisaoDeFerias = baseProvisoes.divide(new BigDecimal("36"), 2, RoundingMode.HALF_UP);
            this.provisao13Salario = baseProvisoes.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

            this.convenioOdontologicoAdic = n(this.convenioOdontologicoDesc).signum() > 0
                    ? new BigDecimal("17.28") : BigDecimal.ZERO;
            this.santaCasaSaudeAdic = n(this.convenioSaude);
        }

        // ATENÇÃO: no serviço original, o ANO (ex.: 2026) soma à base de 7,3%.
        this.outrosEncargosSociaisTrab = somar(
                this.salario,
                this.proLabore,
                this.adiantamentoDeSalario,
                this.salarioFamilia,
                this.horasExtras50Banco,
                this.licencaRemunerada,
                this.horasExtras50,
                this.horasExtrasNoturnas100,
                this.horasExtras100,
                this.horaExtraNoturna50,
                this.adicionalJornadaNoturno20,
                this.adicionalNoturno20,
                this.insalubridadeSalario,
                this.provisaoDeFerias,
                this.reflexoExtrasDsr,
                this.dsrVariavelSAdicJornadaNoturno20,
                this.dsrSAdicionalNoturno20,
                this.dsrSHoraExtraNoturna50,
                this.dsrSHorasExtras100,
                this.abonoPecuniario,
                this.umTercoSAbonoPecuniario,
                this.diasAbonoPecuniarioFerias,
                this.rSaldoDeSalario,
                this.rAdicionalDeFerias,
                this.rFeriasSAvisoPrevio,
                this.valorLiquidoDaRescisao,
                this.feriasProporcionais,
                this.r13SalarioNaRct,
                this.trezeSalarioIntegralRescisao
        )
                .subtract(n(this.descontoEmprestimo))
                .subtract(n(this.faltasInjustEmHoras))
                .add(BigDecimal.valueOf(this.competencia.getYear()))
                .multiply(new BigDecimal("0.073"))
                .setScale(2, RoundingMode.HALF_UP);

        this.descontosTotais = somar(
                this.feriasNormaisMes,
                this.umTercoAdicionalDeFerias,
                this.adicionalDeFerias,
                this.santaCasaSaudeDesc,
                this.convenioOdontologicoDesc,
                this.convenioAnaCostaSaude,
                this.convenioSaude,
                this.valeDiversos,
                this.pensaoAlimenticia25SalLiq,
                this.descontoEmprestimo,
                this.faltasInjustEmHoras,
                this.convenioSaudeCoopart,
                this.convenioDeFarmacia,
                this.convenioSeguros,
                this.multasDeTransito,
                this.inss,
                this.inssDeFerias,
                this.diasAfastInssPDoenca,
                this.inssRescisao,
                this.irrf,
                this.irrfDeFerias,
                this.pensaoAlimenticia25Sal,
                this.inssDeFeriasRef,
                this.penhora,
                this.faltasInjustDsrEmHoras
        );

        // Original: custo total soma estas verbas; não subtrai descontosTotais.
        this.custoMoTotal = somar(
                this.salario,
                this.proLabore,
                this.adiantamentoDeSalario,
                this.salarioFamilia,
                this.horasExtras50Banco,
                this.licencaRemunerada,
                this.plr,
                this.horasExtras50,
                this.horasExtrasNoturnas100,
                this.horasExtras100,
                this.horaExtraNoturna50,
                this.adicionalJornadaNoturno20,
                this.adicionalNoturno20,
                this.diariasDeViagens,
                this.pernoite,
                this.insalubridadeSalario,
                this.dsrGeral,
                this.adicionalRodotren,
                this.comissoesComProvisao,
                this.comissoesSemProvisao,
                this.cestaBasica,
                this.provisaoDeFerias,
                this.difMediasFerias,
                this.provisao13Salario,
                this.valeTransporte,
                this.valeRefeicao,
                this.outrosEncargosSociaisTrab,
                this.rAvisoPrevioIndenizado,
                this.reflexoExtrasDsr,
                this.dsrVariavelSAdicJornadaNoturno20,
                this.dsrSAdicionalNoturno20,
                this.dsrSHoraExtraNoturna50,
                this.dsrSHorasExtras100,
                this.abonoPecuniario,
                this.umTercoSAbonoPecuniario,
                this.diasAbonoPecuniarioFerias,
                this.rSaldoDeSalario,
                this.rAdicionalDeFerias,
                this.rFeriasSAvisoPrevio,
                this.valorLiquidoDaRescisao,
                this.feriasProporcionais,
                this.r13SalarioNaRct,
                this.trezeSalarioIntegralRescisao,
                this.fgts,
                this.santaCasaSaudeAdic,
                this.convenioOdontologicoAdic,
                this.feriasNormaisMesRef,
                this.descAdtoFeriasMes,
                this.adiantamentoDeDiariasDeViagem,
                this.umTercoAdicionalDeFeriasRef,
                this.adicionalPericulosidade,
                this.horasNormaisSalario,
                this.horaExtraNoturna100,
                this.afastamentoDoencasDiasSempre,
                this.reembolso,
                this.segAcidenteTrabalho,
                this.valorOutrasEntidades
        );
    }

    private static BigDecimal n(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static BigDecimal somar(BigDecimal... valores) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal valor : valores) {
            total = total.add(n(valor));
        }
        return total;
    }

}
