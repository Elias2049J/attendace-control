package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.entity.Attendance;
import com.elias.attendancecontrol.model.entity.AttendanceStatus;
import com.elias.attendancecontrol.model.entity.Session;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.AttendanceService;
import com.elias.attendancecontrol.service.EnrollmentService;
import com.elias.attendancecontrol.service.SessionService;
import com.elias.attendancecontrol.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final UserService userService;
    private final SessionService sessionService;
    private final SecurityUtils securityUtils;
    private final EnrollmentService enrollmentService;


    @GetMapping("/manual/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public String showManualRegistrationForm(@PathVariable Long sessionId, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing manual attendance registration form for session: {}", sessionId);
        try {
            Session session = sessionService.getSessionById(sessionId);

            if (session.getStatus() == com.elias.attendancecontrol.model.entity.SessionStatus.CLOSED) {
                redirectAttributes.addFlashAttribute("error", "La sesión está cerrada, no se puede editar");
                return "redirect:/sessions";
            }

            if (session.getStatus() != com.elias.attendancecontrol.model.entity.SessionStatus.ACTIVE) {
                redirectAttributes.addFlashAttribute("error", "La sesión no está activa");
                return "redirect:/sessions";
            }

            List<User> enrolledUsers;
            if (session.getActivity().getRequiresEnrollment() != null && session.getActivity().getRequiresEnrollment()) {
                enrolledUsers = enrollmentService.getEnrolledParticipants(session.getActivity().getId());
            } else {
                enrolledUsers = userService.listUsers();
            }

            List<Attendance> existingAttendances = attendanceService.getAttendanceBySession(sessionId);

            Map<Long, Attendance> existingAttendanceMap = new HashMap<>();
            for (Attendance att : existingAttendances) {
                existingAttendanceMap.put(att.getUser().getId(), att);
            }

            model.addAttribute("session", session);
            model.addAttribute("activity", session.getActivity());
            model.addAttribute("users", enrolledUsers);
            model.addAttribute("existingAttendances", existingAttendances);
            model.addAttribute("existingAttendanceMap", existingAttendanceMap);
            model.addAttribute("attendanceStatuses", AttendanceStatus.values());

            return "attendance/manual-registration";
        } catch (Exception e) {
            log.error("Error showing manual registration form: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/sessions";
        }
    }

    @PostMapping("/manual-registration")
    @PreAuthorize("isAuthenticated()")
    public String manualRegistrationBatch(@RequestParam Long sessionId,
                                          @RequestParam Map<String, String> allParams,
                                          RedirectAttributes redirectAttributes) {
        log.debug("Manual batch attendance registration for session: {}", sessionId);
        try {
            Map<Long, AttendanceStatus> userAttendances = new HashMap<>();

            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("attendance_")) {
                    Long userId = Long.parseLong(key.substring("attendance_".length()));
                    AttendanceStatus status = AttendanceStatus.valueOf(entry.getValue());
                    userAttendances.put(userId, status);
                }
            }

            if (userAttendances.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe marcar al menos una asistencia");
                return "redirect:/attendance/manual/" + sessionId;
            }

            List<Attendance> attendances = attendanceService.manualRegistrationBatch(sessionId, userAttendances);
            redirectAttributes.addFlashAttribute("success",
                String.format("Asistencia registrada exitosamente para %d usuario(s)", attendances.size()));

            return "redirect:/sessions";
        } catch (IllegalArgumentException e) {
            log.error("Error in manual batch registration: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/attendance/manual/" + sessionId;
        } catch (IllegalStateException e) {
            log.error("Cannot register batch attendance: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "No se puede registrar: " + e.getMessage());
            return "redirect:/attendance/manual/" + sessionId;
        } catch (Exception e) {
            log.error("Unexpected error in manual batch registration", e);
            redirectAttributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/attendance/manual/" + sessionId;
        }
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public String getAttendanceHistory(@RequestParam(required = false) Long userId,
                                      @RequestParam(required = false) Long sessionId,
                                      Model model) {
        log.debug("Getting attendance history with filters - userId: {}, sessionId: {}", userId, sessionId);
        User currentUser = securityUtils.getCurrentUserOrThrow();
        List<Attendance> attendances;
        if (securityUtils.isSystemAdmin() || securityUtils.isOrganizationOwnerOrAdmin()) {
            attendances = attendanceService.listAttendance();
            model.addAttribute("users", userService.listUsers());
            model.addAttribute("canViewAll", true);
        } else {
            attendances = attendanceService.getAttendanceByUser(currentUser.getId());
            model.addAttribute("canViewAll", false);
        }
        model.addAttribute("attendances", attendances);
        model.addAttribute("sessions", sessionService.listSessions());
        model.addAttribute("currentUser", currentUser);
        return "attendance/history";
    }
}
