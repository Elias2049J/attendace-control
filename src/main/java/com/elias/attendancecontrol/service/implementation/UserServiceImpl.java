package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User createUser(User user) {
        // TODO: Implementar creación de usuario
        return null;
    }

    @Override
    public User updateUser(Long id, User user) {
        // TODO: Implementar actualización de usuario
        return null;
    }

    @Override
    public void deactivateUser(Long id) {
        // TODO: Implementar desactivación de usuario
    }

    @Override
    public List<User> listUsers() {
        // TODO: Implementar listado de usuarios
        return List.of();
    }

    @Override
    public User getUserById(Long id) {
        // TODO: Implementar obtención de usuario por ID
        return null;
    }
}

