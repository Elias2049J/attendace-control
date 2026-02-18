package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.SessionGeneratorService;
import com.elias.attendancecontrol.service.SessionService;
import com.elias.attendancecontrol.service.SessionStateValidatorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionGeneratorService sessionGeneratorService;
    private final SessionStateValidatorService sessionStateValidatorService;

    public SessionController(SessionService sessionService,
                           SessionGeneratorService sessionGeneratorService,
                           SessionStateValidatorService sessionStateValidatorService) {
        this.sessionService = sessionService;
        this.sessionGeneratorService = sessionGeneratorService;
        this.sessionStateValidatorService = sessionStateValidatorService;
    }

    @PostMapping("/{id}/activate")
    public void activateSession(@PathVariable Long id) {
        // TODO: Implementar activación de sesión
    }

    @PostMapping("/{id}/close")
    public void closeSession(@PathVariable Long id) {
        // TODO: Implementar cierre de sesión
    }

    @GetMapping
    public void listSessions() {
        // TODO: Implementar listado de sesiones
    }

    @PostMapping("/generate/{activityId}")
    public void generateSessions(@PathVariable Long activityId) {
        // TODO: Implementar generación automática de sesiones
    }
}
