package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.Activity;
import com.elias.attendancecontrol.model.entity.Attendance;
import com.elias.attendancecontrol.model.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {

    /**
     * Crea una nueva actividad
     */
    Activity createActivity(Activity activity);

    /**
     * Actualiza una actividad existente
     */
    Activity updateActivity(Long id, Activity activity);

    /**
     * Desactiva una actividad
     */
    void deactivateActivity(Long id);

    /**
     * Lista todas las actividades
     */
    List<Activity> listActivities();

    /**
     * Obtiene una actividad por ID
     */
    Activity getActivityById(Long id);

    AttendanceStatus classifyAttendance(Long sessionId, LocalDateTime registrationTime);

    /**
     * Determina el estado de asistencia
     */
    AttendanceStatus determineStatus(LocalDateTime sessionStart, LocalDateTime registrationTime);


    /**
     * Obtiene el historial de asistencias
     */
    List<Attendance> getAttendanceHistory(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene asistencias por sesión
     */
    List<Attendance> getAttendanceBySession(Long sessionId);

    /**
     * Obtiene asistencias por usuario
     */
    List<Attendance> getAttendanceByUser(Long userId);
}

