package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionStatus {
    PLANNED("Programada"),
    ACTIVE("En curso"),
    CLOSED("Cerrada"),
    CANCELLED("Cancelada");

    private final String displayName;

    public boolean isPlanned() {
        return this == PLANNED;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isFinalState() {
        return this == CLOSED || this == CANCELLED;
    }
}
