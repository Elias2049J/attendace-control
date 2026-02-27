package com.elias.attendancecontrol.model.dto;

public record UserActivityReportDTO(
        Long userId,
        String userName,
        Long activityId,
        String activityName,
        Long totalSesiones,
        Long totalPresentes,
        Long totalFaltas,
        Long totalTardanzas
){}
