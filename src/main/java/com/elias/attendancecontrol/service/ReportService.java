package com.elias.attendancecontrol.service;

import java.time.LocalDate;
import java.util.Map;

public interface ReportService {

    /**
     * Genera un reporte general
     */
    Map<String, Object> generateReport(LocalDate startDate, LocalDate endDate);

    /**
     * Genera un reporte por actividad
     */
    Map<String, Object> generateActivityReport(Long activityId, LocalDate startDate, LocalDate endDate);

    /**
     * Genera un reporte por usuario
     */
    Map<String, Object> generateUserReport(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Exporta un reporte
     */
    byte[] exportReport(Map<String, Object> reportData, String format);
}

