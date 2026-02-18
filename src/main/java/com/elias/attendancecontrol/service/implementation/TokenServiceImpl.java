package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.SessionToken;
import com.elias.attendancecontrol.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Override
    public SessionToken generateToken(Long userId, String ipAddress) {
        // TODO: Implementar generación de token
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        // TODO: Implementar validación de token
        return false;
    }

    @Override
    public void revokeToken(String token) {
        // TODO: Implementar revocación de token
    }
}

