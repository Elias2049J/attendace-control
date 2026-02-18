package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.model.entity.Role;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Override
    public void assignRole(Long userId, Role role) {
        // TODO: Implementar asignación de rol
    }

    @Override
    public boolean validateRole(User user, Role role) {
        // TODO: Implementar validación de rol
        return false;
    }
}

