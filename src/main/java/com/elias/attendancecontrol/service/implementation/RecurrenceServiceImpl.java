package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.RecurrenceRule;
import com.elias.attendancecontrol.service.RecurrenceService;
import org.springframework.stereotype.Service;

@Service
public class RecurrenceServiceImpl implements RecurrenceService {

    @Override
    public RecurrenceRule configureRecurrence(Long activityId, RecurrenceRule recurrenceRule) {
        // TODO: Implementar configuración de recurrencia
        return null;
    }

    @Override
    public RecurrenceRule updateRecurrence(Long id, RecurrenceRule recurrenceRule) {
        // TODO: Implementar actualización de recurrencia
        return null;
    }

    @Override
    public boolean validateRecurrence(RecurrenceRule recurrenceRule) {
        // TODO: Implementar validación de recurrencia
        return false;
    }
}

