package com.insights.rh.importacao.dadosantigos;

import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportacaoFolhaExcelService {

    private static final int TAMANHO_LOTE = 500;
    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("d/M/uuuu");
    private static final DateTimeFormatter DATA_BR_HIFEN = DateTimeFormatter.ofPattern("d-M-uuuu");

    private final FuncionarioImportacaoRepository repository;

    // Índices zero-based da planilha antiga. Os textos abaixo são
    // EXATAMENTE os códigos/aliases aceitos por setValorPorCodigo().
    // Não estão aqui os campos que a entidade calcula via @PrePersist/@PreUpdate.
    private static final Map<Integer, String> RUBRICAS_POR_COLUNA = Map.ofEntries(
            Map.entry(11, "8781"),
            Map.entry(12, "9380"),
            Map.entry(13, "981"),
            Map.entry(14, "995"),
            Map.entry(15, "285"),
            Map.entry(16, "LICENCA REMUNERADA"),
            Map.entry(17, "873"),
            Map.entry(18, "150"),
            Map.entry(19, "HORASEXTRASNOTURNAS100"),
            Map.entry(20, "200"),
            Map.entry(21, "257"),
            Map.entry(22, "226"),
            Map.entry(23, "214"),
            Map.entry(24, "255"),
            Map.entry(25, "256"),
            Map.entry(26, "16"),
            Map.entry(27, "260"),
            Map.entry(28, "3"),
            Map.entry(29, "COMISSOESCOMPROVISAO"),
            Map.entry(31, "CESTABASICA"),
            Map.entry(33, "805"),
            Map.entry(35, "VALETRANSPORTE"),
            Map.entry(36, "VALEREFEICAO"),
            Map.entry(38, "9591"),
            Map.entry(39, "250"),
            Map.entry(40, "854"),
            Map.entry(41, "D.S.R. S/ ADICIONAL NOTURNO 20%"),
            Map.entry(42, "D.S.R. S/ HORA EXTRA NOTURNA 50%"),
            Map.entry(43, "262"),
            Map.entry(44, "808"),
            Map.entry(45, "932"),
            Map.entry(46, "8800"),
            Map.entry(47, "9180"),
            Map.entry(48, "64"),
            Map.entry(49, "R-FERIAS S/ AVISO PREVIO"),
            Map.entry(50, "51"),
            Map.entry(51, "29"),
            Map.entry(52, "8552"),
            Map.entry(53, "8550"),
            Map.entry(54, "FGTS A RECOLHER"),
            Map.entry(55, "8783"),
            Map.entry(56, "931"),
            Map.entry(57, "28"),
            Map.entry(60, "258"),
            Map.entry(61, "259"),
            Map.entry(62, "277"),
            Map.entry(63, "282"),
            Map.entry(64, "271"),
            Map.entry(65, "275"),
            Map.entry(66, "205"),
            Map.entry(67, "8069"),
            Map.entry(68, "280"),
            Map.entry(69, "265"),
            Map.entry(70, "267"),
            Map.entry(71, "242"),
            Map.entry(72, "998"),
            Map.entry(73, "812"),
            Map.entry(74, "8785"),
            Map.entry(75, "989"),
            Map.entry(76, "96"),
            Map.entry(77, "942"),
            Map.entry(83, "900"),
            Map.entry(84, "9162"),
            Map.entry(85, "937"),
            Map.entry(86, "225"),
            Map.entry(87, "9181"),
            Map.entry(88, "8169"),
            Map.entry(89, "149"),
            Map.entry(90, "261"),
            Map.entry(91, "HORAEXTRANOTURNA100"),
            Map.entry(92, "89"),
            Map.entry(93, "9988"),
            Map.entry(94, "9522"),
            Map.entry(95, "146"),
            Map.entry(96, "SEG.ACIDENTE TRABALHO"),
            Map.entry(97, "VALOR OUTRAS ENTIDADES")
    );

    @Transactional(rollbackFor = Exception.class)
    public void importar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma planilha Excel não vazia.");
        }

        try (Workbook workbook = WorkbookFactory.create(arquivo.getInputStream())) {
            Sheet planilha = workbook.getSheetAt(0);
            int primeiraLinha = planilha.getFirstRowNum();
            if (planilha.getRow(primeiraLinha) == null) {
                throw new IllegalArgumentException("A planilha não possui cabeçalho.");
            }

            FormulaEvaluator avaliador = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatador = new DataFormatter(Locale.forLanguageTag("pt-BR"));
            List<FuncionarioImportacao> lote = new ArrayList<>(TAMANHO_LOTE);

            for (int i = primeiraLinha + 1; i <= planilha.getLastRowNum(); i++) {
                Row linha = planilha.getRow(i);
                if (linha == null || linha.getPhysicalNumberOfCells() == 0) {
                    continue;
                }

                try {
                    String nome = lerTexto(linha, 6, formatador, avaliador);
                    if (nome == null) {
                        throw new IllegalArgumentException("Nome do funcionário vazio (coluna 7).");
                    }

                    FuncionarioImportacao funcionario = new FuncionarioImportacao();
                    int mes = lerInteiroObrigatorio(linha, 1, avaliador);
                    int ano = lerInteiroObrigatorio(linha, 2, avaliador);

                    funcionario.setCompetencia(LocalDate.of(ano, mes, 1));
                    funcionario.setCodigoExterno(lerInteiroObrigatorio(linha, 4, avaliador));
                    funcionario.setNome(nome);
                    funcionario.setCargo(lerTexto(linha, 7, formatador, avaliador));
                    funcionario.setDataAdmissao(lerData(linha, 8, formatador, avaliador));

                    String cc = lerTexto(linha, 5, formatador, avaliador);
                    funcionario.setCentroDeCusto(cc == null
                            ? null
                            : CentroDeCusto.fromString(cc.toUpperCase(Locale.ROOT)));

                    for (Map.Entry<Integer, String> rubrica : RUBRICAS_POR_COLUNA.entrySet()) {
                        BigDecimal valor = lerDecimal(linha, rubrica.getKey(), avaliador);
                        if (valor != null) {
                            funcionario.setValorPorCodigo(rubrica.getValue(), valor);
                        }
                    }

                    // A entidade sobrescreve comissoesSemProvisao no recalcular().
                    // Por isso a coluna 30 deve preencher o campo de ORIGEM.
                    BigDecimal comissoesSemProvisao = lerDecimal(linha, 30, avaliador);
                    if (comissoesSemProvisao != null) {
                        // Na planilha antiga existe apenas o valor consolidado.
                        // Preenche ambas as origens para o cálculo funcionar também
                        // se o centro de custo for atribuído posteriormente.
                        funcionario.setComissoesPorFora(comissoesSemProvisao);
                        funcionario.setValorBestPontos(comissoesSemProvisao);
                    }

                    // Colunas 32, 34, 37, 58, 59, 78 e 79 não são importadas:
                    // provisões, encargos, adicionais de saúde e totais são calculados
                    // pela entidade (recalcular) antes de persistir.
                    // Colunas 80 a 82 não eram usadas no serviço antigo.

                    lote.add(funcionario);
                    if (lote.size() == TAMANHO_LOTE) {
                        repository.saveAll(lote); // dispara @PrePersist e recalcular()
                        lote.clear();
                    }
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Erro na linha " + (i + 1) + " do Excel: " + e.getMessage(), e);
                }
            }

            if (!lote.isEmpty()) {
                repository.saveAll(lote);
            }
        }
    }

    private static String lerTexto(Row linha, int coluna,
                                   DataFormatter formatador, FormulaEvaluator avaliador) {
        Cell celula = linha.getCell(coluna, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (celula == null) return null;
        String texto = formatador.formatCellValue(celula, avaliador).trim();
        return texto.isEmpty() ? null : texto;
    }

    private static int lerInteiroObrigatorio(Row linha, int coluna,
                                             FormulaEvaluator avaliador) {
        BigDecimal valor = lerDecimal(linha, coluna, avaliador);
        if (valor == null) {
            throw new IllegalArgumentException("Valor obrigatório ausente na coluna " + (coluna + 1));
        }
        try {
            return valor.intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Valor inteiro inválido na coluna " + (coluna + 1), e);
        }
    }

    private static BigDecimal lerDecimal(Row linha, int coluna, FormulaEvaluator avaliador) {
        Cell celula = linha.getCell(coluna, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (celula == null) return null;

        CellType tipo = celula.getCellType();
        if (tipo == CellType.FORMULA) {
            CellValue resultado = avaliador.evaluate(celula);
            if (resultado == null) return null;
            return switch (resultado.getCellType()) {
                case NUMERIC -> moeda(resultado.getNumberValue());
                case STRING -> converterNumero(resultado.getStringValue());
                case BLANK -> null;
                default -> throw new IllegalArgumentException(
                        "Fórmula inválida na coluna " + (coluna + 1));
            };
        }
        return switch (tipo) {
            case NUMERIC -> moeda(celula.getNumericCellValue());
            case STRING -> converterNumero(celula.getStringCellValue());
            case BLANK -> null;
            default -> throw new IllegalArgumentException(
                    "Valor numérico inválido na coluna " + (coluna + 1));
        };
    }

    private static BigDecimal moeda(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal converterNumero(String texto) {
        if (texto == null || texto.isBlank() || texto.trim().equals("-")) return null;
        String numero = texto.replace("R$", "").replace("\u00A0", "").replaceAll("\\s", "").trim();
        boolean negativo = numero.startsWith("(") && numero.endsWith(")");
        if (negativo) numero = numero.substring(1, numero.length() - 1);
        if (numero.contains(",") && numero.contains(".")) {
            numero = numero.lastIndexOf(',') > numero.lastIndexOf('.')
                    ? numero.replace(".", "").replace(',', '.')
                    : numero.replace(",", "");
        } else if (numero.contains(",")) {
            numero = numero.replace(',', '.');
        } else if (numero.matches("-?\\d{1,3}(\\.\\d{3})+")) {
            numero = numero.replace(".", "");
        }
        try {
            BigDecimal valor = new BigDecimal(numero).setScale(2, RoundingMode.HALF_UP);
            return negativo ? valor.negate() : valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido: " + texto, e);
        }
    }

    private static LocalDate lerData(Row linha, int coluna,
                                     DataFormatter formatador, FormulaEvaluator avaliador) {
        Cell celula = linha.getCell(coluna, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (celula == null) return null;
        if (celula.getCellType() == CellType.NUMERIC) {
            return DateUtil.getLocalDateTime(celula.getNumericCellValue()).toLocalDate();
        }
        if (celula.getCellType() == CellType.FORMULA) {
            CellValue resultado = avaliador.evaluate(celula);
            if (resultado != null && resultado.getCellType() == CellType.NUMERIC) {
                return DateUtil.getLocalDateTime(resultado.getNumberValue()).toLocalDate();
            }
        }
        String texto = lerTexto(linha, coluna, formatador, avaliador);
        if (texto == null) return null;
        try {
            return LocalDate.parse(texto, DATA_BR);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(texto, DATA_BR_HIFEN);
            } catch (DateTimeParseException ex) {
                try {
                    return LocalDate.parse(texto);
                } catch (DateTimeParseException ex2) {
                    throw new IllegalArgumentException("Data inválida na coluna "
                            + (coluna + 1) + ": " + texto, ex2);
                }
            }
        }
    }
}
