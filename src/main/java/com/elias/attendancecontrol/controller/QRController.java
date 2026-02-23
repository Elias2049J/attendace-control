package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;
@Slf4j
@Controller
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QRController {
    private final TokenService tokenService;

    @Value("${app.base-url}")
    private String baseUrl;
    @GetMapping("/generate/{sessionId}")
    public String generateQR(@PathVariable Long sessionId, Model model) {
        log.debug("Generating QR for session: {}", sessionId);
        Map<String, Object> qrData = tokenService.generateQRWithFullData(sessionId, baseUrl);
        model.addAllAttributes(qrData);
        return "qr/view";
    }
    @PostMapping("/regenerate/{sessionId}")
    public String regenerateQR(@PathVariable Long sessionId, RedirectAttributes redirectAttributes) {
        log.debug("Regenerating QR for session: {}", sessionId);
        tokenService.regenerateQR(sessionId);
        redirectAttributes.addFlashAttribute("success", "Código QR regenerado exitosamente");
        return "redirect:/qr/generate/" + sessionId;
    }
    @PostMapping("/validate")
    @ResponseBody
    public boolean validateQR(@RequestBody String token) {
        log.debug("Validating QR token");
        return tokenService.validateQR(token);
    }
}
