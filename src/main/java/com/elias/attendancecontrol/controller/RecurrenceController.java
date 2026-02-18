package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.RecurrenceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/recurrence")
public class RecurrenceController {

    private final RecurrenceService recurrenceService;

    public RecurrenceController(RecurrenceService recurrenceService) {
        this.recurrenceService = recurrenceService;
    }

    @PostMapping("/{activityId}")
    public void configureRecurrence(@PathVariable Long activityId) {
        // TODO: Implementar configuración de recurrencia
    }

    @PutMapping("/{id}")
    public void updateRecurrence(@PathVariable Long id) {
        // TODO: Implementar actualización de recurrencia
    }
}
