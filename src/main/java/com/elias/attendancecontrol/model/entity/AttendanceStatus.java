package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceStatus {
    PRESENT("Presente"),
    LATE("Tarde"),
    ABSENT("Ausente");

    private final String displayName;

    public boolean isLate() {
        return this == LATE;
    }

    public boolean isPresent() {
        return this == PRESENT;
    }

    public boolean isAbsent() {
        return this == ABSENT;
    }
}