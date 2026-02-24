package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    DRAFT("Borrador"),
    SCHEDULED("Programada"),
    PAUSED("Pausada"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada");

    private final String displayName;

    public boolean isDraft() {
        return this == DRAFT;
    }

    public boolean isScheduled() {
        return this == SCHEDULED;
    }

    public boolean isPaused() {
        return this == PAUSED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isEditable() {
        return this == DRAFT || this == SCHEDULED || this == PAUSED;
    }

    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED;
    }
}