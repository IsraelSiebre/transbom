package com.insights.rh.extracao;

import com.insights.rh.repository.FuncionarioImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConciliacaoPagamentoService {

    private final ExtracaoFolhaService extracaoFolhaService;
    private final ExtracaoPorForaService extracaoPorForaService;
    private final FuncionarioImportacaoRepository repository;

    @Transactional(rollbackFor = IOException.class)
    public List<FuncionarioImportacao> processar(
            MultipartFile arquivoFolha,
            MultipartFile arquivoPorFora
    ) throws IOException {

        // 1. Extrair folha de pagamento
        List<FuncionarioImportacao> funcionarios =
                extracaoFolhaService.processar(arquivoFolha);

        // 2. Extrair pagamentos por fora
        ExtracaoPorForaService.ResultadoExtracao resultado =
                extracaoPorForaService.processar(arquivoPorFora);

        // 3. Indexar funcionários pelo nome normalizado
        Map<String, FuncionarioImportacao> funcionariosPorNome =
                new HashMap<>();

        for (FuncionarioImportacao funcionario : funcionarios) {

            String chave = normalizarNome(funcionario.getNome());

            if (chave.isBlank()) {
                throw new IllegalArgumentException(
                        "Funcionário da folha sem nome válido."
                );
            }

            // Não permite vincular nomes ambíguos.
            FuncionarioImportacao anterior =
                    funcionariosPorNome.putIfAbsent(chave, funcionario);

            if (anterior != null) {
                throw new IllegalStateException(
                        "Nome duplicado na folha: "
                                + funcionario.getNome()
                                + ". Não é seguro conciliar somente pelo nome."
                );
            }
        }

        // 4. Vincular dados da Ricieri

        for (ExtracaoPorForaService.Linha linha :
                resultado.linhasRicieri()) {

            String chave = normalizarNome(linha.nome());

            FuncionarioImportacao funcionario = funcionariosPorNome.get(chave);

            if (funcionario == null) {
                FuncionarioImportacao funcionarioImportacao = new FuncionarioImportacao();
                funcionarioImportacao.setNome(linha.nome());
                funcionarioImportacao.setValeTransportePorFora(converter(linha.valeTransporte()));
                funcionarioImportacao.setValeRefeicaoPorFora(converter(linha.valeRefeicao()));
                funcionarioImportacao.setGratificacaoPorFora(converter(linha.gratificacao()));
                funcionarioImportacao.setComissoesPorFora(converter(linha.comissoes()));
                funcionarios.add(funcionarioImportacao);
                continue;
            }

            funcionario.setValeTransportePorFora(
                    converter(linha.valeTransporte())
            );

            funcionario.setValeRefeicaoPorFora(
                    converter(linha.valeRefeicao())
            );

            funcionario.setGratificacaoPorFora(
                    converter(linha.gratificacao())
            );

            funcionario.setComissoesPorFora(
                    converter(linha.comissoes())
            );
        }

        // 5. Vincular dados da BESTPONTOS
        for (Map.Entry<String, Double> entrada :
                resultado.linhasBestPontos().entrySet()) {

            String chave = normalizarNome(entrada.getKey());

            FuncionarioImportacao funcionario =
                    funcionariosPorNome.get(chave);

            if (funcionario == null) {
                FuncionarioImportacao funcionarioImportacao = new FuncionarioImportacao();
                funcionarioImportacao.setNome(entrada.getKey());
                funcionarioImportacao.setValorBestPontos(converter(entrada.getValue()));
                funcionarios.add(funcionarioImportacao);
                continue;
            }

            funcionario.setValorBestPontos(
                    converter(entrada.getValue())
            );
        }

        // 6. Salvar somente após a conciliação
        return repository.saveAll(funcionarios);
    }

    private BigDecimal converter(Double valor) {
        return valor == null ? null : BigDecimal.valueOf(valor);
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
                .toUpperCase(java.util.Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}