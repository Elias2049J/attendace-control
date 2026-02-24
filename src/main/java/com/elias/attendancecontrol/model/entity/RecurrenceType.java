package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecurrenceType {
    NONE("Evento Único"),
    DAILY("Diario"),
    WEEKLY("Semanal"),
    MONTHLY("Mensual");

    private final String displayName;

    public boolean isUnique() {
        return this == NONE;
    }

    public boolean isDaily() {
        return this == DAILY;
    }

    public boolean isWeekly() {
        return this == WEEKLY;
    }

    public boolean isMonthly() {
        return this == MONTHLY;
    }
}
