package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.entity.Attendance;
import com.elias.attendancecontrol.model.entity.QRToken;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.AttendanceService;
import com.elias.attendancecontrol.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AttendanceVerificationController {
    private final TokenService tokenService;
    private final AttendanceService attendanceService;
    private final SecurityUtils securityUtils;

    @GetMapping("/org/{orgSlug}/attendance/verify")
    @PreAuthorize("isAuthenticated()")
    public String verifyQRAndRegister(
            @PathVariable String orgSlug,
            @RequestParam String token,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.debug("Verifying attendance with token: {} for organization slug: {}", token, orgSlug);

        User currentUser = securityUtils.getCurrentUserOrThrow();
        log.debug("User {} attempting to register attendance", currentUser.getUsername());

        if (!tokenService.validateQR(token)) {
            log.warn("Invalid or expired QR token: {}", token);
            redirectAttributes.addFlashAttribute("error", "Código QR inválido o expirado");
            return "redirect:/";
        }

        QRToken qrToken = tokenService.getQRTokenWithSessionAndOrganization(token, orgSlug);
        Attendance attendance = attendanceService.registerAttendance(currentUser.getId(), token);

        model.addAttribute("attendance", attendance);
        model.addAttribute("session", qrToken.getSession());
        model.addAttribute("activity", qrToken.getSession().getActivity());
        model.addAttribute("user", currentUser);
        model.addAttribute("registrationTime", LocalDateTime.now());
        model.addAttribute("organizationSlug", orgSlug);

        log.info("Attendance registered successfully via QR link for user: {} in session: {} (org: {})",
                currentUser.getUsername(), qrToken.getSession().getId(), orgSlug);

        return "attendance/verify";
    }
}

