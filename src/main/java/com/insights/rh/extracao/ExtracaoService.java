package com.insights.rh.extracao;

import com.insights.rh.repository.FuncionarioImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExtracaoService {

    private static final Logger log = LoggerFactory.getLogger(ExtracaoService.class);
    private static final BigDecimal ALIQUOTA_RAT = new BigDecimal("0.0388");
    private static final BigDecimal ALIQUOTA_TERCEIROS = new BigDecimal("0.058");
    private final FuncionarioImportacaoRepository repository;

    public List<FuncionarioImportacao> processar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione um arquivo Excel não vazio.");
        }

        List<Map<String, Object>> linhas = lerExcel(arquivo);
        Map<String, List<Map<String, Object>>> grupos = new LinkedHashMap<>();

        for (Map<String, Object> linha : linhas) {
            String nome = texto(linha.get("cp_nome_epr"));
            if (nome == null) {
                continue;
            }

            String cpf = texto(linha.get("cp_cpf"));
            String competencia = texto(linha.get("cp_competencia"));
            // O CPF identifica o funcionário; competência separa folhas de meses distintos.
            // Sem CPF, usa-se o nome como alternativa.
            String identidade = cpf != null ? "CPF:" + cpf : "NOME:" + nome;
            String chave = identidade + "|" + (competencia != null ? competencia : "SEM_COMPETENCIA");
            grupos.computeIfAbsent(chave, ignorado -> new ArrayList<>()).add(linha);
        }

        List<FuncionarioImportacao> funcionarios = new ArrayList<>();

        for (List<Map<String, Object>> grupo : grupos.values()) {
            FuncionarioImportacao funcionario = new FuncionarioImportacao();
            Map<String, Object> primeiraLinha = grupo.get(0);
            funcionario.setNome(texto(primeiraLinha.get("cp_nome_epr")));
            funcionario.setCpf(texto(primeiraLinha.get("cp_cpf")));

            Set<String> eventosProcessados = new HashSet<>();
            for (Map<String, Object> linha : grupo) {
                importarRubrica(funcionario, linha, "cp_codi_eve_p", "cp_eve_val_p",
                        "P:", eventosProcessados);
                importarRubrica(funcionario, linha, "cp_codi_eve_d", "cp_eve_val_d",
                        "D:", eventosProcessados);
            }

            BigDecimal baseInss = primeiroValor(grupo, "cp_bas_inss");
            BigDecimal baseIrrf = primeiroValor(grupo, "cp_bas_irrf");
            BigDecimal baseFgts = primeiroValor(grupo, "cp_bas_fgts");
            BigDecimal fgts = primeiroValor(grupo, "cp_val_fgts");
            BigDecimal vencimentos = primeiroValor(grupo, "cp_tot_prov");
            BigDecimal descontos = primeiroValor(grupo, "cp_tot_desc");

            funcionario.setBaseInss(baseInss);
            funcionario.setBaseIrrf(baseIrrf);
            funcionario.setBaseFgts(baseFgts);
            funcionario.setFgtsRecolher(fgts);
            funcionario.setTotalVencimentos(vencimentos);
            funcionario.setTotalDescontos(descontos);

            if (vencimentos != null && descontos != null) {
                funcionario.setTotalLiquido(vencimentos.subtract(descontos));
            }

            // Estimativas: confirme as alíquotas e as exceções da folha de origem.
            // A planilha pode registrar encargos zerados mesmo com base positiva.
            if (baseInss != null) {
                funcionario.setSeguroAcidenteTrabalho(
                        baseInss.multiply(ALIQUOTA_RAT).setScale(2, RoundingMode.HALF_UP));
                funcionario.setValorOutrasEntidades(
                        baseInss.multiply(ALIQUOTA_TERCEIROS).setScale(2, RoundingMode.HALF_UP));
            }

            funcionarios.add(funcionario);
        }

        repository.saveAll(funcionarios);

        return funcionarios;
    }

    private void importarRubrica(
            FuncionarioImportacao funcionario,
            Map<String, Object> linha,
            String colunaCodigo,
            String colunaValor,
            String prefixo,
            Set<String> eventosProcessados
    ) {
        String codigo = normalizarCodigo(linha.get(colunaCodigo));
        BigDecimal valor = converterBigDecimal(linha.get(colunaValor));
        if (codigo == null || valor == null) {
            return;
        }

        // Mantém apenas a primeira ocorrência de cada código em proventos/descontos.
        if (!eventosProcessados.add(prefixo + codigo)) {
            return;
        }

        try {
            funcionario.setValorPorCodigo(codigo, valor);
        } catch (IllegalArgumentException ex) {
            log.warn("Rubrica não mapeada: código={}, funcionário={}",
                    codigo, funcionario.getNome());
        }
    }

    private BigDecimal primeiroValor(List<Map<String, Object>> linhas, String coluna) {
        BigDecimal primeiroZero = null;
        for (Map<String, Object> linha : linhas) {
            BigDecimal valor = converterBigDecimal(linha.get(coluna));
            if (valor == null) {
                continue;
            }
            if (valor.signum() != 0) {
                return valor;
            }
            primeiroZero = valor;
        }
        return primeiroZero;
    }

    private String normalizarCodigo(Object valor) {
        String codigo = texto(valor);
        if (codigo == null) {
            return null;
        }
        return codigo.replaceFirst("\\.0+$", "").toUpperCase(Locale.ROOT);
    }

    private BigDecimal converterBigDecimal(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof Number numero) {
            double valorNumerico = numero.doubleValue();
            return Double.isFinite(valorNumerico) ? BigDecimal.valueOf(valorNumerico) : null;
        }

        String texto = texto(valor);
        if (texto == null) {
            return null;
        }
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        }
        return new BigDecimal(texto);
    }

    private String texto(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof Double numero && !Double.isFinite(numero)) {
            return null;
        }
        String texto = valor.toString().trim();
        return texto.isEmpty() || "nan".equalsIgnoreCase(texto) ? null : texto;
    }

    private List<Map<String, Object>> lerExcel(MultipartFile arquivo) throws IOException {
        List<Map<String, Object>> registros = new ArrayList<>();
        DataFormatter formatador = new DataFormatter(new Locale("pt", "BR"));

        try (InputStream entrada = arquivo.getInputStream();
             Workbook workbook = WorkbookFactory.create(entrada)) {

            Sheet planilha = workbook.getSheetAt(0);
            Row cabecalho = planilha.getRow(0);
            if (cabecalho == null) {
                throw new IllegalArgumentException("A planilha não possui cabeçalho.");
            }

            List<String> colunas = new ArrayList<>();
            for (int j = 0; j < cabecalho.getLastCellNum(); j++) {
                colunas.add(formatador.formatCellValue(cabecalho.getCell(j)).trim());
            }
            if (!colunas.contains("cp_nome_epr")) {
                throw new IllegalArgumentException("Coluna obrigatória ausente: cp_nome_epr");
            }

            for (int i = 1; i <= planilha.getLastRowNum(); i++) {
                Row linha = planilha.getRow(i);
                if (linha == null) {
                    continue;
                }
                Map<String, Object> registro = new LinkedHashMap<>();
                for (int j = 0; j < colunas.size(); j++) {
                    String coluna = colunas.get(j);
                    Cell celula = linha.getCell(j);
                    Object valor = "cp_cpf".equals(coluna)
                            ? formatador.formatCellValue(celula)
                            : lerValorCelula(celula);
                    registro.put(coluna, valor);
                }
                registros.add(registro);
            }
        }
        return registros;
    }

    private Object lerValorCelula(Cell celula) {
        if (celula == null) {
            return null;
        }
        CellType tipo = celula.getCellType();
        if (tipo == CellType.FORMULA) {
            tipo = celula.getCachedFormulaResultType();
        }
        return switch (tipo) {
            case NUMERIC -> celula.getNumericCellValue();
            case STRING -> celula.getStringCellValue();
            case BOOLEAN -> celula.getBooleanCellValue();
            case BLANK, ERROR -> null;
            default -> null;
        };
    }
}
