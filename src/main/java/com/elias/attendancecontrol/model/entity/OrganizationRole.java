package com.elias.attendancecontrol.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrganizationRole {
    OWNER("Representante"),
    ADMIN("Administrador"),
    MEMBER("Miembro");

    private final String displayName;

    public boolean isOwner() {
        return this == OWNER;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isMember() {
        return this == MEMBER;
    }
}
