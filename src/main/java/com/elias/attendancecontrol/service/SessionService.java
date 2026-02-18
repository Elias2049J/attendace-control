package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.Session;

import java.util.List;

public interface SessionService {

    /**
     * Activa una sesión
     */
    Session activateSession(Long id);

    /**
     * Cierra una sesión
     */
    Session closeSession(Long id);

    /**
     * Lista todas las sesiones
     */
    List<Session> listSessions();

    /**
     * Obtiene una sesión por ID
     */
    Session getSessionById(Long id);
}

