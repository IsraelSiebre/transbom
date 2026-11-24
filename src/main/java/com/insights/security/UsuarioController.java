package com.insights.security;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "seguranca/login";
    }

    @GetMapping("/minha-conta/senha")
    public String minhaSenha(Principal principal, Model model) {
        model.addAttribute("usuarioAtual", principal.getName());
        return "seguranca/alterar-senha";
    }

    @PostMapping("/minha-conta/senha")
    public String alterarSenha(Principal principal, @RequestParam String senhaAtual,
                              @RequestParam String novaSenha, @RequestParam String confirmacaoSenha,
                              Model model, RedirectAttributes redirect) {
        if (!novaSenha.equals(confirmacaoSenha)) {
            model.addAttribute("erro", "A confirmação deve ser igual à nova senha.");
            return minhaSenha(principal, model);
        }
        try {
            usuarioService.alterarSenha(principal.getName(), senhaAtual, novaSenha);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return minhaSenha(principal, model);
        }
        redirect.addFlashAttribute("sucesso", "Senha alterada. Use a nova senha no próximo acesso.");
        return "redirect:/minha-conta/senha";
    }

    @GetMapping("/usuarios/novo")
    public String novo(Model model) {
        return formularioDeCadastro(model, Role.VISUALIZADOR);
    }

    @PostMapping("/usuarios/novo")
    public String criar(@RequestParam String username, @RequestParam String senha,
                        @RequestParam String confirmacaoSenha,
                        @RequestParam(defaultValue = "VISUALIZADOR") Role role, Model model) {
        model.addAttribute("username", username);
        if (!senha.equals(confirmacaoSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            return formularioDeCadastro(model, role);
        }
        try {
            usuarioService.criarUsuario(username, senha, role);
            return "redirect:/usuarios/novo?sucesso";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("erro", "Não foi possível criar o usuário. Verifique se o nome já está cadastrado.");
        }
        return formularioDeCadastro(model, role);
    }

    private String formularioDeCadastro(Model model, Role selecionado) {
        model.addAttribute("perfis", Role.values());
        model.addAttribute("perfilSelecionado", selecionado);
        return "seguranca/novo-usuario";
    }
}
