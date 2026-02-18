package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public void createActivity() {
        // TODO: Implementar creación de actividad
    }

    @PutMapping("/{id}")
    public void updateActivity(@PathVariable Long id) {
        // TODO: Implementar actualización de actividad
    }

    @DeleteMapping("/{id}")
    public void deactivateActivity(@PathVariable Long id) {
        // TODO: Implementar desactivación de actividad
    }

    @GetMapping
    public String listActivities(Model model) {
        // TODO: Implementar listado de actividades
        return "activities/list";
    }
}
