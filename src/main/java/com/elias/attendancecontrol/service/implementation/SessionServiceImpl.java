package com.elias.attendancecontrol.service.implementation;
import com.elias.attendancecontrol.model.entity.Session;
import com.elias.attendancecontrol.service.SessionService;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class SessionServiceImpl implements SessionService {
    @Override
    public Session activateSession(Long id) {
        // TODO: Implementar activación de sesión
        return null;
    }
    @Override
    public Session closeSession(Long id) {
        // TODO: Implementar cierre de sesión
        return null;
    }
    @Override
    public List<Session> listSessions() {
        // TODO: Implementar listado de sesiones
        return List.of();
    }
    @Override
    public Session getSessionById(Long id) {
        // TODO: Implementar obtención de sesión por ID
        return null;
    }
}
