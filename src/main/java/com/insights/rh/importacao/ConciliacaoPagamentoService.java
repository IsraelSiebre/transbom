
package com.insights.rh.importacao;

import com.insights.rh.importacao.model.Funcionario;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import com.insights.rh.repository.FuncionarioRepository;

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
    private final FuncionarioRepository funcionarioRepository;

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

        // 3. Indexar os cadastros já existentes
        Map<String, List<Funcionario>> cadastroPorNome = new HashMap<>();

        for (Funcionario cadastrado : funcionarioRepository.findAll()) {
            String chave = normalizarNome(cadastrado.getNome());

            if (!chave.isBlank()) {
                cadastroPorNome
                        .computeIfAbsent(chave, k -> new ArrayList<>())
                        .add(cadastrado);
            }
        }

        // 4. Indexar os registros da folha
        Map<String, FuncionarioImportacao> registrosPorNome =
                new HashMap<>();

        for (FuncionarioImportacao registro : funcionarios) {

            if (registro.getFuncionario() == null) {
                throw new IllegalStateException(
                        "Registro da folha sem funcionário vinculado."
                );
            }

            if (!competencia.equals(registro.getCompetencia())) {
                throw new IllegalStateException(
                        "A folha contém competências diferentes."
                );
            }

            Funcionario funcionario = registro.getFuncionario();

            String chave = chaveObrigatoria(funcionario.getNome());

            if (registrosPorNome.containsKey(chave)) {
                throw new IllegalStateException(
                        "Nome duplicado na folha: " + funcionario.getNome()
                );
            }

            // Garantir que o funcionário está cadastrado
            if (funcionario.getId() == null) {

                Funcionario existente = buscarCadastro(
                        chave, cadastroPorNome
                );

                if (existente != null) {
                    registro.setFuncionario(existente);
                } else {
                    Funcionario salvo =
                            funcionarioRepository.save(funcionario);

                    registro.setFuncionario(salvo);

                    cadastroPorNome
                            .computeIfAbsent(chave, k -> new ArrayList<>())
                            .add(salvo);
                }
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
                    registrosPorNome,
                    cadastroPorNome
            );

            registro.setValeTransportePorFora(
                    converter(linha.valeTransporte())
            );

            registro.setValeRefeicaoPorFora(
                    converter(linha.valeRefeicao())
            );

            registro.setGratificacaoPorFora(
                    converter(linha.gratificacao())
            );

            registro.setComissoesPorFora(
                    converter(linha.comissoes())
            );
        }

        // 6. Conciliar BESTPONTOS
        for (Map.Entry<String, Double> entrada :
                resultado.linhasBestPontos().entrySet()) {

            FuncionarioImportacao registro = obterOuCriarRegistro(
                    entrada.getKey(),
                    competencia,
                    funcionarios,
                    registrosPorNome,
                    cadastroPorNome
            );

            registro.setValorBestPontos(
                    converter(entrada.getValue())
            );
        }

        // 7. Persistir após a conciliação
        return repository.saveAll(funcionarios);
    }

    /**
     * Retorna um registro da competência.
     * Caso não exista, cria um registro vinculado
     * ao funcionário cadastrado ou a um novo cadastro.
     */
    private FuncionarioImportacao obterOuCriarRegistro(
            String nome,
            LocalDate competencia,
            List<FuncionarioImportacao> funcionarios,
            Map<String, FuncionarioImportacao> registrosPorNome,
            Map<String, List<Funcionario>> cadastroPorNome
    ) {

        String chave = chaveObrigatoria(nome);

        FuncionarioImportacao registro = registrosPorNome.get(chave);

        if (registro != null) {
            return registro;
        }

        // Procurar funcionário no cadastro
        Funcionario funcionario = buscarCadastro(
                chave, cadastroPorNome
        );

        // Criar somente se não estiver cadastrado
        if (funcionario == null) {

            funcionario = new Funcionario();
            funcionario.setNome(nome.trim());
            funcionario.setCargo(CARGO_PADRAO);

            funcionario = funcionarioRepository.save(funcionario);

            cadastroPorNome
                    .computeIfAbsent(chave, k -> new ArrayList<>())
                    .add(funcionario);
        }

        // Criar lançamento financeiro da competência
        registro = new FuncionarioImportacao();

        registro.setFuncionario(funcionario);
        registro.setCompetencia(competencia);

        funcionarios.add(registro);

        // Fundamental para conciliar com a próxima fonte
        registrosPorNome.put(chave, registro);

        return registro;
    }

    /**
     * Localiza um cadastro pelo nome normalizado.
     * Impede conciliação com nomes ambíguos.
     */
    private Funcionario buscarCadastro(
            String chave,
            Map<String, List<Funcionario>> cadastroPorNome
    ) {

        List<Funcionario> encontrados = cadastroPorNome.get(chave);

        if (encontrados == null || encontrados.isEmpty()) {
            return null;
        }

        if (encontrados.size() > 1) {
            throw new IllegalStateException(
                    "Existem múltiplos funcionários cadastrados " +
                            "com o nome: " + chave +
                            ". A conciliação precisa de um identificador único."
            );
        }

        return encontrados.get(0);
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
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
