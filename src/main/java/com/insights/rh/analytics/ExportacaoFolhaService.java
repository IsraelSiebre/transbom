package com.insights.rh.analytics;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ExportacaoFolhaService {
    private final DetalheFolhaService detalhes;
    private final MaoDeObraAnalyticsService analytics;

    private static final Map<String,String> TITULOS = Map.ofEntries(
            Map.entry("nome","Nome"), Map.entry("competencia","Competência"), Map.entry("cargo","Cargo registrado"),
            Map.entry("centro","Centro de custo"), Map.entry("dataAdmissao","Admissão"), Map.entry("fgts","FGTS"),
            Map.entry("encargos","Encargos"), Map.entry("beneficios","Benefícios"), Map.entry("descontos","Descontos registrados"),
            Map.entry("custo","Custo total"), Map.entry("extras","Horas extras"), Map.entry("provisoes","Provisões"),
            Map.entry("salario","Salários"), Map.entry("comissoes","Comissões"), Map.entry("quantidade","Pessoas distintas"),
            Map.entry("custoMedio","Custo médio por pessoa/competência"), Map.entry("valor","Métrica selecionada"),
            Map.entry("participacao","Participação no custo total (%)"), Map.entry("diferenca","Variação absoluta"),
            Map.entry("variacao","Variação (%)"), Map.entry("salarioMedio","Salário médio"),
            Map.entry("quadroMedio","Quadro médio mensal"), Map.entry("encerramento","Quadro na última competência"),
            Map.entry("mediaMensal","Custo médio mensal"), Map.entry("grupo","Grupo"), Map.entry("natureza","Natureza"),
            Map.entry("codigo","Código da rubrica"), Map.entry("aditiva","Parcela direta do custo"),
            Map.entry("derivada","Campo derivado"), Map.entry("anterior","Valor anterior"),
            Map.entry("mesesSolicitados","Meses selecionados"), Map.entry("mesesImportados","Meses importados"),
            Map.entry("importado","Competência importada"), Map.entry("ultimaCompetencia","Última competência importada"),
            Map.entry("variacaoMensal","Variação mensal do custo (%)"), Map.entry("variacaoAnual","Variação anual do custo (%)"),
            Map.entry("mudancaParticipacao","Mudança de participação (p.p.)"));
    private static final List<String> INDICADORES = List.of("custo","salario","fgts","encargos","beneficios","descontos",
            "extras","comissoes","provisoes","quantidade","custoMedio","salarioMedio","valor");

    static List<String> colunas(String tipo, MultiValueMap<String,String> p) {
        if (tipo.equals("funcionarios")) {
            List<String> colunas = p.get("colunas");
            if (colunas != null && !colunas.isEmpty()) {
                DetalheFolhaService.validarColunas(colunas);
                return List.copyOf(new LinkedHashSet<>(colunas));
            }
            var exportadas = new LinkedHashSet<>(DetalheFolhaService.BASE);
            CatalogoRubricas.todas().forEach(r -> exportadas.add(r.campo()));
            return List.copyOf(exportadas);
        }
        if (tipo.equals("rubricas")) {
            return List.of("codigo","nome","grupo","natureza","aditiva","derivada","valor","anterior","diferenca","participacao");
        }
        List<String> colunas = new ArrayList<>();
        switch (tipo) {
            case "centros", "cargos" -> {
                colunas.add("nome");
                colunas.addAll(List.of("participacao","diferenca","variacao","mudancaParticipacao"));
                if (tipo.equals("centros")) colunas.addAll(List.of("variacaoMensal","variacaoAnual"));
            }
            case "anual" -> colunas.addAll(List.of("id","mesesSolicitados","mesesImportados","ultimaCompetencia","mediaMensal","quadroMedio","encerramento","anterior","diferenca","variacao"));
            case "mensal" -> colunas.addAll(List.of("id","importado","anterior","diferenca","variacao"));
            default -> throw DetalheFolhaService.invalido("Relatório desconhecido.");
        }
        colunas.addAll(INDICADORES);
        return colunas;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public void escrever(String tipo, String formato, MultiValueMap<String,String> filtros, OutputStream saida) throws IOException {
        List<String> campos = colunas(tipo, filtros);
        List<String> cabecalho = campos.stream().map(c -> titulo(c, tipo)).toList();
        if (formato.equals("csv")) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(saida, StandardCharsets.UTF_8));
            writer.write('\uFEFF');
            csv(writer, cabecalho);
            linhas(tipo, filtros, campos, l -> csv(writer,l));
            writer.flush();
        } else if (formato.equals("xlsx")) {
            try (SXSSFWorkbook book = new SXSSFWorkbook(100)) {
                book.setCompressTempFiles(true);
                var planilha = new Planilha(book, cabecalho);
                linhas(tipo, filtros, campos, planilha::adicionar);
                book.write(saida);
            }
        } else {
            throw DetalheFolhaService.invalido("Formato de exportação inválido.");
        }
    }

    private String titulo(String campo, String tipo) {
        if (campo.equals("id")) return tipo.equals("anual") ? "Ano" : "Competência";
        if (campo.equals("nome") && tipo.equals("centros")) return "Centro de custo";
        if (campo.equals("nome") && tipo.equals("cargos")) return "Cargo";
        if (campo.equals("valor") && tipo.equals("rubricas")) return "Valor registrado";
        if (TITULOS.containsKey(campo)) return TITULOS.get(campo);
        return CatalogoRubricas.obter(campo).nome();
    }

    private void linhas(String tipo, MultiValueMap<String,String> p, List<String> campos, Consumer<List<Object>> destino) {
        if (tipo.equals("funcionarios")) {
            detalhes.percorrer(p, campos, destino);
            return;
        }
        Object agregado = analytics.resumo(p).get(tipo);
        if (!(agregado instanceof List<?> linhas)) throw new IllegalStateException("Consulta sem a série solicitada.");
        for (Object registro : linhas) {
            Map<?,?> linha = (Map<?,?>) registro;
            if (tipo.equals("rubricas") && (foraDaSelecao(p.getFirst("grupo"), linha.get("grupo"))
                    || foraDaSelecao(p.getFirst("rubrica"), linha.get("id")))) continue;
            destino.accept(campos.stream().map(c -> valor(c, linha.get(c), tipo)).toList());
        }
    }

    private static boolean foraDaSelecao(String filtro, Object valor) {
        return filtro != null && !filtro.equals(valor);
    }

    private Object valor(String campo, Object v, String tipo) {
        if (campo.equals("id") && tipo.equals("mensal") && v != null) {
            return v.toString().substring(5,7) + "/" + v.toString().substring(0,4);
        }
        return v;
    }

    static void csv(Writer w, List<?> linha) {
        try {
            for (int i = 0; i < linha.size(); i++) {
                if (i > 0) w.write(';');
                Object v = linha.get(i);
                String s = v == null ? "" : v.toString();
                if (v instanceof BigDecimal d) s = d.toPlainString().replace('.', ',');
                if (v instanceof java.sql.Date data) s = data.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (v instanceof CharSequence && (s.startsWith("\t") || s.startsWith("\r") || s.startsWith("\n")
                        || s.stripLeading().matches("(?s)^[=+@-].*"))) s = "'" + s;
                w.write('"');
                w.write(s.replace("\"", "\"\""));
                w.write('"');
            }
            w.write("\r\n");
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static final class Planilha {
        private final SXSSFWorkbook book;
        private final List<String> titulos;
        private final CellStyle monetario;
        private final CellStyle data;
        private Sheet sheet;
        private int indice;

        Planilha(SXSSFWorkbook book, List<String> titulos) {
            this.book = book;
            this.titulos = titulos;
            monetario = book.createCellStyle();
            monetario.setDataFormat(book.createDataFormat().getFormat("#,##0.00"));
            data = book.createCellStyle();
            data.setDataFormat(book.createDataFormat().getFormat("dd/mm/yyyy"));
            abrir();
        }

        private void abrir() {
            sheet = book.createSheet("Folha " + (book.getNumberOfSheets() + 1));
            sheet.createFreezePane(0,1);
            Row cabecalho = sheet.createRow(0);
            for (int i = 0; i < titulos.size(); i++) {
                cabecalho.createCell(i).setCellValue(titulos.get(i));
                sheet.setColumnWidth(i, Math.min(60, Math.max(18,titulos.get(i).length() + 3)) * 256);
            }
            indice = 1;
        }

        void adicionar(List<Object> valores) {
            if (indice >= 1_048_576) abrir();
            Row linha = sheet.createRow(indice++);
            for (int i = 0; i < valores.size(); i++) {
                Object v = valores.get(i);
                if (v == null) continue;
                Cell celula = linha.createCell(i);
                if (v instanceof BigDecimal d && d.stripTrailingZeros().precision() > 15) {
                    celula.setCellValue(d.toPlainString());
                } else if (v instanceof Number n) {
                    celula.setCellValue(n.doubleValue());
                    if (v instanceof BigDecimal) celula.setCellStyle(monetario);
                } else if (v instanceof java.sql.Date dataSql) {
                    celula.setCellValue(dataSql.toLocalDate());
                    celula.setCellStyle(data);
                } else if (v instanceof LocalDate dia) {
                    celula.setCellValue(dia);
                    celula.setCellStyle(data);
                } else if (v instanceof Boolean b) {
                    celula.setCellValue(b);
                } else {
                    celula.setCellValue(v.toString());
                }
            }
        }
    }
}
