package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrganizationPlan {
    FREE("Gratuito"),   // Plan limitado (50 usuarios, 10 actividades)
    BASIC("Básico"),       // Plan de 200 usuarios, 50 actividades
    PREMIUM("Premium"),     // Plan con más features (1000 usuarios, 200 actividades)
    ENTERPRISE("Enterprise"); // Plan ilimitado
    private final String displayName;
}
