package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.ActivityException;
import com.elias.attendancecontrol.service.ActivityExceptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ActivityExceptionServiceImpl implements ActivityExceptionService {

    @Override
    public ActivityException createException(ActivityException exception) {
        // TODO: Implementar creación de excepción
        return null;
    }

    @Override
    public ActivityException rescheduleOccurrence(Long activityId, LocalDate originalDate, LocalDate newDate, String reason) {
        // TODO: Implementar reprogramación de ocurrencia
        return null;
    }

    @Override
    public ActivityException cancelOccurrence(Long activityId, LocalDate date, String reason) {
        // TODO: Implementar cancelación de ocurrencia
        return null;
    }
}

