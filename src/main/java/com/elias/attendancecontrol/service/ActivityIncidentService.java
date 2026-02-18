package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.ActivityException;

import java.time.LocalDate;

public interface ActivityExceptionService {

    /**
     * Crea una excepción de actividad
     */
    ActivityException createException(ActivityException exception);

    /**
     * Reprograma una ocurrencia de actividad
     */
    ActivityException rescheduleOccurrence(Long activityId, LocalDate originalDate, LocalDate newDate, String reason);

    /**
     * Cancela una ocurrencia de actividad
     */
    ActivityException cancelOccurrence(Long activityId, LocalDate date, String reason);
}

