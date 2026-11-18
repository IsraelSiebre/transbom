package com.insights.security;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/usuarios/novo")
    public String novo() {
        return "seguranca/novo-usuario";
    }

    @PostMapping("/usuarios/novo")
    public String criar(@RequestParam String username, @RequestParam String senha,
                        @RequestParam String confirmacaoSenha, Model model) {
        model.addAttribute("username", username);
        if (!senha.equals(confirmacaoSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            return "seguranca/novo-usuario";
        }
        try {
            usuarioService.criarUsuario(username, senha);
            return "redirect:/usuarios/novo?sucesso";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("erro", "Não foi possível criar o usuário. Verifique se o nome já está cadastrado.");
        }
        return "seguranca/novo-usuario";
    }
}
