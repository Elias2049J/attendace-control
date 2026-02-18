package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.SessionToken;

public interface TokenService {

    /**
     * Genera un nuevo token de sesión
     */
    SessionToken generateToken(Long userId, String ipAddress);

    /**
     * Valida si un token es válido y activo
     */
    boolean validateToken(String token);

    /**
     * Revoca un token existente
     */
    void revokeToken(String token);
}

