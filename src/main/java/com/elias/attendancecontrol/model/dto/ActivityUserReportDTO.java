package com.elias.attendancecontrol.model.dto;

public record ActivityUserReportDTO(
        Long activityId,
        String activityName,
        Long userId,
        String userName,
        Long totalSesiones,
        Long totalPresentes,
        Long totalFaltas,
        Long totalTardanzas
) {}
