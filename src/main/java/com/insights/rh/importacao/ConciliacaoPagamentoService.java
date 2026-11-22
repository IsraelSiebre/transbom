
package com.insights.rh.importacao;

import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.repository.FuncionarioImportacaoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConciliacaoPagamentoService {

    private final ImportacaoFolhaService extracaoFolhaService;
    private final ImportacaoPorForaService extracaoPorForaService;

    private final FuncionarioImportacaoRepository repository;

    private static final String CARGO_PADRAO = "Por Fora";

    @Transactional(rollbackFor = IOException.class)
    public List<FuncionarioImportacao> processar(
            MultipartFile arquivoFolha,
            MultipartFile arquivoPorFora
    ) throws IOException {

        // 1. Extrair a folha
        List<FuncionarioImportacao> funcionarios = new ArrayList<>(
                extracaoFolhaService.processar(arquivoFolha)
        );

        if (funcionarios.isEmpty()) {
            throw new IllegalArgumentException(
                    "A folha de pagamento não contém funcionários."
            );
        }

        LocalDate competencia = Objects.requireNonNull(
                funcionarios.get(0).getCompetencia(),
                "Competência da folha não informada."
        );

        // 2. Extrair pagamentos por fora
        ImportacaoPorForaService.ResultadoExtracao resultado =
                extracaoPorForaService.processar(arquivoPorFora);


        // 4. Indexar os registros da folha
        Map<String, FuncionarioImportacao> registrosPorNome =
                new HashMap<>();

        for (FuncionarioImportacao registro : funcionarios) {

            if (!competencia.equals(registro.getCompetencia())) {
                throw new IllegalStateException(
                        "A folha contém competências diferentes."
                );
            }

            String chave = chaveObrigatoria(registro.getNome());

            if (registrosPorNome.containsKey(chave)) {
                throw new IllegalStateException(
                        "Nome duplicado na folha: " + registro.getNome()
                );
            }

            registrosPorNome.put(chave, registro);
        }

        // 5. Conciliar RICIERI
        for (ImportacaoPorForaService.Linha linha :
                resultado.linhasRicieri()) {

            FuncionarioImportacao registro = obterOuCriarRegistro(
                    linha.nome(),
                    competencia,
                    funcionarios,
                    registrosPorNome
            );

            registro.setValeTransporte(
                    linha.valeTransporte()
            );

            registro.setValeRefeicao(
                    linha.valeRefeicao()
            );

            registro.setComissoesComProvisao(
                    linha.gratificacao()
            );

            registro.setComissoesPorFora(
                    linha.comissoes()
            );
        }

        // 6. Conciliar BESTPONTOS
        for (Map.Entry<String, BigDecimal> entrada :
                resultado.linhasBestPontos().entrySet()) {

            FuncionarioImportacao registro = obterOuCriarRegistro(
                    entrada.getKey(),
                    competencia,
                    funcionarios,
                    registrosPorNome
            );

            registro.setValorBestPontos(
                    entrada.getValue()
            );
        }

        // 7. Persistir após a conciliação
        return repository.saveAll(funcionarios);
    }

    /**
     * Retorna um registro da competência.
     * Caso não exista, cria um registro
     */
    private FuncionarioImportacao obterOuCriarRegistro(
            String nome,
            LocalDate competencia,
            List<FuncionarioImportacao> funcionarios,
            Map<String, FuncionarioImportacao> registrosPorNome
    ) {

        String chave = chaveObrigatoria(nome);

        FuncionarioImportacao registro = registrosPorNome.get(chave);

        if (registro != null) {
            return registro;
        }

        // Criar lançamento financeiro da competência
        registro = new FuncionarioImportacao();

        registro.setNome(nome.trim());
        registro.setCargo(CARGO_PADRAO);
        registro.setCompetencia(competencia);

        funcionarios.add(registro);

        // Fundamental para conciliar com a próxima fonte
        registrosPorNome.put(chave, registro);

        return registro;
    }

    private String chaveObrigatoria(String nome) {

        String chave = normalizarNome(nome);

        if (chave.isBlank()) {
            throw new IllegalArgumentException(
                    "Funcionário sem nome válido: " + nome
            );
        }

        return chave;
    }

    private String normalizarNome(String nome) {

        if (nome == null) {
            return "";
        }

        String normalizado = Normalizer.normalize(
                nome,
                Normalizer.Form.NFD
        );

        return normalizado
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
