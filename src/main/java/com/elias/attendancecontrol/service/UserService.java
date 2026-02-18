package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.User;

import java.util.List;

public interface UserService {

    /**
     * Crea un nuevo usuario
     */
    User createUser(User user);

    /**
     * Actualiza un usuario existente
     */
    User updateUser(Long id, User user);

    /**
     * Desactiva un usuario
     */
    void deactivateUser(Long id);

    /**
     * Lista todos los usuarios
     */
    List<User> listUsers();

    /**
     * Obtiene un usuario por ID
     */
    User getUserById(Long id);
}

