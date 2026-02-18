package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;
    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public void listAuditLogs() {
        // TODO: Implementar listado de logs de auditoría
    }

    @GetMapping("/filter")
    public void filterAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // TODO: Implementar filtrado de logs de auditoría
    }
}
