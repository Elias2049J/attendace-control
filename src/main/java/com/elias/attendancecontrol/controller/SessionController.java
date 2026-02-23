package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.entity.Session;
import com.elias.attendancecontrol.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Slf4j
@Controller
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final SecurityUtils securityUtils;

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ORG_MEMBER')")
    public String activateSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("Activating session: {}", id);
        try {
            Session session = sessionService.getSessionById(id);
            if (session.getActivity() != null && session.getActivity().getOrganization() != null) {
                securityUtils.validateResourceOwnership(session.getActivity().getOrganization().getId());
            }
            sessionService.activateSession(id);
            redirectAttributes.addFlashAttribute("success", "Sesión activada exitosamente");
            return "redirect:/activities/" + session.getActivity().getId() + "/sessions";
        } catch (SecurityException e) {
            log.warn("User attempted to activate session from different organization: {}", id);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede activar: " + e.getMessage());
        }
        return "redirect:/calendar";
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ORG_MEMBER')")
    public String closeSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("Closing session: {}", id);
        try {
            Session session = sessionService.getSessionById(id);
            if (session.getActivity() != null && session.getActivity().getOrganization() != null) {
                securityUtils.validateResourceOwnership(session.getActivity().getOrganization().getId());
            }
            sessionService.closeSession(id);
            redirectAttributes.addFlashAttribute("success", "Sesión cerrada exitosamente");
            return "redirect:/activities/" + session.getActivity().getId() + "/sessions";
        } catch (SecurityException e) {
            log.warn("User attempted to close session from different organization: {}", id);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede cerrar: " + e.getMessage());
        }
        return "redirect:/calendar";
    }

    @PostMapping("/generate/{activityId}")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ORG_MEMBER')")
    public String generateSessions(@PathVariable Long activityId, RedirectAttributes redirectAttributes) {
        log.debug("Generating sessions for activity: {}", activityId);
        try {
            sessionService.generateSessions(activityId);
            redirectAttributes.addFlashAttribute("success", "Sesiones generadas exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se pueden generar sesiones: " + e.getMessage());
        }
        return "redirect:/activities/" + activityId + "/sessions";
    }
}
