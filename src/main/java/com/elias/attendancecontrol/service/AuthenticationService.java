package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.User;

public interface AuthenticationService {

    /**
     * Autentica un usuario con sus credenciales
     */
    User authenticate(String username, String password);

    /**
     * Valida las credenciales de un usuario
     */
    boolean validateCredentials(String username, String password);

    /**
     * Crea una sesión para el usuario autenticado
     */
    String createSession(User user, String ipAddress);
}

