
package com.insights.rh.extracao;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ExtracaoPorForaService {

    public ResultadoExtracao processar(MultipartFile arquivo)
            throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "O arquivo Excel está vazio."
            );
        }

        List<Linha> linhasRicieri = new ArrayList<>();
        Map<String, Double> linhasBestPontos = new LinkedHashMap<>();

        try (InputStream entrada = arquivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(entrada)) {

            DataFormatter formatter =
                    new DataFormatter(Locale.forLanguageTag("pt-BR"));

            // ============================
            // GUIA RICIERI
            // A, F, G, L, P
            // ============================

            Sheet ricieri = workbook.getSheet("Ricieri");

            if (ricieri == null) {
                throw new IllegalArgumentException(
                        "Guia 'Ricieri' não encontrada."
                );
            }

            // Linha 4 do Excel = índice 3
            for (int i = 3; i <= ricieri.getLastRowNum(); i++) {

                Row row = ricieri.getRow(i);

                if (row == null) {
                    continue;
                }

                String nome = lerTexto(row.getCell(0), formatter);

                if (nome == null || nome.equalsIgnoreCase("Nome")) {
                    continue;
                }

                // Ignora títulos de seções, como "Motoristas",
                // que aparecem imediatamente antes de outro cabeçalho.
                Row proxima = ricieri.getRow(i + 1);

                if (proxima != null) {
                    String proximoNome =
                            lerTexto(proxima.getCell(0), formatter);

                    if ("Nome".equalsIgnoreCase(proximoNome)) {
                        continue;
                    }
                }

                Linha registro = new Linha(
                        nome,
                        lerNumero(row.getCell(5)),   // F - VT
                        lerNumero(row.getCell(6)),   // G - VR
                        lerNumero(row.getCell(11)),  // L - Gratificação
                        lerNumero(row.getCell(15))   // P - Comissões
                );

                linhasRicieri.add(registro);
            }

            // ============================
            // GUIA BESTPONTOS
            // A, H
            // ============================

            Sheet bestPontos = workbook.getSheet("BESTPONTOS");

            if (bestPontos == null) {
                throw new IllegalArgumentException(
                        "Guia 'BESTPONTOS' não encontrada."
                );
            }

            // Linha 6 do Excel = índice 5
            for (int i = 5; i <= bestPontos.getLastRowNum(); i++) {

                Row row = bestPontos.getRow(i);

                if (row == null) {
                    continue;
                }

                String nome = lerTexto(row.getCell(0), formatter);

                if (nome == null || nome.equalsIgnoreCase("Nome")) {
                    continue;
                }

                Double valor = lerNumero(row.getCell(7)); // H

                linhasBestPontos.put(nome, valor);
            }
        }

        return new ResultadoExtracao(
                linhasRicieri,
                linhasBestPontos
        );
    }

    // ============================
    // LEITURA SEGURA DE TEXTO
    // ============================

    private String lerTexto(Cell cell, DataFormatter formatter) {

        if (cell == null) {
            return null;
        }

        String texto = formatter.formatCellValue(cell).trim();

        return texto.isEmpty() ? null : texto;
    }

    // ============================
    // LEITURA SEGURA DE NÚMEROS
    // ============================

    private Double lerNumero(Cell cell) {

        if (cell == null) {
            return null;
        }

        CellType tipo = cell.getCellType();

        if (tipo == CellType.FORMULA) {
            tipo = cell.getCachedFormulaResultType();
        }

        return switch (tipo) {

            case NUMERIC -> cell.getNumericCellValue();

            case STRING -> {
                String texto = cell.getStringCellValue().trim();

                if (texto.isEmpty()) {
                    yield null;
                }

                try {
                    texto = texto
                            .replace("R$", "")
                            .replace(" ", "")
                            .replace(".", "")
                            .replace(",", ".");

                    yield Double.parseDouble(texto);

                } catch (NumberFormatException ex) {
                    yield null;
                }
            }

            default -> null;
        };
    }

    // ============================
    // RESULTADO
    // ============================

    public record ResultadoExtracao(
            List<Linha> linhasRicieri,
            Map<String, Double> linhasBestPontos
    ) {}

    public record Linha(
            String nome,
            Double valeTransporte,
            Double valeRefeicao,
            Double gratificacao,
            Double comissoes
    ) {}
}
