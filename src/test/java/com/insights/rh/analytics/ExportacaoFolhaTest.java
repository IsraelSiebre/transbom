package com.insights.rh.analytics;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExportacaoFolhaTest {
    @Test
    void csvPreservaCentavosDelimitadoresEQuebrasDeLinha() {
        var out = new StringWriter();
        ExportacaoFolhaService.csv(out, List.of("Motorista; \"A\"\n2", new BigDecimal("-120.36"), " =1+2", "\t1"));
        assertThat(out.toString()).isEqualTo("\"Motorista; \"\"A\"\"\n2\";\"-120,36\";\"' =1+2\";\"'\t1\"\r\n");
    }

    @Test
    void relatoriosAgregadosUsamMesmaConsultaEFormatos() throws Exception {
        var analytics = mock(MaoDeObraAnalyticsService.class);
        var filtros = new LinkedMultiValueMap<String,String>();
        filtros.add("centro", "ADM");
        when(analytics.resumo(filtros)).thenReturn(Map.of("centros", List.of(Map.of("nome","=SOMA(A1)",
                "custo", new BigDecimal("2547.76"), "participacao", new BigDecimal("12.35")))));
        var service = new ExportacaoFolhaService(mock(DetalheFolhaService.class), analytics);
        var csv = new ByteArrayOutputStream();
        service.escrever("centros","csv",filtros,csv);
        assertThat(csv.toString(StandardCharsets.UTF_8)).contains("Centro de custo","2547,76","'=SOMA(A1)");
        var arquivo = new ByteArrayOutputStream();
        service.escrever("centros","xlsx",filtros,arquivo);
        try (var livro = new XSSFWorkbook(new ByteArrayInputStream(arquivo.toByteArray()))) {
            var primeira = livro.getSheetAt(0).getRow(1);
            assertThat(primeira.getCell(0).getStringCellValue()).isEqualTo("=SOMA(A1)");
            int custo = ExportacaoFolhaService.colunas("centros", filtros).indexOf("custo");
            assertThat(primeira.getCell(custo).getNumericCellValue()).isEqualTo(2547.76);
        }
        verify(analytics, times(2)).resumo(filtros);
    }

    @Test
    void exportaTodasAsLinhasDaMesmaBuscaNasColunasSelecionadas() throws Exception {
        var detalhes = mock(DetalheFolhaService.class);
        var filtros = new LinkedMultiValueMap<String,String>();
        filtros.add("busca","Silva");
        filtros.add("pagina","2");
        filtros.addAll("colunas", List.of("nome","provisao13Salario"));
        doAnswer(inv -> {
            Consumer<List<Object>> destino = inv.getArgument(2);
            destino.accept(List.of("Pessoa Silva",new BigDecimal("157.32")));
            return null;
        }).when(detalhes).percorrer(eq(filtros),eq(List.of("nome","provisao13Salario")),any());
        var csv = new ByteArrayOutputStream();
        new ExportacaoFolhaService(detalhes,mock(MaoDeObraAnalyticsService.class)).escrever("funcionarios","csv",filtros,csv);
        assertThat(csv.toString(StandardCharsets.UTF_8)).contains("\"Nome\";\"Provisão de 13º salário\"",
                "\"Pessoa Silva\";\"157,32\"").doesNotContain("CPF");
        filtros.add("colunas","cpf");
        assertThatThrownBy(() -> ExportacaoFolhaService.colunas("funcionarios",filtros))
                .hasMessageContaining("Coluna");
    }
}
