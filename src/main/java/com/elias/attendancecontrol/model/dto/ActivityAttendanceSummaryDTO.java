package com.elias.attendancecontrol.model.dto;

public record ActivityAttendanceSummaryDTO(
    Long activityId,
    String activityName,
    Long totalSesiones,
    Long totalPresentes,
    Long totalFaltas,
    Long totalTardanzas,
    Double porcentajeAsistencia
){}
