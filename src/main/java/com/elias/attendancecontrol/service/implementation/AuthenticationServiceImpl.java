package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.AuthenticationService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public User authenticate(String username, String password) {
        // TODO: Implementar lógica de autenticación
        return null;
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        // TODO: Implementar validación de credenciales
        return false;
    }

    @Override
    public String createSession(User user, String ipAddress) {
        // TODO: Implementar creación de sesión
        return null;
    }
}

