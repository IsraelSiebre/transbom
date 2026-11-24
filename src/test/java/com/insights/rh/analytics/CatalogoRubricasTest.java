package com.insights.rh.analytics;

import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.insights.CalculoService;
import com.insights.rh.insights.model.FuncionarioCalculo;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoRubricasTest {
    @Test
    void cobreOsCamposMonetariosPublicadosESuasColunasReais() throws Exception {
        var origensDeComissoes = Set.of("comissoesPorFora", "valorBestPontos");
        var campos = Arrays.stream(FuncionarioImportacao.class.getDeclaredFields())
                .filter(f -> f.getType() == BigDecimal.class).map(f -> f.getName())
                .filter(campo -> !origensDeComissoes.contains(campo)).toList();
        assertThat(CatalogoRubricas.todas()).extracting(CatalogoRubricas.Rubrica::campo)
                .containsExactlyInAnyOrderElementsOf(campos);
        assertThat(CatalogoRubricas.todas().stream().map(CatalogoRubricas.Rubrica::codigo)
                .filter(Objects::nonNull).distinct().toList())
                .containsExactlyInAnyOrderElementsOf(FuncionarioImportacao.codigosRubricasImportacao());
        for (var rubrica : CatalogoRubricas.todas()) {
            var field = FuncionarioImportacao.class.getDeclaredField(rubrica.campo());
            assertThat(field.getAnnotation(Column.class).name()).isEqualTo(rubrica.coluna());
        }
    }

    @ParameterizedTest
    @EnumSource(value = CentroDeCusto.class, names = {"ADM", "TRANSF"})
    void decomposicaoReconciliaAoCentavoComRegistroECalculoOficial(CentroDeCusto centro) throws Exception {
        var f = new FuncionarioImportacao();
        f.setNome("Pessoa de teste");
        f.setCompetencia(LocalDate.of(2024,4,1));
        f.setCentroDeCusto(centro);
        f.setComissoesPorFora(new BigDecimal("51.27"));
        f.setValorBestPontos(new BigDecimal("194.83"));
        int contador = 1;
        for (var r : CatalogoRubricas.todas()) {
            var campo = FuncionarioImportacao.class.getDeclaredField(r.campo());
            campo.setAccessible(true);
            campo.set(f, BigDecimal.valueOf(contador++ * 101L, 2));
        }
        f.recalcular();
        assertThat(f.getComissoesSemProvisao()).isEqualByComparingTo(centro == CentroDeCusto.TRANSF ? "194.83" : "51.27");
        BigDecimal custo = BigDecimal.ZERO, descontos = BigDecimal.ZERO;
        var calculo = new CalculoService(null).converterParaRecord(f);
        for (var r : CatalogoRubricas.todas()) {
            var campo = FuncionarioImportacao.class.getDeclaredField(r.campo());
            campo.setAccessible(true);
            var valor = (BigDecimal) campo.get(f);
            if (r.aditiva()) custo = custo.add(valor);
            if (r.grupo().equals("descontos")) descontos = descontos.add(valor);
            if (!r.grupo().equals("informativos")) {
                assertThat(FuncionarioCalculo.class.getMethod(r.campo()).invoke(calculo)).isEqualTo(valor);
            }
        }
        assertThat(custo).isEqualByComparingTo(f.getCustoMoTotal());
        assertThat(descontos).isEqualByComparingTo(f.getDescontosTotais());
        assertThat(CatalogoRubricas.sqlHorasExtras()).contains("horas_extras50_banco_285")
                .doesNotContain("adicional", "dsr_");
    }
}
