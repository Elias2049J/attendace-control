package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.Activity;
import com.elias.attendancecontrol.model.entity.Session;

import java.time.LocalDate;
import java.util.List;

public interface CalendarService {

    /**
     * Obtiene la vista del calendario
     */
    List<Session> getCalendarView(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene actividades por rango de fechas
     */
    List<Activity> getActivitiesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Aplica excepciones al calendario
     */
    List<Session> applyExceptions(List<Session> sessions);
}

