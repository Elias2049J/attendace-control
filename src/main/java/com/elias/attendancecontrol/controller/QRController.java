package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.QRService;
import com.elias.attendancecontrol.service.TokenGeneratorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/qr")
public class QRController {

    private final QRService qrService;
    private final TokenGeneratorService tokenGeneratorService;

    public QRController(QRService qrService, TokenGeneratorService tokenGeneratorService) {
        this.qrService = qrService;
        this.tokenGeneratorService = tokenGeneratorService;
    }

    @PostMapping("/generate/{sessionId}")
    public void generateQR(@PathVariable Long sessionId) {
        // TODO: Implementar generación de código QR
    }

    @PostMapping("/regenerate/{sessionId}")
    public void regenerateQR(@PathVariable Long sessionId) {
        // TODO: Implementar regeneración de código QR
    }

    @PostMapping("/validate")
    public void validateQR(@RequestBody String token) {
        // TODO: Implementar validación de código QR
    }
}
