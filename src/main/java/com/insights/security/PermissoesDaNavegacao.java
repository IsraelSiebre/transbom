package com.insights.security;

import com.insights.rh.Controller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {Controller.class, UsuarioController.class})
public class PermissoesDaNavegacao {
    @ModelAttribute("administrador")
    public boolean administrador(HttpServletRequest request) {
        return request.isUserInRole(Role.ADMIN.name());
    }
}
