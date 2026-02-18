package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.ReportService;
import com.elias.attendancecontrol.service.StatisticsCalculatorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final StatisticsCalculatorService statisticsCalculatorService;

    public ReportController(ReportService reportService,
                           StatisticsCalculatorService statisticsCalculatorService) {
        this.reportService = reportService;
        this.statisticsCalculatorService = statisticsCalculatorService;
    }

    @PostMapping("/generate")
    public void generateReport() {
        // TODO: Implementar generación de reporte general
    }

    @PostMapping("/activity/{activityId}")
    public void generateActivityReport(@PathVariable Long activityId) {
        // TODO: Implementar generación de reporte por actividad
    }

    @PostMapping("/user/{userId}")
    public void generateUserReport(@PathVariable Long userId) {
        // TODO: Implementar generación de reporte por usuario
    }
}
