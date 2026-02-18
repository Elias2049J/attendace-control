package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.Attendance;

import java.util.List;

public interface AttendanceService {

    /**
     * Registra la asistencia mediante QR
     */
    Attendance registerAttendance(Long userId, String qrToken);

    /**
     * Registra la asistencia manualmente
     */
    Attendance manualRegistration(Long sessionId, Long userId);

    /**
     * Lista todas las asistencias
     */
    List<Attendance> listAttendance();
}

