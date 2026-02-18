package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.AuthenticationService;
import com.elias.attendancecontrol.service.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    public AuthController(AuthenticationService authenticationService, TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // TODO: Implementar lógica de autenticación
        return "auth/login";
    }

    @PostMapping("/login")
    public String login() {
        // TODO: Implementar lógica de autenticación
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout() {
        // TODO: Implementar lógica de cierre de sesión
        return "redirect:/auth/login";
    }
}
