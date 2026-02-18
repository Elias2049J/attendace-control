package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.CalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public void getCalendarView() {
        // TODO: Implementar vista de calendario
    }

    @GetMapping("/activities")
    public void getActivitiesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        // TODO: Implementar obtención de actividades por rango de fechas
    }
}
