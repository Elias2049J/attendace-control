package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.Role;
import com.elias.attendancecontrol.model.entity.User;

public interface RoleService {

    /**
     * Asigna un rol a un usuario
     */
    void assignRole(Long userId, Role role);

    /**
     * Valida si un usuario tiene un rol específico
     */
    boolean validateRole(User user, Role role);
}

