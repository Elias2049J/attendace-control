package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnrollmentStatus {
    ENROLLED("Inscrito"),
    DROPPED("Dado de baja"),
    COMPLETED("Completado");

    private final String displayName;

    public boolean isEnrolled() {
        return this == ENROLLED;
    }

    public boolean isDropped() {
        return this == DROPPED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
