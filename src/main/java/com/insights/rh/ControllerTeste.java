
package com.insights.rh;

import com.insights.rh.importacao.ConciliacaoPagamentoService;
import com.insights.rh.importacao.model.CentroDeCusto;
import com.insights.rh.importacao.model.Funcionario;
import com.insights.rh.importacao.model.FuncionarioImportacao;

import com.insights.rh.insights.CalculoService;
import com.insights.rh.insights.model.CalculoTotal;
import com.insights.rh.insights.model.FuncionarioCalculo;
import com.insights.rh.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ControllerTeste {

    private final ConciliacaoPagamentoService conciliacaoService;
    private final CalculoService calculoService;
    private final FuncionarioRepository funcionarioRepository;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/extracao/processar")
    @ResponseBody
    public ResponseEntity<List<FuncionarioImportacao>> processar(
            @RequestParam("folha") MultipartFile folha,
            @RequestParam("porFora") MultipartFile porFora
    ) throws IOException {

        if (folha.isEmpty() || porFora.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<FuncionarioImportacao> funcionarios =
                conciliacaoService.processar(folha, porFora);

        System.out.println(
                "Funcionários importados: " + funcionarios.size()
        );

        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) LocalDate competencia,
            Model model
    ) {

        if (competencia == null) {
            competencia = LocalDate.now().withDayOfMonth(1);
        }

        List<FuncionarioCalculo> todosCalculos =
                calculoService.calcularListaMOFuncionarios(competencia);

        List<FuncionarioCalculo> calculos = todosCalculos.stream()
                .filter(f -> f.centroDeCusto() != null)
                .toList();

        long qntFuncionariosSemCC =
                todosCalculos.size() - calculos.size();

        CalculoTotal total =
                calculoService.calcularTotal(calculos);

        Map<CentroDeCusto, CalculoTotal> totalPorCentroCusto =
                calculoService.calcularTotalPorCentroDeCusto(calculos);

        model.addAttribute("calculos", calculos);
        model.addAttribute("competencia", competencia);
        model.addAttribute("total", total);
        model.addAttribute("totalPorCentroCusto", totalPorCentroCusto);
        model.addAttribute("qntFuncionariosSemCC", qntFuncionariosSemCC);

        return "dashboard";
    }


    @GetMapping("/funcionarios/sem-centro-custo")
    public String funcionariosSemCc(Model model) {

        List<Funcionario> funcionariosSemCC =
                funcionarioRepository.findByCentroDeCustoEmptyOrNull();

        model.addAttribute("funcionariosSemCC", funcionariosSemCC);

        model.addAttribute(
                "centrosDeCustoExistentes",
                CentroDeCusto.values()
        );
        return "funcionarios-sem-cc";
    }

    @PostMapping("/funcionarios/sem-centro-custo")
    public String salvarCentrosDeCusto(
            @RequestParam("funcionarioIds") List<Long> funcionarioIds,
            @RequestParam("centrosDeCusto") List<CentroDeCusto> centrosDeCusto,
            RedirectAttributes redirectAttributes) {

        if (funcionarioIds.size() != centrosDeCusto.size()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Os dados enviados são inconsistentes."
            );

            return "redirect:/funcionarios/sem-centro-custo";
        }

        List<Funcionario> funcionariosParaSalvar = new ArrayList<>();

        for (int i = 0; i < funcionarioIds.size(); i++) {

            Long funcionarioId = funcionarioIds.get(i);

            CentroDeCusto centroDeCusto = centrosDeCusto.get(i);

            if (centroDeCusto == null) {
                continue;
            }

            Optional<Funcionario> resultado =
                    funcionarioRepository.findById(funcionarioId);

            if (resultado.isEmpty()) {
                continue;
            }

            Funcionario funcionario = resultado.get();

            // Impede sobrescrever um centro já definido.
            if (funcionario.getCentroDeCusto() != null) {
                continue;
            }

            funcionario.setCentroDeCusto(centroDeCusto);

            funcionariosParaSalvar.add(funcionario);
        }

        funcionarioRepository.saveAll(funcionariosParaSalvar);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                funcionariosParaSalvar.size() +
                        " funcionário(s) atualizado(s) com sucesso!"
        );

        return "redirect:/funcionarios/sem-centro-custo";
    }

}
