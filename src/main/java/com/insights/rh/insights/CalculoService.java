package com.insights.rh.insights;

import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.insights.model.CalculoTotal;
import com.insights.rh.insights.model.FuncionarioCalculo;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculoService {

    private final FuncionarioImportacaoRepository repository;

    public List<FuncionarioCalculo> calcularListaMOFuncionarios(LocalDate competencia) {
        competencia = LocalDate.of(competencia.getYear(), competencia.getMonth(), 1);
        List<FuncionarioImportacao> funcionariosImportacao = repository.findAllByCompetencia(competencia);
        List<FuncionarioCalculo> listaCalculos = new ArrayList<>();

        for (FuncionarioImportacao funcionarioImportacao : funcionariosImportacao) {
            String nome =  funcionarioImportacao.getFuncionario().getNome();
            String cargo =  funcionarioImportacao.getFuncionario().getCargo();
            CentroDeCusto centroDeCusto = funcionarioImportacao.getFuncionario().getCentroDeCusto();
            LocalDate dataAdmissao = funcionarioImportacao.getFuncionario().getDataAdmissao();

            BigDecimal salario = funcionarioImportacao.getValorPorCodigo("8781");
            BigDecimal proLabore =  funcionarioImportacao.getValorPorCodigo("9380");
            BigDecimal adiantamentoDeSalario = funcionarioImportacao.getValorPorCodigo("981");
            BigDecimal salarioFamilia = funcionarioImportacao.getValorPorCodigo("995");
            BigDecimal horasExtras50Banco = funcionarioImportacao.getValorPorCodigo("285");
            BigDecimal licencaRemunerada = funcionarioImportacao.getValorPorCodigo("LICENCA REMUNERADA");
            BigDecimal plr = funcionarioImportacao.getValorPorCodigo("873");
            BigDecimal horasExtras50 = funcionarioImportacao.getValorPorCodigo("150");
            BigDecimal horasExtras100 = funcionarioImportacao.getValorPorCodigo("200");
            BigDecimal horasExtrasNoturnas100 = funcionarioImportacao.getValorPorCodigo("264");
            BigDecimal horaExtraNoturna50 = funcionarioImportacao.getValorPorCodigo("257");
            BigDecimal adicionalJornadaNoturno20 = funcionarioImportacao.getValorPorCodigo("226");
            BigDecimal adicionalNoturno20 = funcionarioImportacao.getValorPorCodigo("214");
            BigDecimal diariasDeViagens = funcionarioImportacao.getValorPorCodigo("255");
            BigDecimal pernoite = funcionarioImportacao.getValorPorCodigo("256");
            BigDecimal insalubridadeSalario = funcionarioImportacao.getValorPorCodigo("16");
            BigDecimal dsrGeral = funcionarioImportacao.getValorPorCodigo("3");
            BigDecimal adicionalRodotren = funcionarioImportacao.getValorPorCodigo("260");

            BigDecimal comissoesComProvisao = funcionarioImportacao.getGratificacaoPorFora();
            BigDecimal comissoesSemProvisao = CentroDeCusto.TRANSF.equals(funcionarioImportacao.getFuncionario().getCentroDeCusto()) ? funcionarioImportacao.getValorBestPontos() : funcionarioImportacao.getComissoesPorFora();
            BigDecimal cestaBasica = BigDecimal.valueOf(180);
            BigDecimal provisaoDeFerias = init(salario)
                    .add(init(plr))
                    .add(init(horasExtras50))
                    .add(init(horasExtrasNoturnas100))
                    .add(init(horasExtras100))
                    .add(init(horaExtraNoturna50))
                    .add(init(adicionalNoturno20))
                    .add(init(insalubridadeSalario))
                    .divide(BigDecimal.valueOf(36), 2, RoundingMode.HALF_UP);

            BigDecimal difMediasFerias = funcionarioImportacao.getValorPorCodigo("805");
            BigDecimal provisao13Salario = init(salario)
                    .add(init(plr))
                    .add(init(horasExtras50))
                    .add(init(horasExtrasNoturnas100))
                    .add(init(horasExtras100))
                    .add(init(horaExtraNoturna50))
                    .add(init(adicionalNoturno20))
                    .add(init(insalubridadeSalario))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

            BigDecimal valeTransporte = funcionarioImportacao.getValeTransportePorFora();
            BigDecimal valeRefeicao = funcionarioImportacao.getValeRefeicaoPorFora();

            BigDecimal rAvisoPrevioIndenizado = funcionarioImportacao.getValorPorCodigo("9591");
            BigDecimal reflexoExtrasDsr = funcionarioImportacao.getValorPorCodigo("250");
            BigDecimal dsrVariavelSAdicJornadaNoturno20 = funcionarioImportacao.getValorPorCodigo("854");
            BigDecimal dsrSAdicionalNoturno20 = funcionarioImportacao.getValorPorCodigo("D.S.R. S/ ADICIONAL NOTURNO 20%");
            BigDecimal dsrSHoraExtraNoturna50 = funcionarioImportacao.getValorPorCodigo("D.S.R. S/ HORA EXTRA NOTURNA 50%");
            BigDecimal dsrSHorasExtras100 = funcionarioImportacao.getValorPorCodigo("262");
            BigDecimal abonoPecuniario = funcionarioImportacao.getValorPorCodigo("808");
            BigDecimal umTercoSAbonoPecuniario = funcionarioImportacao.getValorPorCodigo("932");
            BigDecimal diasAbonoPecuniarioFerias = funcionarioImportacao.getValorPorCodigo("8800");
            BigDecimal rSaldoDeSalario = funcionarioImportacao.getValorPorCodigo("9180");
            BigDecimal rAdicionalDeFerias = funcionarioImportacao.getValorPorCodigo("64");
            BigDecimal rFeriasSAvisoPrevio = funcionarioImportacao.getValorPorCodigo("R-FERIAS S/ AVISO PREVIO");
            BigDecimal valorLiquidoDaRescisao = funcionarioImportacao.getValorPorCodigo("51");
            BigDecimal feriasProporcionais = funcionarioImportacao.getValorPorCodigo("29");
            BigDecimal r13SalarioNaRct = funcionarioImportacao.getValorPorCodigo("8552");
            BigDecimal trezeSalarioIntegralRescisao = funcionarioImportacao.getValorPorCodigo("8550");
            BigDecimal fgts = funcionarioImportacao.getValorPorCodigo("FGTS A RECOLHER");
            BigDecimal feriasNormaisMes = funcionarioImportacao.getValorPorCodigo("8783");
            BigDecimal umTercoAdicionalDeFerias = funcionarioImportacao.getValorPorCodigo("931");
            BigDecimal adicionalDeFerias = funcionarioImportacao.getValorPorCodigo("28");
            BigDecimal santaCasaSaudeDesc = funcionarioImportacao.getValorPorCodigo("258");
            BigDecimal convenioOdontologicoDesc = funcionarioImportacao.getValorPorCodigo("259");
            BigDecimal convenioOdontologicoAdic = init(convenioOdontologicoDesc).compareTo(BigDecimal.ZERO) > 0
                    ? BigDecimal.valueOf(17.28)
                    : BigDecimal.ZERO;
            BigDecimal convenioSaude = funcionarioImportacao.getValorPorCodigo("282");
            BigDecimal santaCasaSaudeAdic = convenioSaude;
            BigDecimal convenioAnaCostaSaude = funcionarioImportacao.getValorPorCodigo("277");
            BigDecimal valeDiversos = funcionarioImportacao.getValorPorCodigo("271");
            BigDecimal pensaoAlimenticia25SalLiq = funcionarioImportacao.getValorPorCodigo("275");
            BigDecimal descontoEmprestimo = funcionarioImportacao.getValorPorCodigo("205");
            BigDecimal faltasInjustEmHoras = funcionarioImportacao.getValorPorCodigo("8069");
            BigDecimal convenioSaudeCoopart = funcionarioImportacao.getValorPorCodigo("280");
            BigDecimal convenioDeFarmacia = funcionarioImportacao.getValorPorCodigo("265");
            BigDecimal convenioSeguros = funcionarioImportacao.getValorPorCodigo("267");
            BigDecimal multasDeTransito = funcionarioImportacao.getValorPorCodigo("242");
            BigDecimal inss = funcionarioImportacao.getValorPorCodigo("998");
            BigDecimal inssDeFerias = funcionarioImportacao.getValorPorCodigo("812");
            BigDecimal diasAfastInssPDoenca = funcionarioImportacao.getValorPorCodigo("8785");
            BigDecimal inssRescisao = funcionarioImportacao.getValorPorCodigo("989");
            BigDecimal irrf = funcionarioImportacao.getValorPorCodigo("96");
            BigDecimal irrfDeFerias = funcionarioImportacao.getValorPorCodigo("942");
            BigDecimal pensaoAlimenticia25Sal = funcionarioImportacao.getValorPorCodigo("900");
            BigDecimal feriasNormaisMesRef = funcionarioImportacao.getValorPorCodigo("9162");
            BigDecimal descAdtoFeriasMes = funcionarioImportacao.getValorPorCodigo("937");
            BigDecimal adiantamentoDeDiariasDeViagem = funcionarioImportacao.getValorPorCodigo("225");
            BigDecimal inssDeFeriasRef = funcionarioImportacao.getValorPorCodigo("9181");
            BigDecimal umTercoAdicionalDeFeriasRef = funcionarioImportacao.getValorPorCodigo("8169");
            BigDecimal adicionalPericulosidade = funcionarioImportacao.getValorPorCodigo("149");
            BigDecimal horasNormaisSalario = funcionarioImportacao.getValorPorCodigo("261");
            BigDecimal horaExtraNoturna100 = funcionarioImportacao.getValorPorCodigo("264");
            BigDecimal penhora = funcionarioImportacao.getValorPorCodigo("89");
            BigDecimal faltasInjustDsrEmHoras = funcionarioImportacao.getValorPorCodigo("9988");
            BigDecimal afastamentoDoencasDiasSempre = funcionarioImportacao.getValorPorCodigo("9522");
            BigDecimal reembolso = funcionarioImportacao.getValorPorCodigo("146");
            BigDecimal segAcidenteTrabalho = funcionarioImportacao.getValorPorCodigo("SEG.ACIDENTE TRABALHO");
            BigDecimal valorOutrasEntidades = funcionarioImportacao.getValorPorCodigo("VALOR OUTRAS ENTIDADES");

            BigDecimal outrosEncargosSociaisTrab = init(salario)
                    .add(init(proLabore))
                    .add(init(adiantamentoDeSalario))
                    .add(init(salarioFamilia))
                    .add(init(horasExtras50Banco))
                    .add(init(licencaRemunerada))
                    .add(init(horasExtras50))
                    .add(init(horasExtrasNoturnas100))
                    .add(init(horasExtras100))
                    .add(init(horaExtraNoturna50))
                    .add(init(adicionalJornadaNoturno20))
                    .add(init(adicionalNoturno20))
                    .add(init(insalubridadeSalario))
                    .add(init(provisaoDeFerias))
                    .add(init(reflexoExtrasDsr))
                    .add(init(dsrVariavelSAdicJornadaNoturno20))
                    .add(init(dsrSAdicionalNoturno20))
                    .add(init(dsrSHoraExtraNoturna50))
                    .add(init(dsrSHorasExtras100))
                    .add(init(abonoPecuniario))
                    .add(init(umTercoSAbonoPecuniario))
                    .add(init(diasAbonoPecuniarioFerias))
                    .add(init(rSaldoDeSalario))
                    .add(init(rAdicionalDeFerias))
                    .add(init(rFeriasSAvisoPrevio))
                    .add(init(valorLiquidoDaRescisao))
                    .add(init(feriasProporcionais))
                    .add(init(r13SalarioNaRct))
                    .add(init(trezeSalarioIntegralRescisao))
                    .subtract(init(descontoEmprestimo))
                    .subtract(init(faltasInjustEmHoras))
                    .add(init(BigDecimal.valueOf(funcionarioImportacao.getCompetencia().getYear())))
                    .multiply(new BigDecimal("0.073"))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal descontosTotais = init(feriasNormaisMes)
                    .add(init(umTercoAdicionalDeFerias))
                    .add(init(adicionalDeFerias))
                    .add(init(santaCasaSaudeDesc))
                    .add(init(convenioOdontologicoDesc))
                    .add(init(convenioAnaCostaSaude))
                    .add(init(convenioSaude))
                    .add(init(valeDiversos))
                    .add(init(pensaoAlimenticia25SalLiq))
                    .add(init(descontoEmprestimo))
                    .add(init(faltasInjustEmHoras))
                    .add(init(convenioSaudeCoopart))
                    .add(init(convenioDeFarmacia))
                    .add(init(convenioSeguros))
                    .add(init(multasDeTransito))
                    .add(init(inss))
                    .add(init(inssDeFerias))
                    .add(init(diasAfastInssPDoenca))
                    .add(init(inssRescisao))
                    .add(init(irrf))
                    .add(init(irrfDeFerias))
                    .add(init(pensaoAlimenticia25Sal))
                    .add(init(inssDeFeriasRef))
                    .add(init(penhora))
                    .add(init(faltasInjustDsrEmHoras));

            BigDecimal custoMoTotal = init(salario)
                    .add(init(proLabore))
                    .add(init(adiantamentoDeSalario))
                    .add(init(salarioFamilia))
                    .add(init(horasExtras50Banco))
                    .add(init(licencaRemunerada))
                    .add(init(plr))
                    .add(init(horasExtras50))
                    .add(init(horasExtrasNoturnas100))
                    .add(init(horasExtras100))
                    .add(init(horaExtraNoturna50))
                    .add(init(adicionalJornadaNoturno20))
                    .add(init(adicionalNoturno20))
                    .add(init(diariasDeViagens))
                    .add(init(pernoite))
                    .add(init(insalubridadeSalario))
                    .add(init(dsrGeral))
                    .add(init(adicionalRodotren))
                    .add(init(comissoesComProvisao))
                    .add(init(comissoesSemProvisao))
                    .add(init(cestaBasica))
                    .add(init(provisaoDeFerias))
                    .add(init(difMediasFerias))
                    .add(init(provisao13Salario))
                    .add(init(valeTransporte))
                    .add(init(valeRefeicao))
                    .add(init(outrosEncargosSociaisTrab))
                    .add(init(rAvisoPrevioIndenizado))
                    .add(init(reflexoExtrasDsr))
                    .add(init(dsrVariavelSAdicJornadaNoturno20))
                    .add(init(dsrSAdicionalNoturno20))
                    .add(init(dsrSHoraExtraNoturna50))
                    .add(init(dsrSHorasExtras100))
                    .add(init(abonoPecuniario))
                    .add(init(umTercoSAbonoPecuniario))
                    .add(init(diasAbonoPecuniarioFerias))
                    .add(init(rSaldoDeSalario))
                    .add(init(rAdicionalDeFerias))
                    .add(init(rFeriasSAvisoPrevio))
                    .add(init(valorLiquidoDaRescisao))
                    .add(init(feriasProporcionais))
                    .add(init(r13SalarioNaRct))
                    .add(init(trezeSalarioIntegralRescisao))
                    .add(init(fgts))
                    .add(init(santaCasaSaudeAdic))
                    .add(init(convenioOdontologicoAdic))
                    .add(init(feriasNormaisMesRef))
                    .add(init(descAdtoFeriasMes))
                    .add(init(adiantamentoDeDiariasDeViagem))
                    .add(init(umTercoAdicionalDeFeriasRef))
                    .add(init(adicionalPericulosidade))
                    .add(init(horasNormaisSalario))
                    .add(init(horaExtraNoturna100))
                    .add(init(afastamentoDoencasDiasSempre))
                    .add(init(reembolso))
                    .add(init(segAcidenteTrabalho))
                    .add(init(valorOutrasEntidades));

            FuncionarioCalculo funcionarioCalculo = new FuncionarioCalculo(
                    nome,
                    cargo,
                    centroDeCusto,
                    dataAdmissao,
                    salario,
                    proLabore,
                    adiantamentoDeSalario,
                    salarioFamilia,
                    horasExtras50Banco,
                    licencaRemunerada,
                    plr,
                    horasExtras50,
                    horasExtrasNoturnas100,
                    horasExtras100,
                    horaExtraNoturna50,
                    adicionalJornadaNoturno20,
                    adicionalNoturno20,
                    diariasDeViagens,
                    pernoite,
                    insalubridadeSalario,
                    dsrGeral,
                    adicionalRodotren,
                    comissoesComProvisao,
                    comissoesSemProvisao,
                    cestaBasica,
                    provisaoDeFerias,
                    difMediasFerias,
                    provisao13Salario,
                    valeTransporte,
                    valeRefeicao,
                    outrosEncargosSociaisTrab,
                    rAvisoPrevioIndenizado,
                    reflexoExtrasDsr,
                    dsrVariavelSAdicJornadaNoturno20,
                    dsrSAdicionalNoturno20,
                    dsrSHoraExtraNoturna50,
                    dsrSHorasExtras100,
                    abonoPecuniario,
                    umTercoSAbonoPecuniario,
                    diasAbonoPecuniarioFerias,
                    rSaldoDeSalario,
                    rAdicionalDeFerias,
                    rFeriasSAvisoPrevio,
                    valorLiquidoDaRescisao,
                    feriasProporcionais,
                    r13SalarioNaRct,
                    trezeSalarioIntegralRescisao,
                    fgts,
                    feriasNormaisMes,
                    umTercoAdicionalDeFerias,
                    adicionalDeFerias,
                    santaCasaSaudeAdic,
                    convenioOdontologicoAdic,
                    santaCasaSaudeDesc,
                    convenioOdontologicoDesc,
                    convenioAnaCostaSaude,
                    convenioSaude,
                    valeDiversos,
                    pensaoAlimenticia25SalLiq,
                    descontoEmprestimo,
                    faltasInjustEmHoras,
                    convenioSaudeCoopart,
                    convenioDeFarmacia,
                    convenioSeguros,
                    multasDeTransito,
                    inss,
                    inssDeFerias,
                    diasAfastInssPDoenca,
                    inssRescisao,
                    irrf,
                    irrfDeFerias,
                    descontosTotais,
                    custoMoTotal,
                    pensaoAlimenticia25Sal,
                    feriasNormaisMesRef,
                    descAdtoFeriasMes,
                    adiantamentoDeDiariasDeViagem,
                    inssDeFeriasRef,
                    umTercoAdicionalDeFeriasRef,
                    adicionalPericulosidade,
                    horasNormaisSalario,
                    horaExtraNoturna100,
                    penhora,
                    faltasInjustDsrEmHoras,
                    afastamentoDoencasDiasSempre,
                    reembolso,
                    segAcidenteTrabalho,
                    valorOutrasEntidades

            );

            if(funcionarioCalculo.nome().equalsIgnoreCase("ADENILSON CAMPOS SENA")) {
                System.out.println(funcionarioCalculo.toString());
            }
            listaCalculos.add(funcionarioCalculo);
        }

        return listaCalculos;
    }


    public CalculoTotal calcularTotal(List<FuncionarioCalculo> listaCalculos) {
        return new CalculoTotal(
                somar(listaCalculos, FuncionarioCalculo::salario),
                somar(listaCalculos, FuncionarioCalculo::proLabore),
                somar(listaCalculos, FuncionarioCalculo::adiantamentoDeSalario),
                somar(listaCalculos, FuncionarioCalculo::salarioFamilia),
                somar(listaCalculos, FuncionarioCalculo::horasExtras50Banco),
                somar(listaCalculos, FuncionarioCalculo::licencaRemunerada),
                somar(listaCalculos, FuncionarioCalculo::plr),
                somar(listaCalculos, FuncionarioCalculo::horasExtras50),
                somar(listaCalculos, FuncionarioCalculo::horasExtrasNoturnas100),
                somar(listaCalculos, FuncionarioCalculo::horasExtras100),
                somar(listaCalculos, FuncionarioCalculo::horaExtraNoturna50),
                somar(listaCalculos, FuncionarioCalculo::adicionalJornadaNoturno20),
                somar(listaCalculos, FuncionarioCalculo::adicionalNoturno20),
                somar(listaCalculos, FuncionarioCalculo::diariasDeViagens),
                somar(listaCalculos, FuncionarioCalculo::pernoite),
                somar(listaCalculos, FuncionarioCalculo::insalubridadeSalario),
                somar(listaCalculos, FuncionarioCalculo::dsrGeral),
                somar(listaCalculos, FuncionarioCalculo::adicionalRodotren),
                somar(listaCalculos, FuncionarioCalculo::comissoesComProvisao),
                somar(listaCalculos, FuncionarioCalculo::comissoesSemProvisao),
                somar(listaCalculos, FuncionarioCalculo::cestaBasica),
                somar(listaCalculos, FuncionarioCalculo::provisaoDeFerias),
                somar(listaCalculos, FuncionarioCalculo::difMediasFerias),
                somar(listaCalculos, FuncionarioCalculo::provisao13Salario),
                somar(listaCalculos, FuncionarioCalculo::valeTransporte),
                somar(listaCalculos, FuncionarioCalculo::valeRefeicao),
                somar(listaCalculos, FuncionarioCalculo::outrosEncargosSociaisTrab),
                somar(listaCalculos, FuncionarioCalculo::rAvisoPrevioIndenizado),
                somar(listaCalculos, FuncionarioCalculo::reflexoExtrasDsr),
                somar(listaCalculos, FuncionarioCalculo::dsrVariavelSAdicJornadaNoturno20),
                somar(listaCalculos, FuncionarioCalculo::dsrSAdicionalNoturno20),
                somar(listaCalculos, FuncionarioCalculo::dsrSHoraExtraNoturna50),
                somar(listaCalculos, FuncionarioCalculo::dsrSHorasExtras100),
                somar(listaCalculos, FuncionarioCalculo::abonoPecuniario),
                somar(listaCalculos, FuncionarioCalculo::umTercoSAbonoPecuniario),
                somar(listaCalculos, FuncionarioCalculo::diasAbonoPecuniarioFerias),
                somar(listaCalculos, FuncionarioCalculo::rSaldoDeSalario),
                somar(listaCalculos, FuncionarioCalculo::rAdicionalDeFerias),
                somar(listaCalculos, FuncionarioCalculo::rFeriasSAvisoPrevio),
                somar(listaCalculos, FuncionarioCalculo::valorLiquidoDaRescisao),
                somar(listaCalculos, FuncionarioCalculo::feriasProporcionais),
                somar(listaCalculos, FuncionarioCalculo::r13SalarioNaRct),
                somar(listaCalculos, FuncionarioCalculo::trezeSalarioIntegralRescisao),
                somar(listaCalculos, FuncionarioCalculo::fgts),
                somar(listaCalculos, FuncionarioCalculo::feriasNormaisMes),
                somar(listaCalculos, FuncionarioCalculo::umTercoAdicionalDeFerias),
                somar(listaCalculos, FuncionarioCalculo::adicionalDeFerias),
                somar(listaCalculos, FuncionarioCalculo::santaCasaSaudeAdic),
                somar(listaCalculos, FuncionarioCalculo::convenioOdontologicoAdic),
                somar(listaCalculos, FuncionarioCalculo::santaCasaSaudeDesc),
                somar(listaCalculos, FuncionarioCalculo::convenioOdontologicoDesc),
                somar(listaCalculos, FuncionarioCalculo::convenioAnaCostaSaude),
                somar(listaCalculos, FuncionarioCalculo::convenioSaude),
                somar(listaCalculos, FuncionarioCalculo::valeDiversos),
                somar(listaCalculos, FuncionarioCalculo::pensaoAlimenticia25SalLiq),
                somar(listaCalculos, FuncionarioCalculo::descontoEmprestimo),
                somar(listaCalculos, FuncionarioCalculo::faltasInjustEmHoras),
                somar(listaCalculos, FuncionarioCalculo::convenioSaudeCoopart),
                somar(listaCalculos, FuncionarioCalculo::convenioDeFarmacia),
                somar(listaCalculos, FuncionarioCalculo::convenioSeguros),
                somar(listaCalculos, FuncionarioCalculo::multasDeTransito),
                somar(listaCalculos, FuncionarioCalculo::inss),
                somar(listaCalculos, FuncionarioCalculo::inssDeFerias),
                somar(listaCalculos, FuncionarioCalculo::diasAfastInssPDoenca),
                somar(listaCalculos, FuncionarioCalculo::inssRescisao),
                somar(listaCalculos, FuncionarioCalculo::irrf),
                somar(listaCalculos, FuncionarioCalculo::irrfDeFerias),
                somar(listaCalculos, FuncionarioCalculo::descontosTotais),
                somar(listaCalculos, FuncionarioCalculo::custoMoTotal),
                somar(listaCalculos, FuncionarioCalculo::pensaoAlimenticia25Sal),
                somar(listaCalculos, FuncionarioCalculo::feriasNormaisMesRef),
                somar(listaCalculos, FuncionarioCalculo::descAdtoFeriasMes),
                somar(listaCalculos, FuncionarioCalculo::adiantamentoDeDiariasDeViagem),
                somar(listaCalculos, FuncionarioCalculo::inssDeFeriasRef),
                somar(listaCalculos, FuncionarioCalculo::umTercoAdicionalDeFeriasRef),
                somar(listaCalculos, FuncionarioCalculo::adicionalPericulosidade),
                somar(listaCalculos, FuncionarioCalculo::horasNormaisSalario),
                somar(listaCalculos, FuncionarioCalculo::horaExtraNoturna100),
                somar(listaCalculos, FuncionarioCalculo::penhora),
                somar(listaCalculos, FuncionarioCalculo::faltasInjustDsrEmHoras),
                somar(listaCalculos, FuncionarioCalculo::afastamentoDoencasDiasSempre),
                somar(listaCalculos, FuncionarioCalculo::reembolso),
                somar(listaCalculos, FuncionarioCalculo::segAcidenteTrabalho),
                somar(listaCalculos, FuncionarioCalculo::valorOutrasEntidades)
        );
    }

    public Map<CentroDeCusto, CalculoTotal> calcularTotalPorCentroDeCusto(
            List<FuncionarioCalculo> calculos
    ) {
        return calculos.stream()
                .filter(f -> f.centroDeCusto() != null)
                .collect(Collectors.groupingBy(
                        FuncionarioCalculo::centroDeCusto,
                        () -> new EnumMap<>(CentroDeCusto.class),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::calcularTotal
                        )
                ));
    }


    private BigDecimal somar(
            List<FuncionarioCalculo> lista,
            Function<FuncionarioCalculo, BigDecimal> campo
    ) {
        return lista.stream()
                .map(campo)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal init(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

}
