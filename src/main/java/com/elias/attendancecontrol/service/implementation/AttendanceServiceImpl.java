package com.elias.attendancecontrol.service.implementation;
import com.elias.attendancecontrol.model.entity.Attendance;
import com.elias.attendancecontrol.service.AttendanceService;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Override
    public Attendance registerAttendance(Long userId, String qrToken) {
        // TODO: Implementar registro de asistencia por QR
        return null;
    }
    @Override
    public Attendance manualRegistration(Long sessionId, Long userId) {
        // TODO: Implementar registro manual
        return null;
    }
    @Override
    public List<Attendance> listAttendance() {
        // TODO: Implementar listado de asistencias
        return List.of();
    }
}
