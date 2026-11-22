package com.insights.rh;

import com.insights.rh.importacao.ConciliacaoPagamentoService;
import com.insights.rh.importacao.dadosantigos.ImportacaoFolhaExcelService;
import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.FuncionarioImportacao;
import com.insights.rh.insights.CalculoService;
import com.insights.rh.insights.model.CalculoTotal;
import com.insights.rh.insights.model.FuncionarioCalculo;
import com.insights.rh.repository.FuncionarioImportacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@RequestMapping("/mao-de-obra")
public class Controller {

    private final ConciliacaoPagamentoService conciliacaoService;
    private final CalculoService calculoService;
    private final FuncionarioImportacaoRepository importacaoRepository;
    private final ImportacaoFolhaExcelService importacaoFolhaExcelService;

    @GetMapping("/upload")
    public String index() {
        return "rh/upload";
    }

    @PostMapping("/extracao/processar")
    public ResponseEntity<List<FuncionarioImportacao>> processar(
            @RequestParam("folha") MultipartFile folha,
            @RequestParam("porFora") MultipartFile porFora) throws IOException {

        if (folha.isEmpty() || porFora.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<FuncionarioImportacao> funcionarios = conciliacaoService.processar(folha, porFora);
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate competencia,
            Model model) {

        LocalDate mes = normalizarCompetencia(competencia);
        List<FuncionarioCalculo> todosCalculos = calculoService.calcularListaMOFuncionarios(mes);
        List<FuncionarioCalculo> calculos = todosCalculos.stream()
                .filter(f -> f.centroDeCusto() != null)
                .toList();

        long qntFuncionariosSemCC = todosCalculos.size() - calculos.size();
        CalculoTotal total = calculoService.calcularTotal(calculos);
        Map<CentroDeCusto, CalculoTotal> totalPorCentroCusto =
                calculoService.calcularTotalPorCentroDeCusto(calculos);

        model.addAttribute("calculos", calculos);
        model.addAttribute("competencia", mes);
        model.addAttribute("total", total);
        model.addAttribute("totalPorCentroCusto", totalPorCentroCusto);
        model.addAttribute("qntFuncionariosSemCC", qntFuncionariosSemCC);
        return "rh/dashboard";
    }

    // O centro de custo agora pertence ao registro importado, não a Funcionario.
    @GetMapping("/funcionarios/sem-centro-custo")
    public String funcionariosSemCc(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate competencia,
            Model model) {

        LocalDate mes = normalizarCompetencia(competencia);
        List<FuncionarioImportacao> funcionariosSemCC = importacaoRepository
                .findAllByCompetencia(mes)
                .stream()
                .filter(f -> f.getCentroDeCusto() == null)
                .toList();

        model.addAttribute("funcionariosSemCC", funcionariosSemCC);
        model.addAttribute("centrosDeCustoExistentes", CentroDeCusto.values());
        model.addAttribute("competencia", mes);
        return "rh/funcionarios-sem-cc";
    }

    @PostMapping("/funcionarios/sem-centro-custo")
    public String salvarCentrosDeCusto(
            @RequestParam("funcionarioIds") List<Long> funcionarioIds,
            @RequestParam("centrosDeCusto") List<CentroDeCusto> centrosDeCusto,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate competencia,
            RedirectAttributes redirectAttributes) {

        String destino = "redirect:/mao-de-obra/funcionarios/sem-centro-custo";
        if (competencia != null) {
            redirectAttributes.addAttribute("competencia", competencia.withDayOfMonth(1).toString());
        }

        if (funcionarioIds.size() != centrosDeCusto.size()) {
            redirectAttributes.addFlashAttribute("erro", "Os dados enviados são inconsistentes.");
            return destino;
        }

        Map<Long, FuncionarioImportacao> porId = new HashMap<>();
        importacaoRepository.findAllById(funcionarioIds)
                .forEach(f -> porId.put(f.getId(), f));

        Set<Long> idsProcessados = new HashSet<>();
        List<FuncionarioImportacao> paraSalvar = new ArrayList<>();
        for (int i = 0; i < funcionarioIds.size(); i++) {
            Long id = funcionarioIds.get(i);
            CentroDeCusto novoCC = centrosDeCusto.get(i);
            if (novoCC == null || !idsProcessados.add(id)) {
                continue;
            }

            FuncionarioImportacao funcionario = porId.get(id);
            if (funcionario == null) {
                continue;
            }
            if (funcionario.getCentroDeCusto() != null) {
                continue;
            }
            if (competencia != null
                    && !funcionario.getCompetencia().withDayOfMonth(1)
                    .equals(competencia.withDayOfMonth(1))) {
                continue;
            }

            funcionario.setCentroDeCusto(novoCC);
            paraSalvar.add(funcionario);
        }

        // O @PreUpdate de FuncionarioImportacao executa recalcular() ao salvar.
        importacaoRepository.saveAll(paraSalvar);
        redirectAttributes.addFlashAttribute("sucesso",
                paraSalvar.size() + " funcionário(s) atualizado(s) com sucesso!");
        return destino;
    }

    @GetMapping("/detalhamento")
    public String detalhamento(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate competencia,
            Model model) {

        LocalDate mes = normalizarCompetencia(competencia);
        model.addAttribute("competencia", mes);
        model.addAttribute("calculos", calculoService.calcularListaMOFuncionarios(mes));
        return "rh/detalhamento";
    }

    @PostMapping("/folha")
    public ResponseEntity<Void> importar(@RequestParam("arquivo") MultipartFile arquivo)
            throws IOException {
        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        importacaoFolhaExcelService.importar(arquivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/folha", produces = MediaType.TEXT_HTML_VALUE)
    public String paginaImportacao() {
        return "rh/aaa";
    }

    private LocalDate normalizarCompetencia(LocalDate competencia) {
        return (competencia == null ? LocalDate.now() : competencia).withDayOfMonth(1);
    }
}
