package com.insights.rh.importacao.dadosantigos;

import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.Funcionario;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import com.insights.rh.repository.FuncionarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;

@Service
public class ImportacaoFolhaExcelService {

    private final FuncionarioRepository funcionarios;
    private final FuncionarioImportacaoRepository folhas;
    private static final DataFormatter FORMATTER = new DataFormatter(new Locale("pt", "BR"));

    public ImportacaoFolhaExcelService(FuncionarioRepository funcionarios,
                                       FuncionarioImportacaoRepository folhas) {
        this.funcionarios = funcionarios;
        this.folhas = folhas;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importar(MultipartFile arquivo) throws IOException {
        int contador = 0;

        try (Workbook workbook = WorkbookFactory.create(arquivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row cabecalho = sheet.getRow(sheet.getFirstRowNum());

            if (cabecalho == null) throw new IllegalArgumentException("Planilha sem cabeçalho");


            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if(row == null) {break;}


                FuncionarioImportacao funcionarioImportacao = new FuncionarioImportacao();
                funcionarioImportacao.setCompetencia(LocalDate.of((int) row.getCell(2).getNumericCellValue(), (int) row.getCell(1).getNumericCellValue(), 1));
                funcionarioImportacao.setValorPorCodigo("8781", toBigDecimal(row.getCell(11).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9380", toBigDecimal(row.getCell(12).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("981", toBigDecimal(row.getCell(13).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("995", toBigDecimal(row.getCell(14).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("285", toBigDecimal(row.getCell(15).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("LICENCA REMUNERADA", toBigDecimal(row.getCell(16).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("873", toBigDecimal(row.getCell(17).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("150", toBigDecimal(row.getCell(18).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("264", toBigDecimal(row.getCell(19).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("200", toBigDecimal(row.getCell(20).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("257", toBigDecimal(row.getCell(21).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("226", toBigDecimal(row.getCell(22).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("214", toBigDecimal(row.getCell(23).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("255", toBigDecimal(row.getCell(24).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("256", toBigDecimal(row.getCell(25).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("16", toBigDecimal(row.getCell(26).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("260", toBigDecimal(row.getCell(27).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("3", toBigDecimal(row.getCell(28).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Comissões COM Provisão", toBigDecimal(row.getCell(29).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Comissões SEM Provisão", toBigDecimal(row.getCell(30).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("CESTA BÁSICA", toBigDecimal(row.getCell(31).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Provisão de Férias", toBigDecimal(row.getCell(32).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("805", toBigDecimal(row.getCell(33).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Provisão de 13º", toBigDecimal(row.getCell(34).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Vale Transporte", toBigDecimal(row.getCell(35).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Vale Refeição", toBigDecimal(row.getCell(36).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Outros Encargos Sociais e Trab", toBigDecimal(row.getCell(37).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9591", toBigDecimal(row.getCell(38).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("250", toBigDecimal(row.getCell(39).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("854", toBigDecimal(row.getCell(40).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("D.S.R. S/ ADICIONAL NOTURNO 20%", toBigDecimal(row.getCell(41).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("D.S.R. S/ HORA EXTRA NOTURNA 50%", toBigDecimal(row.getCell(42).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("262", toBigDecimal(row.getCell(43).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("808", toBigDecimal(row.getCell(44).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("932", toBigDecimal(row.getCell(45).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8800", toBigDecimal(row.getCell(46).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9180", toBigDecimal(row.getCell(47).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("64", toBigDecimal(row.getCell(48).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("R-FERIAS S/ AVISO PREVIO", toBigDecimal(row.getCell(49).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("51", toBigDecimal(row.getCell(50).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("29", toBigDecimal(row.getCell(51).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8552", toBigDecimal(row.getCell(52).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8550", toBigDecimal(row.getCell(53).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("FGTS A RECOLHER", toBigDecimal(row.getCell(54).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8783", toBigDecimal(row.getCell(55).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("931", toBigDecimal(row.getCell(56).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("28", toBigDecimal(row.getCell(57).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("208", toBigDecimal(row.getCell(58).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("170", toBigDecimal(row.getCell(59).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("258", toBigDecimal(row.getCell(60).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("259", toBigDecimal(row.getCell(61).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("277", toBigDecimal(row.getCell(62).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("282", toBigDecimal(row.getCell(63).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("271", toBigDecimal(row.getCell(64).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("275", toBigDecimal(row.getCell(65).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("205", toBigDecimal(row.getCell(66).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8069", toBigDecimal(row.getCell(67).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("280", toBigDecimal(row.getCell(68).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("265", toBigDecimal(row.getCell(69).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("267", toBigDecimal(row.getCell(70).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("242", toBigDecimal(row.getCell(71).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("998", toBigDecimal(row.getCell(72).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("812", toBigDecimal(row.getCell(73).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8785", toBigDecimal(row.getCell(74).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("989", toBigDecimal(row.getCell(75).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("96", toBigDecimal(row.getCell(76).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("942", toBigDecimal(row.getCell(77).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("DESCONTOS TOTAIS", toBigDecimal(row.getCell(78).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("Custo M.O Total", toBigDecimal(row.getCell(79).getNumericCellValue()));

                funcionarioImportacao.setValorPorCodigo("900", toBigDecimal(row.getCell(83).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9162", toBigDecimal(row.getCell(84).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("937", toBigDecimal(row.getCell(85).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("225", toBigDecimal(row.getCell(86).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9181", toBigDecimal(row.getCell(87).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("8169", toBigDecimal(row.getCell(88).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("149", toBigDecimal(row.getCell(89).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("261", toBigDecimal(row.getCell(90).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("264", toBigDecimal(row.getCell(91).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("89", toBigDecimal(row.getCell(92).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9988", toBigDecimal(row.getCell(93).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("9522", toBigDecimal(row.getCell(94).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("146", toBigDecimal(row.getCell(95).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("SEG.ACIDENTE TRABALHO", toBigDecimal(row.getCell(96).getNumericCellValue()));
                funcionarioImportacao.setValorPorCodigo("VALOR OUTRAS ENTIDADES", toBigDecimal(row.getCell(97).getNumericCellValue()));

                Optional<Funcionario> funcionarioOptional = funcionarios.findByNome(row.getCell(6).getStringCellValue());

                Funcionario funcionario;

                if (!funcionarioOptional.isPresent()) {
                    funcionario = new Funcionario();
                    funcionario.setNome(row.getCell(6).getStringCellValue());
                    funcionario.setCodigoExterno((int) row.getCell(4).getNumericCellValue());
                    funcionario.setEmpresa(row.getCell(3).getStringCellValue());
                    funcionario.setCargo(row.getCell(7).getStringCellValue());



                    funcionario.setDataAdmissao(row.getCell(8).getLocalDateTimeCellValue().toLocalDate());

                    funcionario.setCentroDeCusto(row.getCell(5).getStringCellValue());

                    funcionarios.save(funcionario);
                } else {
                    funcionario = funcionarioOptional.get();
                }
                funcionarioImportacao.setFuncionario(funcionario);
                folhas.save(funcionarioImportacao);
                contador++;
                System.out.println(contador +" - "+ funcionario.getCentroDeCusto());
            }

        }
    }

    private BigDecimal toBigDecimal(Double valor) {
        return BigDecimal.valueOf(valor);
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", " ").trim().replaceAll("\\s+", " ");
    }


    private String texto(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell).trim();
    }
}
