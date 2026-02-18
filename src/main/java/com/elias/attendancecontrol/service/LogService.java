package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {

    /**
     * Registra un evento en el log de auditoría
     */
    AuditLog logEvent(Long userId, String eventType, String description, String ipAddress);

    /**
     * Crea un registro de auditoría
     */
    AuditLog createAuditLog(AuditLog auditLog);


    /**
     * Lista todos los logs de auditoría
     */
    List<AuditLog> listAuditLogs();

    /**
     * Filtra logs de auditoría
     */
    List<AuditLog> filterAuditLogs(Long userId, String eventType, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Obtiene logs de auditoría por usuario
     */
    List<AuditLog> getAuditByUser(Long userId);
}

