package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.service.ActivityService;
import com.elias.attendancecontrol.service.ReportService;
import com.elias.attendancecontrol.service.UserService;
import com.elias.attendancecontrol.service.implementation.FileExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
@Slf4j
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ActivityService activityService;
    private final UserService userService;
    private final FileExportService fileExportService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN')")
    public String showReportsMenu(Model model, @RequestParam(required = false) String success, @RequestParam(required = false) String error) {
        log.debug("Showing reports menu");
        model.addAttribute("activities", activityService.listActivitiesSorted());
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("activeMenu", "reports");
        if (success != null) model.addAttribute("success", success);
        if (error != null) model.addAttribute("error", error);
        return "reports/menu";
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN')")
    public String generateReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 Model model) {
        log.debug("Generating general report from {} to {}", startDate, endDate);
        try {
            var reportData = reportService.generateActivitiesUsersGeneralReport(startDate, endDate);
            model.addAttribute("reportData", reportData);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("success", "Reporte generado correctamente");
            return "reports/general";
        } catch (Exception e) {
            log.error("Error generating report", e);
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "reports/menu";
        }
    }

    @GetMapping("/activity/{activityId}/export")
    public ResponseEntity<byte[]> exportActivityReport(
            @PathVariable Long activityId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format) {

        byte[] fileBytes = fileExportService.exportReport(
                reportService.generateActivityReport(activityId, startDate, endDate),
                format);

        String filename;
        MediaType mediaType;

        if ("excel".equalsIgnoreCase(format)) {
            filename = "activity-report.xlsx";
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            filename = "activity-report.pdf";
            mediaType = MediaType.APPLICATION_PDF;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/general/export")
    public ResponseEntity<byte[]> exportGeneralReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format) {

        byte[] fileBytes = fileExportService.exportReport(
                reportService.generateActivitiesUsersGeneralReport(startDate, endDate),
                format);

        String filename;
        MediaType mediaType;

        if ("excel".equalsIgnoreCase(format)) {
            filename = "general-attendance.xlsx";
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            filename = "general-attendance-report.pdf";
            mediaType = MediaType.APPLICATION_PDF;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/member/{userId}/export")
    public ResponseEntity<byte[]> exportUserReport(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "pdf") String format) {

        byte[] fileBytes = fileExportService.exportReport(
                reportService.generateUserReport(userId, startDate, endDate),
                format);

        String filename;
        MediaType mediaType;

        if ("excel".equalsIgnoreCase(format)) {
            filename = "member-report.xlsx";
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            filename = "member-report.pdf";
            mediaType = MediaType.APPLICATION_PDF;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/activity/{activityId}")
    public String generateActivityReport(@PathVariable Long activityId,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                         Model model) {
        log.debug("Generating activity report for activity: {} from {} to {}", activityId, startDate, endDate);
        try {
            var reportData = reportService.generateActivityReport(activityId, startDate, endDate);
            model.addAttribute("reportData", reportData);
            model.addAttribute("activityId", activityId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("success", "Reporte generado correctamente");
            return "reports/activity";
        } catch (Exception e) {
            log.error("Error generating activity report", e);
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "reports/menu";
        }
    }

    @PostMapping("/user/{userId}")
    public String generateUserReport(@PathVariable Long userId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                     Model model) {
        log.debug("Generating user report for user: {} from {} to {}", userId, startDate, endDate);
        try {
            var reportData = reportService.generateUserReport(userId, startDate, endDate);
            model.addAttribute("reportData", reportData);
            model.addAttribute("userId", userId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("success", "Reporte generado correctamente");
            return "reports/user";
        } catch (Exception e) {
            log.error("Error generating user report", e);
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "reports/menu";
        }
    }
}
