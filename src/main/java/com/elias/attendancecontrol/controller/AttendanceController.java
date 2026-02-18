package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.AttendanceQueryService;
import com.elias.attendancecontrol.service.AttendanceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceQueryService attendanceQueryService;

    public AttendanceController(AttendanceService attendanceService,
                               AttendanceQueryService attendanceQueryService) {
        this.attendanceService = attendanceService;
        this.attendanceQueryService = attendanceQueryService;
    }

    @PostMapping("/register")
    public void registerAttendance() {
        // TODO: Implementar registro de asistencia por QR
    }

    @PostMapping("/manual-registration")
    public void manualRegistration() {
        // TODO: Implementar registro manual de asistencia
    }

    @GetMapping
    public void listAttendance() {
        // TODO: Implementar listado de asistencias
    }

    @GetMapping("/history")
    public void getAttendanceHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long sessionId) {
        // TODO: Implementar historial de asistencias
    }
}
