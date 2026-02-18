package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.Activity;
import com.elias.attendancecontrol.model.entity.Attendance;
import com.elias.attendancecontrol.model.entity.AttendanceStatus;
import com.elias.attendancecontrol.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Override
    public Activity createActivity(Activity activity) {
        // TODO: Implementar creación de actividad
        return null;
    }

    @Override
    public Activity updateActivity(Long id, Activity activity) {
        // TODO: Implementar actualización de actividad
        return null;
    }

    @Override
    public void deactivateActivity(Long id) {
        // TODO: Implementar desactivación de actividad
    }

    @Override
    public List<Activity> listActivities() {
        // TODO: Implementar listado de actividades
        return List.of();
    }

    @Override
    public Activity getActivityById(Long id) {
        // TODO: Implementar obtención de actividad por ID
        return null;
    }

    @Override
    public AttendanceStatus classifyAttendance(Long sessionId, LocalDateTime registrationTime) {
        return null;
    }

    @Override
    public AttendanceStatus determineStatus(LocalDateTime sessionStart, LocalDateTime registrationTime) {
        return null;
    }

    @Override
    public List<Attendance> getAttendanceHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<Attendance> getAttendanceBySession(Long sessionId) {
        return List.of();
    }

    @Override
    public List<Attendance> getAttendanceByUser(Long userId) {
        return List.of();
    }
}

