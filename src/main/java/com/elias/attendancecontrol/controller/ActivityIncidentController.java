package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.ActivityExceptionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/activity-exceptions")
public class ActivityExceptionController {

    private final ActivityExceptionService activityExceptionService;

    public ActivityExceptionController(ActivityExceptionService activityExceptionService) {
        this.activityExceptionService = activityExceptionService;
    }

    @PostMapping("/reschedule/{activityId}")
    public void rescheduleOccurrence(@PathVariable Long activityId) {
        // TODO: Implementar reprogramación de ocurrencia
    }

    @PostMapping("/cancel/{activityId}")
    public void cancelOccurrence(@PathVariable Long activityId) {
        // TODO: Implementar cancelación de ocurrencia
    }
}
