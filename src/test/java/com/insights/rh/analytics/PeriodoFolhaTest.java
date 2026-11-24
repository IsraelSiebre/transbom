package com.insights.rh.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PeriodoFolhaTest {
    private static final List<LocalDate> MESES = List.of("2024-01-01","2024-03-01","2025-01-01","2025-03-01")
            .stream().map(LocalDate::parse).toList();

    @Test void comparaMesesEscolhidosNaMesmaPosicaoDoAnoAnterior() {
        var p = new LinkedMultiValueMap<String,String>();
        p.add("ano","2025"); p.add("mes","1"); p.add("mes","3"); p.add("comparar","anual");
        var periodo = PeriodoFolha.resolver(p,MESES);
        assertThat(periodo.meses()).containsExactly(MESES.get(2),MESES.get(3));
        assertThat(periodo.base()).containsExactly(MESES.get(0),MESES.get(1));
        assertThat(periodo.comparavel()).isTrue();
    }

    @Test void lacunaImpedeComparacaoInclusiveEmIntervalosPersonalizados() {
        var p = new LinkedMultiValueMap<String,String>();
        p.add("inicio","2025-01-31"); p.add("fim","2025-03"); p.add("comparar","personalizado"); p.add("baseInicio","2024-01");
        var periodo = PeriodoFolha.resolver(p,MESES);
        assertThat(periodo.meses()).hasSize(3).contains(LocalDate.of(2025,2,1));
        assertThat(periodo.base()).hasSize(3).contains(LocalDate.of(2024,2,1));
        assertThat(periodo.comparavel()).isFalse();
    }

    @Test void validaIntervaloENormalizaCompetencia() {
        var p = new LinkedMultiValueMap<String,String>();
        p.add("inicio","2025-05"); p.add("fim","2025-01");
        assertThatThrownBy(() -> PeriodoFolha.resolver(p,MESES)).isInstanceOf(ResponseStatusException.class);
        p.clear(); p.add("competencia","2025-01-17");
        assertThat(PeriodoFolha.resolver(p,MESES).meses()).containsExactly(MESES.get(2));
    }

    @Test void percentualComBaseZeroEArredondamentoDeMediaPorPessoaMes() {
        var m = new LinkedHashMap<String,Object>();
        m.put("custo",new BigDecimal("10.01")); m.put("salario",BigDecimal.TEN); m.put("contratos",2);
        assertThat(IndicadoresFolha.medias(m).get("custoMedio")).isEqualTo(new BigDecimal("5.01"));
        IndicadoresFolha.comparar(m,new BigDecimal("10.01"),BigDecimal.ZERO);
        assertThat(m).containsEntry("variacao",null).containsEntry("diferenca",new BigDecimal("10.01"));
        assertThat(IndicadoresFolha.percentual(BigDecimal.ONE,new BigDecimal("3"))).isEqualByComparingTo("33.33");
    }

    @Test void decompoeMudancasDeQuadroEMediaSemPerderCentavos() {
        var ponte = IndicadoresFolha.decomporQuadro(Map.of("custo",100,"contratos",3),
                Map.of("custo",55,"contratos",2));
        assertThat(ponte.get("volume")).isEqualByComparingTo("27.50");
        assertThat(ponte.get("media")).isEqualByComparingTo("17.50");
        assertThat(ponte.get("volume").add(ponte.get("media"))).isEqualByComparingTo(ponte.get("diferenca"));
        assertThat(IndicadoresFolha.decomporQuadro(Map.of(),Map.of())).isEmpty();
    }
}

