package com.insights.rh.extracao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "folha_pagamento")
@RequiredArgsConstructor
public class FuncionarioImportacao {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

        // REMUNERAÇÃO E ADICIONAIS

        @Column(name = "rubrica_16")
        private BigDecimal insalubridade;

        @Column(name = "rubrica_149")
        private BigDecimal periculosidade;

        @Column(name = "rubrica_150")
        private BigDecimal horasExtras50;

        @Column(name = "rubrica_200")
        private BigDecimal horasExtras100;

        @Column(name = "rubrica_214")
        private BigDecimal adicionalNoturnoValor;

        @Column(name = "rubrica_226")
        private BigDecimal adicionalNoturnoHoraReduzida;

        @Column(name = "rubrica_250")
        private BigDecimal reflexoExtrasDsr;

        @Column(name = "rubrica_257")
        private BigDecimal horasExtrasNoturnas50;

        @Column(name = "rubrica_260")
        private BigDecimal adicionalMotoristaRodotrem;

        @Column(name = "rubrica_261")
        private BigDecimal horasNormais;

        @Column(name = "rubrica_264")
        private BigDecimal horasExtrasNoturnas100;

        @Column(name = "rubrica_296")
        private BigDecimal bancoHoras50;

        @Column(name = "rubrica_854")
        private BigDecimal reflexoAdicionalNoturnoDsr;

        @Column(name = "rubrica_873")
        private BigDecimal participacaoLucros;

        @Column(name = "rubrica_9360")
        private BigDecimal dsrHoristaVariavel;

        @Column(name = "rubrica_9380")
        private BigDecimal proLabore;

        // BENEFÍCIOS E REEMBOLSOS

        @Column(name = "rubrica_255")
        private BigDecimal diariasViagens;

        @Column(name = "rubrica_256")
        private BigDecimal pernoite;

        @Column(name = "rubrica_259")
        private BigDecimal planoOdontologico;

        @Column(name = "rubrica_265")
        private BigDecimal descontoFarmacia;

        @Column(name = "rubrica_267")
        private BigDecimal convenioSeguros;

        @Column(name = "rubrica_271")
        private BigDecimal valesDiversos;

        @Column(name = "rubrica_277")
        private BigDecimal convenioAnaCosta;

        @Column(name = "rubrica_278")
        private BigDecimal convenioNotredame;

        @Column(name = "rubrica_280")
        private BigDecimal convenioSaudeCoparticipacao;

        @Column(name = "rubrica_281")
        private BigDecimal convenioSaudeDependente;

        @Column(name = "rubrica_282")
        private BigDecimal convenioSaudeTitular;

        // DESCONTOS

        @Column(name = "rubrica_205")
        private BigDecimal emprestimo;

        @Column(name = "rubrica_242")
        private BigDecimal multaTransito;

        @Column(name = "rubrica_272")
        private BigDecimal pensaoAlimenticiaSalarioMinimo;

        @Column(name = "rubrica_283")
        private BigDecimal pensaoMensalLiquido;

        @Column(name = "rubrica_292")
        private BigDecimal contribuicaoEspontanea;

        @Column(name = "rubrica_293")
        private BigDecimal contribuicaoConfederativa;

        @Column(name = "rubrica_299")
        private BigDecimal descontoMaterial;

        @Column(name = "rubrica_981")
        private BigDecimal descontoAdiantamentoSalarial;

        @Column(name = "rubrica_990")
        private BigDecimal estouroMes;

        @Column(name = "rubrica_991")
        private BigDecimal estouroMesAnterior;

        @Column(name = "rubrica_9750")
        private BigDecimal descontoEmprestimoCreditoTrabalhador;

        // FÉRIAS E ABONOS

        @Column(name = "rubrica_29")
        private BigDecimal feriasProporcionais;

        @Column(name = "rubrica_805")
        private BigDecimal mediaValorFerias;

        @Column(name = "rubrica_806")
        private BigDecimal mediaHorasFerias;

        @Column(name = "rubrica_807")
        private BigDecimal vantagensFerias;

        @Column(name = "rubrica_808")
        private BigDecimal mediaValorAbono;

        @Column(name = "rubrica_809")
        private BigDecimal mediaHorasAbono;

        @Column(name = "rubrica_810")
        private BigDecimal vantagensAbono;

        @Column(name = "rubrica_815")
        private BigDecimal mediaHorasFeriasProporcionais;

        @Column(name = "rubrica_816")
        private BigDecimal mediaValorFeriasProporcionais;

        @Column(name = "rubrica_931")
        private BigDecimal umTercoFerias;

        @Column(name = "rubrica_932")
        private BigDecimal umTercoAbonoFerias;

        @Column(name = "rubrica_937")
        private BigDecimal adiantamentoFerias;

        @Column(name = "rubrica_8112")
        private BigDecimal diferencaUmTercoFerias;

        @Column(name = "rubrica_8169")
        private BigDecimal umTercoFeriasProporcionaisRescisao;

        @Column(name = "rubrica_8190")
        private BigDecimal diferencaMediaValorFerias;

        @Column(name = "rubrica_8198")
        private BigDecimal diferencaMediaValorAbonoFerias;

        @Column(name = "rubrica_8417")
        private BigDecimal diferencaUmTercoAbonoFerias;

        @Column(name = "rubrica_8783")
        private BigDecimal diasFerias;

        @Column(name = "rubrica_8800")
        private BigDecimal diasAbonoPecuniarioFerias;

        // INSS E IRRF

        @Column(name = "rubrica_812")
        private BigDecimal inssFerias;

        @Column(name = "rubrica_821")
        private BigDecimal inssDiferencaFerias;

        @Column(name = "rubrica_826")
        private BigDecimal inssRescisao;

        @Column(name = "rubrica_828")
        private BigDecimal irrfRescisao;

        @Column(name = "rubrica_836")
        private BigDecimal inssDiferencaFeriasDescontoMaior;

        @Column(name = "rubrica_843")
        private BigDecimal inssEmpregador;

        @Column(name = "rubrica_942")
        private BigDecimal irrfFerias;

        @Column(name = "rubrica_989")
        private BigDecimal inssDecimoTerceiroRescisao;

        @Column(name = "rubrica_998")
        private BigDecimal inss;

        @Column(name = "rubrica_999")
        private BigDecimal impostoRenda;

        // AFASTAMENTOS E FALTAS

        @Column(name = "rubrica_896")
        private BigDecimal mediaAfastamentoDoenca;

        @Column(name = "rubrica_8054")
        private BigDecimal periculosidadeAcidenteTrabalho;

        @Column(name = "rubrica_8058")
        private BigDecimal periculosidadeDoenca;

        @Column(name = "rubrica_8785")
        private BigDecimal diasAfastamentoInssDoenca;

        @Column(name = "rubrica_8786")
        private BigDecimal diasAfastamentoAcidenteTrabalho;

        @Column(name = "rubrica_8792")
        private BigDecimal diasFaltas;

        @Column(name = "rubrica_8801")
        private BigDecimal descontoDiasAfastados;

        @Column(name = "rubrica_8808")
        private BigDecimal descontoDiasAfastadosAcidenteTrabalho;

        @Column(name = "rubrica_8923")
        private BigDecimal diasSuspensos;

        @Column(name = "rubrica_9522")
        private BigDecimal diasAfastamentoDoencaAte15Dias;

        @Column(name = "rubrica_9532")
        private BigDecimal periculosidadeAfastamentoAte15Dias;

        // RESCISÃO E 13º SALÁRIO

        @Column(name = "rubrica_51")
        private BigDecimal liquidoRescisao;

        @Column(name = "rubrica_8550")
        private BigDecimal decimoTerceiroIntegralRescisao;

        @Column(name = "rubrica_8551")
        private BigDecimal mediaHorasDecimoTerceiroRescisao;

        @Column(name = "rubrica_8552")
        private BigDecimal mediaValorDecimoTerceiroRescisao;

        @Column(name = "rubrica_9180")
        private BigDecimal saldoSalarioDias;

        // DIAS NORMAIS

        @Column(name = "rubrica_8781")
        private BigDecimal diasNormais;

        // BASES DE CÁLCULO

        @Column(name = "base_fgts")
        private BigDecimal baseFgts;

        @Column(name = "base_inss")
        private BigDecimal baseInss;

        @Column(name = "base_irrf")
        private BigDecimal baseIrrf;

        // ENCARGOS PATRONAIS

        @Column(name = "fgts_recolher")
        private BigDecimal fgtsRecolher;

        @Column(name = "seguro_acidente_trabalho")
        private BigDecimal seguroAcidenteTrabalho;

        @Column(name = "valor_outras_entidades")
        private BigDecimal valorOutrasEntidades;

        // TOTALIZADORES

        @Column(name = "total_vencimentos")
        private BigDecimal totalVencimentos;

        @Column(name = "total_descontos")
        private BigDecimal totalDescontos;

        @Column(name = "total_liquido")
        private BigDecimal totalLiquido;

    @Column(name = "data_importacao")
    LocalDateTime dataImportacao = LocalDateTime.now();

    @Column(name = "nome")
    private  String nome;

    @Column(name = "cpf")
    private String cpf;

    public void setValorPorCodigo(String codigo, BigDecimal valor) {

        codigo = codigo.trim()
                .replaceFirst("\\.0+$", "")
                .toUpperCase(java.util.Locale.ROOT);

        if (codigo == null || valor == null) {
            return;
        }

        switch (codigo) {

            // REMUNERAÇÃO E ADICIONAIS
            case "16" -> this.insalubridade = valor;
            case "149" -> this.periculosidade = valor;
            case "150" -> this.horasExtras50 = valor;
            case "200" -> this.horasExtras100 = valor;
            case "214" -> this.adicionalNoturnoValor = valor;
            case "226" -> this.adicionalNoturnoHoraReduzida = valor;
            case "250" -> this.reflexoExtrasDsr = valor;
            case "257" -> this.horasExtrasNoturnas50 = valor;
            case "260" -> this.adicionalMotoristaRodotrem = valor;
            case "261" -> this.horasNormais = valor;
            case "264" -> this.horasExtrasNoturnas100 = valor;
            case "296" -> this.bancoHoras50 = valor;
            case "854" -> this.reflexoAdicionalNoturnoDsr = valor;
            case "873" -> this.participacaoLucros = valor;
            case "9360" -> this.dsrHoristaVariavel = valor;
            case "9380" -> this.proLabore = valor;

            // BENEFÍCIOS E REEMBOLSOS
            case "255" -> this.diariasViagens = valor;
            case "256" -> this.pernoite = valor;
            case "259" -> this.planoOdontologico = valor;
            case "265" -> this.descontoFarmacia = valor;
            case "267" -> this.convenioSeguros = valor;
            case "271" -> this.valesDiversos = valor;
            case "277" -> this.convenioAnaCosta = valor;
            case "278" -> this.convenioNotredame = valor;
            case "280" -> this.convenioSaudeCoparticipacao = valor;
            case "281" -> this.convenioSaudeDependente = valor;
            case "282" -> this.convenioSaudeTitular = valor;

            // DESCONTOS
            case "205" -> this.emprestimo = valor;
            case "242" -> this.multaTransito = valor;
            case "272" -> this.pensaoAlimenticiaSalarioMinimo = valor;
            case "283" -> this.pensaoMensalLiquido = valor;
            case "292" -> this.contribuicaoEspontanea = valor;
            case "293" -> this.contribuicaoConfederativa = valor;
            case "299" -> this.descontoMaterial = valor;
            case "981" -> this.descontoAdiantamentoSalarial = valor;
            case "990" -> this.estouroMes = valor;
            case "991" -> this.estouroMesAnterior = valor;
            case "9750" -> this.descontoEmprestimoCreditoTrabalhador = valor;

            // FÉRIAS E ABONOS
            case "29" -> this.feriasProporcionais = valor;
            case "805" -> this.mediaValorFerias = valor;
            case "806" -> this.mediaHorasFerias = valor;
            case "807" -> this.vantagensFerias = valor;
            case "808" -> this.mediaValorAbono = valor;
            case "809" -> this.mediaHorasAbono = valor;
            case "810" -> this.vantagensAbono = valor;
            case "815" -> this.mediaHorasFeriasProporcionais = valor;
            case "816" -> this.mediaValorFeriasProporcionais = valor;
            case "931" -> this.umTercoFerias = valor;
            case "932" -> this.umTercoAbonoFerias = valor;
            case "937" -> this.adiantamentoFerias = valor;
            case "8112" -> this.diferencaUmTercoFerias = valor;
            case "8169" -> this.umTercoFeriasProporcionaisRescisao = valor;
            case "8190" -> this.diferencaMediaValorFerias = valor;
            case "8198" -> this.diferencaMediaValorAbonoFerias = valor;
            case "8417" -> this.diferencaUmTercoAbonoFerias = valor;
            case "8783" -> this.diasFerias = valor;
            case "8800" -> this.diasAbonoPecuniarioFerias = valor;

            // INSS E IRRF
            case "812" -> this.inssFerias = valor;
            case "821" -> this.inssDiferencaFerias = valor;
            case "826" -> this.inssRescisao = valor;
            case "828" -> this.irrfRescisao = valor;
            case "836" -> this.inssDiferencaFeriasDescontoMaior = valor;
            case "843" -> this.inssEmpregador = valor;
            case "942" -> this.irrfFerias = valor;
            case "989" -> this.inssDecimoTerceiroRescisao = valor;
            case "998" -> this.inss = valor;
            case "999" -> this.impostoRenda = valor;

            // AFASTAMENTOS E FALTAS
            case "896" -> this.mediaAfastamentoDoenca = valor;
            case "8054" -> this.periculosidadeAcidenteTrabalho = valor;
            case "8058" -> this.periculosidadeDoenca = valor;
            case "8785" -> this.diasAfastamentoInssDoenca = valor;
            case "8786" -> this.diasAfastamentoAcidenteTrabalho = valor;
            case "8792" -> this.diasFaltas = valor;
            case "8801" -> this.descontoDiasAfastados = valor;
            case "8808" -> this.descontoDiasAfastadosAcidenteTrabalho = valor;
            case "8923" -> this.diasSuspensos = valor;
            case "9522" -> this.diasAfastamentoDoencaAte15Dias = valor;
            case "9532" -> this.periculosidadeAfastamentoAte15Dias = valor;

            // RESCISÃO E 13º SALÁRIO
            case "51" -> this.liquidoRescisao = valor;
            case "8550" -> this.decimoTerceiroIntegralRescisao = valor;
            case "8551" -> this.mediaHorasDecimoTerceiroRescisao = valor;
            case "8552" -> this.mediaValorDecimoTerceiroRescisao = valor;
            case "9180" -> this.saldoSalarioDias = valor;

            // DIAS NORMAIS
            case "8781" -> this.diasNormais = valor;

            // BASES DE CÁLCULO
            case "BASE FGTS" -> this.baseFgts = valor;
            case "BASE INSS" -> this.baseInss = valor;
            case "BASE IRFF", "BASE IRRF" -> this.baseIrrf = valor;

            // ENCARGOS PATRONAIS
            case "FGTS A RECOLHER" -> this.fgtsRecolher = valor;
            case "SEG.ACIDENTE TRABALHO" ->
                    this.seguroAcidenteTrabalho = valor;
            case "VALOR OUTRAS ENTIDADES" ->
                    this.valorOutrasEntidades = valor;

            // TOTALIZADORES
            case "TOTAL DE VENCIMENTOS" ->
                    this.totalVencimentos = valor;
            case "TOTAL DE DESCONTOS" ->
                    this.totalDescontos = valor;
            case "TOTAL LIQUIDO" ->
                    this.totalLiquido = valor;

            default -> throw new IllegalArgumentException(
                    "Código de rubrica desconhecido: " + codigo
            );
        }
    }

    @Override
    public String toString() {
        return "FuncionarioImportacao{" +
                "id=" + id +
                ", insalubridade=" + insalubridade +
                ", periculosidade=" + periculosidade +
                ", horasExtras50=" + horasExtras50 +
                ", horasExtras100=" + horasExtras100 +
                ", adicionalNoturnoValor=" + adicionalNoturnoValor +
                ", adicionalNoturnoHoraReduzida=" + adicionalNoturnoHoraReduzida +
                ", reflexoExtrasDsr=" + reflexoExtrasDsr +
                ", horasExtrasNoturnas50=" + horasExtrasNoturnas50 +
                ", adicionalMotoristaRodotrem=" + adicionalMotoristaRodotrem +
                ", horasNormais=" + horasNormais +
                ", horasExtrasNoturnas100=" + horasExtrasNoturnas100 +
                ", bancoHoras50=" + bancoHoras50 +
                ", reflexoAdicionalNoturnoDsr=" + reflexoAdicionalNoturnoDsr +
                ", participacaoLucros=" + participacaoLucros +
                ", dsrHoristaVariavel=" + dsrHoristaVariavel +
                ", proLabore=" + proLabore +
                ", diariasViagens=" + diariasViagens +
                ", pernoite=" + pernoite +
                ", planoOdontologico=" + planoOdontologico +
                ", descontoFarmacia=" + descontoFarmacia +
                ", convenioSeguros=" + convenioSeguros +
                ", valesDiversos=" + valesDiversos +
                ", convenioAnaCosta=" + convenioAnaCosta +
                ", convenioNotredame=" + convenioNotredame +
                ", convenioSaudeCoparticipacao=" + convenioSaudeCoparticipacao +
                ", convenioSaudeDependente=" + convenioSaudeDependente +
                ", convenioSaudeTitular=" + convenioSaudeTitular +
                ", emprestimo=" + emprestimo +
                ", multaTransito=" + multaTransito +
                ", pensaoAlimenticiaSalarioMinimo=" + pensaoAlimenticiaSalarioMinimo +
                ", pensaoMensalLiquido=" + pensaoMensalLiquido +
                ", contribuicaoEspontanea=" + contribuicaoEspontanea +
                ", contribuicaoConfederativa=" + contribuicaoConfederativa +
                ", descontoMaterial=" + descontoMaterial +
                ", descontoAdiantamentoSalarial=" + descontoAdiantamentoSalarial +
                ", estouroMes=" + estouroMes +
                ", estouroMesAnterior=" + estouroMesAnterior +
                ", descontoEmprestimoCreditoTrabalhador=" + descontoEmprestimoCreditoTrabalhador +
                ", feriasProporcionais=" + feriasProporcionais +
                ", mediaValorFerias=" + mediaValorFerias +
                ", mediaHorasFerias=" + mediaHorasFerias +
                ", vantagensFerias=" + vantagensFerias +
                ", mediaValorAbono=" + mediaValorAbono +
                ", mediaHorasAbono=" + mediaHorasAbono +
                ", vantagensAbono=" + vantagensAbono +
                ", mediaHorasFeriasProporcionais=" + mediaHorasFeriasProporcionais +
                ", mediaValorFeriasProporcionais=" + mediaValorFeriasProporcionais +
                ", umTercoFerias=" + umTercoFerias +
                ", umTercoAbonoFerias=" + umTercoAbonoFerias +
                ", adiantamentoFerias=" + adiantamentoFerias +
                ", diferencaUmTercoFerias=" + diferencaUmTercoFerias +
                ", umTercoFeriasProporcionaisRescisao=" + umTercoFeriasProporcionaisRescisao +
                ", diferencaMediaValorFerias=" + diferencaMediaValorFerias +
                ", diferencaMediaValorAbonoFerias=" + diferencaMediaValorAbonoFerias +
                ", diferencaUmTercoAbonoFerias=" + diferencaUmTercoAbonoFerias +
                ", diasFerias=" + diasFerias +
                ", diasAbonoPecuniarioFerias=" + diasAbonoPecuniarioFerias +
                ", inssFerias=" + inssFerias +
                ", inssDiferencaFerias=" + inssDiferencaFerias +
                ", inssRescisao=" + inssRescisao +
                ", irrfRescisao=" + irrfRescisao +
                ", inssDiferencaFeriasDescontoMaior=" + inssDiferencaFeriasDescontoMaior +
                ", inssEmpregador=" + inssEmpregador +
                ", irrfFerias=" + irrfFerias +
                ", inssDecimoTerceiroRescisao=" + inssDecimoTerceiroRescisao +
                ", inss=" + inss +
                ", impostoRenda=" + impostoRenda +
                ", mediaAfastamentoDoenca=" + mediaAfastamentoDoenca +
                ", periculosidadeAcidenteTrabalho=" + periculosidadeAcidenteTrabalho +
                ", periculosidadeDoenca=" + periculosidadeDoenca +
                ", diasAfastamentoInssDoenca=" + diasAfastamentoInssDoenca +
                ", diasAfastamentoAcidenteTrabalho=" + diasAfastamentoAcidenteTrabalho +
                ", diasFaltas=" + diasFaltas +
                ", descontoDiasAfastados=" + descontoDiasAfastados +
                ", descontoDiasAfastadosAcidenteTrabalho=" + descontoDiasAfastadosAcidenteTrabalho +
                ", diasSuspensos=" + diasSuspensos +
                ", diasAfastamentoDoencaAte15Dias=" + diasAfastamentoDoencaAte15Dias +
                ", periculosidadeAfastamentoAte15Dias=" + periculosidadeAfastamentoAte15Dias +
                ", liquidoRescisao=" + liquidoRescisao +
                ", decimoTerceiroIntegralRescisao=" + decimoTerceiroIntegralRescisao +
                ", mediaHorasDecimoTerceiroRescisao=" + mediaHorasDecimoTerceiroRescisao +
                ", mediaValorDecimoTerceiroRescisao=" + mediaValorDecimoTerceiroRescisao +
                ", saldoSalarioDias=" + saldoSalarioDias +
                ", diasNormais=" + diasNormais +
                ", baseFgts=" + baseFgts +
                ", baseInss=" + baseInss +
                ", baseIrrf=" + baseIrrf +
                ", fgtsRecolher=" + fgtsRecolher +
                ", seguroAcidenteTrabalho=" + seguroAcidenteTrabalho +
                ", valorOutrasEntidades=" + valorOutrasEntidades +
                ", totalVencimentos=" + totalVencimentos +
                ", totalDescontos=" + totalDescontos +
                ", totalLiquido=" + totalLiquido +
                ", dataImportacao=" + dataImportacao +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
