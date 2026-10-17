package com.insights.rh.extracao;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Service
public class ExtracaoService {

    public List<LinhaMaoDeObra> processar(MultipartFile arquivo) throws IOException {
        List<Map<String, Object>> df = lerExcel(arquivo);

        // df.drop(columns=[...])
        List<String> colunasRemover = List.of(
                "codi_emp", "nome_emp", "cgce_emp", "tins_emp",
                "cp_tipo_calc", "cp_competencia", "cp_data_hora",
                "cp_quebra", "cp_pagina_ini", "cp_label", "cp_codi_epr"
        );

        for (String coluna : colunasRemover) {
            if (!df.isEmpty() && !df.get(0).containsKey(coluna)) {
                throw new IllegalArgumentException("Coluna não encontrada: " + coluna);
            }
        }
        for (Map<String, Object> linha : df) {
            for (String coluna : colunasRemover) {
                linha.remove(coluna);
            }
        }

        // df.groupby("cp_nome_epr"): grupos ordenados por nome.
        Map<String, List<Map<String, Object>>> listaDF = new TreeMap<>();
        for (Map<String, Object> linha : df) {
            Object nome = linha.get("cp_nome_epr");
            if (nome == null || ehNaN(nome)) {
                continue;
            }
            listaDF.computeIfAbsent(texto(nome), chave -> new ArrayList<>()).add(linha);
        }

        List<LinhaMaoDeObra> listadict = new ArrayList<>();

        for (List<Map<String, Object>> dfl : listaDF.values()) {
            String name = texto(dfl.get(0).get("cp_nome_epr"));
            String dataP = name + " CPF:" + texto(dfl.get(0).get("cp_cpf"))
                    + " CTPS:xx PIS/PASEP:xx";

            listadict.add(new LinhaMaoDeObra(name, name, dataP, 0));

            // Ganhos
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_nome_eve_p");
                if (!texto(row).equals("nan")) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_nome_eve_p", row);
                    Object cod = primeira.get("cp_codi_eve_p");
                    String descricao = texto(cod) + " - "
                            + texto(primeira.get("cp_nome_eve_p")) + " "
                            + texto(primeira.get("cp_eve_inf_p")) + " "
                            + texto(primeira.get("cp_eve_val_p"));
                    listadict.add(new LinhaMaoDeObra(
                            name, cod, descricao, primeira.get("cp_eve_val_p")));
                }
            }

            // Descontos
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_nome_eve_d");
                if (!texto(row).equals("nan")) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_nome_eve_d", row);
                    Object cod = primeira.get("cp_codi_eve_d");
                    String descricao = texto(cod) + " - "
                            + texto(primeira.get("cp_nome_eve_d")) + " "
                            + texto(primeira.get("cp_eve_inf_d")) + " "
                            + texto(primeira.get("cp_eve_val_d"));
                    listadict.add(new LinhaMaoDeObra(
                            name, cod, descricao, primeira.get("cp_eve_val_d")));
                }
            }

            // Base INSS
            Object baseINSS = 0;
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_bas_inss");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_bas_inss", row);
                    Object valor = primeira.get("cp_bas_inss");
                    listadict.add(new LinhaMaoDeObra(
                            name, "BASE INSS", "BASE INSS - " + texto(valor), valor));
                    baseINSS = texto(valor).replace(",", ".");
                }
            }

            // Base IRRF (grafia IRFF preservada do Python)
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_bas_irrf");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_bas_irrf", row);
                    Object valor = primeira.get("cp_bas_irrf");
                    listadict.add(new LinhaMaoDeObra(
                            name, "BASE IRFF", "BASE IRFF - " + texto(valor), valor));
                }
            }

            // Base FGTS
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_bas_fgts");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_bas_fgts", row);
                    Object valor = primeira.get("cp_bas_fgts");
                    listadict.add(new LinhaMaoDeObra(
                            name, "BASE FGTS", "BASE FGTS - " + texto(valor), valor));
                }
            }

            // FGTS a recolher
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_val_fgts");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_val_fgts", row);
                    Object valor = primeira.get("cp_val_fgts");
                    listadict.add(new LinhaMaoDeObra(
                            name, "FGTS A RECOLHER", "FGTS A RECOLHER - " + texto(valor), valor));
                }
            }

            // RAT - Seguro acidente de trabalho
            double rat = (Double.parseDouble(texto(baseINSS)) * 3.88) / 100;
            listadict.add(new LinhaMaoDeObra(
                    name, "SEG.ACIDENTE TRABALHO",
                    "SEG.ACIDENTE TRABALHO 3,88% " + formatarFloat(rat),
                    formatarFloat(rat)));

            // INSS terceiros - Outras entidades
            double insst = (Double.parseDouble(texto(baseINSS)) * 5.8) / 100;
            listadict.add(new LinhaMaoDeObra(
                    name, "VALOR OUTRAS ENTIDADES",
                    "VALOR OUTRAS ENTIDADES 5,80% " + formatarFloat(insst),
                    formatarFloat(insst)));

            // Total proventos
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_tot_prov");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_tot_prov", row);
                    Object valor = primeira.get("cp_tot_prov");
                    listadict.add(new LinhaMaoDeObra(
                            name, "TOTAL DE VENCIMENTOS",
                            "TOTAL DE VENCIMENTOS (+): " + texto(valor), valor));
                }
            }

            // Total descontos
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_tot_desc");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_tot_desc", row);
                    Object valor = primeira.get("cp_tot_desc");
                    listadict.add(new LinhaMaoDeObra(
                            name, "TOTAL DE DESCONTOS",
                            "TOTAL DE DESCONTOS (-): " + texto(valor), valor));
                }
            }

            // Total líquido
            for (Map<String, Object> linha : dfl) {
                Object row = linha.get("cp_tot_liq");
                if (!iguais(row, 0)) {
                    Map<String, Object> primeira = primeiraOcorrencia(dfl, "cp_tot_liq", row);
                    Object valor = primeira.get("cp_tot_liq");
                    listadict.add(new LinhaMaoDeObra(
                            name, "TOTAL LIQUIDO",
                            "TOTAL LIQUIDO (=): " + texto(valor), valor));
                }
            }
        }

        return listadict;
    }

    private String formatarFloat(double valor) {
        // float formatado com .2f no Python (arredondamento HALF_EVEN)
        if (Double.isNaN(valor)) return "nan";
        if (Double.isInfinite(valor)) return valor < 0 ? "-inf" : "inf";
        return new BigDecimal(valor)
                .setScale(2, RoundingMode.HALF_EVEN)
                .toPlainString()
                .replace('.', ',');
    }

    private Map<String, Object> primeiraOcorrencia(
            List<Map<String, Object>> linhas, String coluna, Object valor) {
        for (Map<String, Object> linha : linhas) {
            if (iguais(linha.get(coluna), valor)) {
                return linha;
            }
        }
        throw new IndexOutOfBoundsException("Nenhuma ocorrência encontrada");
    }

    private boolean iguais(Object a, Object b) {
        if (a instanceof Number na && b instanceof Number nb) {
            return na.doubleValue() == nb.doubleValue();
        }
        return Objects.equals(a, b);
    }

    private boolean ehNaN(Object valor) {
        return valor instanceof Double numero && numero.isNaN();
    }

    private String texto(Object valor) {
        if (valor == null || ehNaN(valor)) return "nan";
        return String.valueOf(valor);
    }

    private List<Map<String, Object>> lerExcel(MultipartFile arquivo) throws IOException {
        List<Map<String, Object>> registros = new ArrayList<>();
        try (InputStream entrada = arquivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(entrada)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row cabecalho = sheet.getRow(0);
            List<String> colunas = new ArrayList<>();
            for (Cell celula : cabecalho) {
                colunas.add(celula.getStringCellValue());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, Object> registro = new LinkedHashMap<>();
                for (int j = 0; j < colunas.size(); j++) {
                    registro.put(colunas.get(j), lerValorCelula(row.getCell(j)));
                }
                registros.add(registro);
            }
        }
        return registros;
    }

    private Object lerValorCelula(Cell celula) {
        if (celula == null) return Double.NaN;
        CellType tipo = celula.getCellType();
        if (tipo == CellType.FORMULA) {
            tipo = celula.getCachedFormulaResultType();
        }
        return switch (tipo) {
            case NUMERIC -> celula.getNumericCellValue();
            case STRING -> {
                String valor = celula.getStringCellValue();
                yield valor.isEmpty() ? Double.NaN : valor;
            }
            case BOOLEAN -> celula.getBooleanCellValue();
            case BLANK, ERROR -> Double.NaN;
            default -> Double.NaN;
        };
    }
}

