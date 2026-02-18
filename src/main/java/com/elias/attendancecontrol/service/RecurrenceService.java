package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.RecurrenceRule;

public interface RecurrenceService {

    /**
     * Configura la recurrencia para una actividad
     */
    RecurrenceRule configureRecurrence(Long activityId, RecurrenceRule recurrenceRule);

    /**
     * Actualiza una regla de recurrencia existente
     */
    RecurrenceRule updateRecurrence(Long id, RecurrenceRule recurrenceRule);

    /**
     * Valida una regla de recurrencia
     */
    boolean validateRecurrence(RecurrenceRule recurrenceRule);
}

