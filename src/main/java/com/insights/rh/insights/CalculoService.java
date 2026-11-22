package com.insights.rh.insights;

import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.insights.model.CalculoTotal;
import com.insights.rh.insights.model.FuncionarioCalculo;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculoService {

    private final FuncionarioImportacaoRepository repository;

    // Apenas consulta dados já calculados na entidade e os transforma em records.
    public List<FuncionarioCalculo> calcularListaMOFuncionarios(LocalDate competencia) {
        Objects.requireNonNull(competencia, "A competência é obrigatória.");
        LocalDate mes = competencia.withDayOfMonth(1);

        return repository.findAllByCompetencia(mes).stream()
                .map(this::converterParaRecord)
                .toList();
    }

    public FuncionarioCalculo converterParaRecord(FuncionarioImportacao f) {
        Objects.requireNonNull(f, "O funcionario importado não pode ser nulo.");

        return new FuncionarioCalculo(
                f.getNome(),
                f.getCargo(),
                f.getCentroDeCusto(),
                f.getDataAdmissao(),
                valorOuZero(f.getSalario()),
                valorOuZero(f.getProLabore()),
                valorOuZero(f.getAdiantamentoDeSalario()),
                valorOuZero(f.getSalarioFamilia()),
                valorOuZero(f.getHorasExtras50Banco()),
                valorOuZero(f.getLicencaRemunerada()),
                valorOuZero(f.getPlr()),
                valorOuZero(f.getHorasExtras50()),
                valorOuZero(f.getHorasExtrasNoturnas100()),
                valorOuZero(f.getHorasExtras100()),
                valorOuZero(f.getHoraExtraNoturna50()),
                valorOuZero(f.getAdicionalJornadaNoturno20()),
                valorOuZero(f.getAdicionalNoturno20()),
                valorOuZero(f.getDiariasDeViagens()),
                valorOuZero(f.getPernoite()),
                valorOuZero(f.getInsalubridadeSalario()),
                valorOuZero(f.getDsrGeral()),
                valorOuZero(f.getAdicionalRodotren()),
                valorOuZero(f.getComissoesComProvisao()),
                valorOuZero(f.getComissoesSemProvisao()),
                valorOuZero(f.getCestaBasica()),
                valorOuZero(f.getProvisaoDeFerias()),
                valorOuZero(f.getDifMediasFerias()),
                valorOuZero(f.getProvisao13Salario()),
                valorOuZero(f.getValeTransporte()),
                valorOuZero(f.getValeRefeicao()),
                valorOuZero(f.getOutrosEncargosSociaisTrab()),
                valorOuZero(f.getRAvisoPrevioIndenizado()),
                valorOuZero(f.getReflexoExtrasDsr()),
                valorOuZero(f.getDsrVariavelSAdicJornadaNoturno20()),
                valorOuZero(f.getDsrSAdicionalNoturno20()),
                valorOuZero(f.getDsrSHoraExtraNoturna50()),
                valorOuZero(f.getDsrSHorasExtras100()),
                valorOuZero(f.getAbonoPecuniario()),
                valorOuZero(f.getUmTercoSAbonoPecuniario()),
                valorOuZero(f.getDiasAbonoPecuniarioFerias()),
                valorOuZero(f.getRSaldoDeSalario()),
                valorOuZero(f.getRAdicionalDeFerias()),
                valorOuZero(f.getRFeriasSAvisoPrevio()),
                valorOuZero(f.getValorLiquidoDaRescisao()),
                valorOuZero(f.getFeriasProporcionais()),
                valorOuZero(f.getR13SalarioNaRct()),
                valorOuZero(f.getTrezeSalarioIntegralRescisao()),
                valorOuZero(f.getFgts()),
                valorOuZero(f.getFeriasNormaisMes()),
                valorOuZero(f.getUmTercoAdicionalDeFerias()),
                valorOuZero(f.getAdicionalDeFerias()),
                valorOuZero(f.getSantaCasaSaudeAdic()),
                valorOuZero(f.getConvenioOdontologicoAdic()),
                valorOuZero(f.getSantaCasaSaudeDesc()),
                valorOuZero(f.getConvenioOdontologicoDesc()),
                valorOuZero(f.getConvenioAnaCostaSaude()),
                valorOuZero(f.getConvenioSaude()),
                valorOuZero(f.getValeDiversos()),
                valorOuZero(f.getPensaoAlimenticia25SalLiq()),
                valorOuZero(f.getDescontoEmprestimo()),
                valorOuZero(f.getFaltasInjustEmHoras()),
                valorOuZero(f.getConvenioSaudeCoopart()),
                valorOuZero(f.getConvenioDeFarmacia()),
                valorOuZero(f.getConvenioSeguros()),
                valorOuZero(f.getMultasDeTransito()),
                valorOuZero(f.getInss()),
                valorOuZero(f.getInssDeFerias()),
                valorOuZero(f.getDiasAfastInssPDoenca()),
                valorOuZero(f.getInssRescisao()),
                valorOuZero(f.getIrrf()),
                valorOuZero(f.getIrrfDeFerias()),
                valorOuZero(f.getDescontosTotais()),
                valorOuZero(f.getCustoMoTotal()),
                valorOuZero(f.getPensaoAlimenticia25Sal()),
                valorOuZero(f.getFeriasNormaisMesRef()),
                valorOuZero(f.getDescAdtoFeriasMes()),
                valorOuZero(f.getAdiantamentoDeDiariasDeViagem()),
                valorOuZero(f.getInssDeFeriasRef()),
                valorOuZero(f.getUmTercoAdicionalDeFeriasRef()),
                valorOuZero(f.getAdicionalPericulosidade()),
                valorOuZero(f.getHorasNormaisSalario()),
                valorOuZero(f.getHoraExtraNoturna100()),
                valorOuZero(f.getPenhora()),
                valorOuZero(f.getFaltasInjustDsrEmHoras()),
                valorOuZero(f.getAfastamentoDoencasDiasSempre()),
                valorOuZero(f.getReembolso()),
                valorOuZero(f.getSegAcidenteTrabalho()),
                valorOuZero(f.getValorOutrasEntidades())
        );
    }

    // Soma cada rubrica dos records; nenhuma regra de cálculo individual aqui.
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
            List<FuncionarioCalculo> calculos) {
        return calculos.stream()
                .filter(f -> f.centroDeCusto() != null)
                .collect(Collectors.groupingBy(
                        FuncionarioCalculo::centroDeCusto,
                        () -> new EnumMap<>(CentroDeCusto.class),
                        Collectors.collectingAndThen(Collectors.toList(), this::calcularTotal)
                ));
    }

    private BigDecimal somar(
            List<FuncionarioCalculo> lista,
            Function<FuncionarioCalculo, BigDecimal> campo) {
        return lista.stream()
                .map(campo)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
