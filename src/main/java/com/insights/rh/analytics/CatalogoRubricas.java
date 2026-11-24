package com.insights.rh.analytics;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Rubricas publicadas no dashboard. As origens de comissões são representadas pela rubrica calculada. */
public final class CatalogoRubricas {
    private CatalogoRubricas() {}

    public record Rubrica(String campo, String coluna, String nome, String grupo, String natureza,
                          String codigo, boolean aditiva, boolean derivada) {
        public String sql() {
            return "coalesce(f." + coluna + ", 0)";
        }
    }

    private static final List<Rubrica> TODAS = List.of(
            new Rubrica("salario", "salario_8781", "Salário", "remuneracao", "Componente do custo", "8781", true, false),
            new Rubrica("proLabore", "pro_labore_9380", "Pró-labore", "remuneracao", "Componente do custo", "9380", true, false),
            new Rubrica("adiantamentoDeSalario", "adiantamento_de_salario_981", "Adiantamento de salário", "remuneracao", "Componente do custo", "981", true, false),
            new Rubrica("salarioFamilia", "salario_familia_995", "Salário-família", "remuneracao", "Componente do custo", "995", true, false),
            new Rubrica("licencaRemunerada", "licenca_remunerada", "Licença remunerada", "remuneracao", "Componente do custo", "LICENCA REMUNERADA", true, false),
            new Rubrica("plr", "plr_873", "Participação nos lucros e resultados", "remuneracao", "Componente do custo", "873", true, false),
            new Rubrica("dsrGeral", "dsr_geral_3", "Descanso semanal remunerado", "remuneracao", "Componente do custo", "3", true, false),
            new Rubrica("horasNormaisSalario", "horas_normais_salario_261", "Horas normais de salário", "remuneracao", "Componente do custo", "261", true, false),
            new Rubrica("afastamentoDoencasDiasSempre", "afastamento_doencas_dias_sempre_9522", "Afastamento por doença", "remuneracao", "Componente do custo", "9522", true, false),
            new Rubrica("horasExtras50Banco", "horas_extras50_banco_285", "Horas extras 50% em banco", "extras", "Componente do custo", "285", true, false),
            new Rubrica("horasExtras50", "horas_extras50_150", "Horas extras 50%", "extras", "Componente do custo", "150", true, false),
            new Rubrica("horasExtrasNoturnas100", "horas_extras_noturnas100_264", "Horas extras noturnas 100%", "extras", "Componente do custo", "264", true, false),
            new Rubrica("horasExtras100", "horas_extras100_200", "Horas extras 100%", "extras", "Componente do custo", "200", true, false),
            new Rubrica("horaExtraNoturna50", "hora_extra_noturna50_257", "Hora extra noturna 50%", "extras", "Componente do custo", "257", true, false),
            new Rubrica("horaExtraNoturna100", "hora_extra_noturna100_264", "Hora extra noturna 100% (campo complementar)", "extras", "Componente do custo", "264", true, false),
            new Rubrica("adicionalJornadaNoturno20", "adicional_jornada_noturno20_226", "Adicional de jornada noturna 20%", "extras", "Componente do custo", "226", true, false),
            new Rubrica("adicionalNoturno20", "adicional_noturno20_214", "Adicional noturno 20%", "extras", "Componente do custo", "214", true, false),
            new Rubrica("insalubridadeSalario", "insalubridade_salario_16", "Insalubridade", "extras", "Componente do custo", "16", true, false),
            new Rubrica("adicionalRodotren", "adicional_rodotren_260", "Adicional rodotrem", "extras", "Componente do custo", "260", true, false),
            new Rubrica("reflexoExtrasDsr", "reflexo_extras_dsr_250", "Reflexo de extras em DSR", "extras", "Componente do custo", "250", true, false),
            new Rubrica("dsrVariavelSAdicJornadaNoturno20", "dsr_variavel_sadic_jornada_noturno20_854", "DSR variável sobre jornada noturna 20%", "extras", "Componente do custo", "854", true, false),
            new Rubrica("dsrSAdicionalNoturno20", "dsr_sadicional_noturno20", "DSR sobre adicional noturno 20%", "extras", "Componente do custo", "D.S.R. S/ ADICIONAL NOTURNO 20%", true, false),
            new Rubrica("dsrSHoraExtraNoturna50", "dsr_shora_extra_noturna50", "DSR sobre hora extra noturna 50%", "extras", "Componente do custo", "D.S.R. S/ HORA EXTRA NOTURNA 50%", true, false),
            new Rubrica("dsrSHorasExtras100", "dsr_shoras_extras100_262", "DSR sobre horas extras 100%", "extras", "Componente do custo", "262", true, false),
            new Rubrica("adicionalPericulosidade", "adicional_periculosidade_149", "Adicional de periculosidade", "extras", "Componente do custo", "149", true, false),
            new Rubrica("comissoesComProvisao", "comissoes_com_provisao", "Comissões com provisão", "comissoes", "Componente do custo", null, true, false),
            new Rubrica("comissoesSemProvisao", "comissoes_sem_provisao", "Comissões sem provisão", "comissoes", "Componente do custo", null, true, true),
            new Rubrica("cestaBasica", "cesta_basica", "Cesta básica", "beneficios", "Componente do custo", null, true, false),
            new Rubrica("valeTransporte", "vale_transporte", "Vale-transporte", "beneficios", "Componente do custo", null, true, false),
            new Rubrica("valeRefeicao", "vale_refeicao", "Vale-refeição", "beneficios", "Componente do custo", null, true, false),
            new Rubrica("convenioOdontologicoAdic", "convenio_odontologico_adic", "Convênio odontológico adicional", "beneficios", "Componente do custo", null, true, true),
            new Rubrica("santaCasaSaudeAdic", "santa_casa_saude_adic", "Santa Casa Saúde adicional", "beneficios", "Componente do custo", null, true, true),
            new Rubrica("fgts", "fgts", "FGTS", "encargos", "Componente do custo", "FGTS A RECOLHER", true, false),
            new Rubrica("outrosEncargosSociaisTrab", "outros_encargos_sociais_trab", "Outros encargos sociais e trabalhistas", "encargos", "Componente do custo", null, true, true),
            new Rubrica("segAcidenteTrabalho", "seg_acidente_trabalho", "Seguro de acidente de trabalho", "encargos", "Componente do custo", "SEG.ACIDENTE TRABALHO", true, false),
            new Rubrica("valorOutrasEntidades", "valor_outras_entidades", "Contribuições a outras entidades", "encargos", "Componente do custo", "VALOR OUTRAS ENTIDADES", true, false),
            new Rubrica("provisaoDeFerias", "provisao_de_ferias", "Provisão de férias", "provisoes", "Componente do custo", null, true, true),
            new Rubrica("provisao13Salario", "provisao13_salario", "Provisão de 13º salário", "provisoes", "Componente do custo", null, true, true),
            new Rubrica("difMediasFerias", "dif_medias_ferias_805", "Diferença de médias de férias", "ferias", "Componente do custo", "805", true, false),
            new Rubrica("rAvisoPrevioIndenizado", "r_aviso_previo_indenizado_9591", "Aviso prévio indenizado", "ferias", "Componente do custo", "9591", true, false),
            new Rubrica("abonoPecuniario", "abono_pecuniario_808", "Abono pecuniário", "ferias", "Componente do custo", "808", true, false),
            new Rubrica("umTercoSAbonoPecuniario", "um_terco_sabono_pecuniario_932", "Um terço sobre abono pecuniário", "ferias", "Componente do custo", "932", true, false),
            new Rubrica("diasAbonoPecuniarioFerias", "dias_abono_pecuniario_ferias_8800", "Dias de abono pecuniário de férias", "ferias", "Componente do custo", "8800", true, false),
            new Rubrica("rSaldoDeSalario", "r_saldo_de_salario_9180", "Saldo de salário na rescisão", "ferias", "Componente do custo", "9180", true, false),
            new Rubrica("rAdicionalDeFerias", "r_adicional_de_ferias_64", "Adicional de férias na rescisão", "ferias", "Componente do custo", "64", true, false),
            new Rubrica("rFeriasSAvisoPrevio", "r_ferias_saviso_previo", "Férias sobre aviso prévio", "ferias", "Componente do custo", "R-FERIAS S/ AVISO PREVIO", true, false),
            new Rubrica("valorLiquidoDaRescisao", "valor_liquido_da_rescisao_51", "Valor líquido da rescisão", "ferias", "Componente do custo", "51", true, false),
            new Rubrica("feriasProporcionais", "ferias_proporcionais_29", "Férias proporcionais", "ferias", "Componente do custo", "29", true, false),
            new Rubrica("r13SalarioNaRct", "r13_salario_na_rct_8552", "13º salário na rescisão", "ferias", "Componente do custo", "8552", true, false),
            new Rubrica("trezeSalarioIntegralRescisao", "treze_salario_integral_rescisao_8550", "13º salário integral na rescisão", "ferias", "Componente do custo", "8550", true, false),
            new Rubrica("feriasNormaisMesRef", "ferias_normais_mes_ref_9162", "Férias normais do mês de referência", "ferias", "Componente do custo", "9162", true, false),
            new Rubrica("descAdtoFeriasMes", "desc_adto_ferias_mes_937", "Desconto de adiantamento de férias do mês", "ferias", "Componente do custo", "937", true, false),
            new Rubrica("umTercoAdicionalDeFeriasRef", "um_terco_adicional_de_ferias_ref_8169", "Um terço adicional de férias de referência", "ferias", "Componente do custo", "8169", true, false),
            new Rubrica("diariasDeViagens", "diarias_de_viagens_255", "Diárias de viagens", "outros", "Componente do custo", "255", true, false),
            new Rubrica("pernoite", "pernoite_256", "Pernoite", "outros", "Componente do custo", "256", true, false),
            new Rubrica("adiantamentoDeDiariasDeViagem", "adiantamento_de_diarias_de_viagem_225", "Adiantamento de diárias", "outros", "Componente do custo", "225", true, false),
            new Rubrica("reembolso", "reembolso_146", "Reembolso", "outros", "Componente do custo", "146", true, false),
            new Rubrica("feriasNormaisMes", "ferias_normais_mes_8783", "Férias normais do mês", "descontos", "Componente dos descontos", "8783", false, false),
            new Rubrica("umTercoAdicionalDeFerias", "um_terco_adicional_de_ferias_931", "Um terço adicional de férias", "descontos", "Componente dos descontos", "931", false, false),
            new Rubrica("adicionalDeFerias", "adicional_de_ferias_28", "Adicional de férias", "descontos", "Componente dos descontos", "28", false, false),
            new Rubrica("santaCasaSaudeDesc", "santa_casa_saude_desc_258", "Desconto Santa Casa Saúde", "descontos", "Componente dos descontos", "258", false, false),
            new Rubrica("convenioOdontologicoDesc", "convenio_odontologico_desc_259", "Desconto de convênio odontológico", "descontos", "Componente dos descontos", "259", false, false),
            new Rubrica("convenioAnaCostaSaude", "convenio_ana_costa_saude_277", "Convênio Ana Costa Saúde", "descontos", "Componente dos descontos", "277", false, false),
            new Rubrica("convenioSaude", "convenio_saude_282", "Convênio de saúde", "descontos", "Componente dos descontos", "282", false, false),
            new Rubrica("valeDiversos", "vale_diversos_271", "Vales diversos", "descontos", "Componente dos descontos", "271", false, false),
            new Rubrica("pensaoAlimenticia25SalLiq", "pensao_alimenticia25_sal_liq_275", "Pensão alimentícia de 25% do salário líquido", "descontos", "Componente dos descontos", "275", false, false),
            new Rubrica("descontoEmprestimo", "desconto_emprestimo_205", "Desconto de empréstimo", "descontos", "Componente dos descontos", "205", false, false),
            new Rubrica("faltasInjustEmHoras", "faltas_injust_em_horas_8069", "Faltas injustificadas em horas", "descontos", "Componente dos descontos", "8069", false, false),
            new Rubrica("convenioSaudeCoopart", "convenio_saude_coopart_280", "Coparticipação no convênio de saúde", "descontos", "Componente dos descontos", "280", false, false),
            new Rubrica("convenioDeFarmacia", "convenio_de_farmacia_265", "Convênio de farmácia", "descontos", "Componente dos descontos", "265", false, false),
            new Rubrica("convenioSeguros", "convenio_seguros_267", "Convênio de seguros", "descontos", "Componente dos descontos", "267", false, false),
            new Rubrica("multasDeTransito", "multas_de_transito_242", "Multas de trânsito", "descontos", "Componente dos descontos", "242", false, false),
            new Rubrica("inss", "inss_998", "INSS", "descontos", "Componente dos descontos", "998", false, false),
            new Rubrica("inssDeFerias", "inss_de_ferias_812", "INSS de férias", "descontos", "Componente dos descontos", "812", false, false),
            new Rubrica("diasAfastInssPDoenca", "dias_afast_inss_pdoenca_8785", "Dias de afastamento pelo INSS", "descontos", "Componente dos descontos", "8785", false, false),
            new Rubrica("inssRescisao", "inss_rescisao_989", "INSS da rescisão", "descontos", "Componente dos descontos", "989", false, false),
            new Rubrica("irrf", "irrf_96", "IRRF", "descontos", "Componente dos descontos", "96", false, false),
            new Rubrica("irrfDeFerias", "irrf_de_ferias_942", "IRRF de férias", "descontos", "Componente dos descontos", "942", false, false),
            new Rubrica("pensaoAlimenticia25Sal", "pensao_alimenticia25_sal_900", "Pensão alimentícia de 25% do salário", "descontos", "Componente dos descontos", "900", false, false),
            new Rubrica("inssDeFeriasRef", "inss_de_ferias_ref_9181", "INSS de férias de referência", "descontos", "Componente dos descontos", "9181", false, false),
            new Rubrica("penhora", "penhora_89", "Penhora", "descontos", "Componente dos descontos", "89", false, false),
            new Rubrica("faltasInjustDsrEmHoras", "faltas_injust_dsr_em_horas_9988", "Faltas injustificadas em horas de DSR", "descontos", "Componente dos descontos", "9988", false, false),
            new Rubrica("gratificacaoPorFora", "gratificacao_por_fora", "Gratificação por fora", "informativos", "Valor de origem", null, false, false),
            new Rubrica("descontosTotais", "descontos_totais", "Descontos totais registrados", "totais", "Total calculado", null, false, true),
            new Rubrica("custoMoTotal", "custo_mo_total", "Custo total de mão de obra", "totais", "Total calculado", null, false, true)
    );
    private static final Map<String, Rubrica> POR_CAMPO = TODAS.stream()
            .collect(Collectors.toUnmodifiableMap(Rubrica::campo, Function.identity()));

    public static List<Rubrica> todas() {
        return TODAS;
    }

    public static Rubrica obter(String campo) {
        Rubrica rubrica = POR_CAMPO.get(campo);
        if (rubrica == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rubrica desconhecida.");
        }
        return rubrica;
    }

    public static String sqlGrupo(String grupo) {
        if (grupo.equals("descontos")) {
            return obter("descontosTotais").sql();
        }
        List<String> parcelas = TODAS.stream().filter(r -> r.aditiva() && r.grupo().equals(grupo))
                .map(Rubrica::sql).toList();
        if (parcelas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grupo sem decomposição aditiva.");
        }
        return "(" + String.join(" + ", parcelas) + ")";
    }

    public static String sqlHorasExtras() {
        return TODAS.stream().filter(r -> r.grupo().equals("extras") && r.campo().startsWith("hora"))
                .map(Rubrica::sql).collect(Collectors.joining(" + ", "(", ")"));
    }
}
