package com.elias.attendancecontrol.persistence.repository;

import com.elias.attendancecontrol.model.entity.ActivityException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityExceptionRepository extends JpaRepository<Long, ActivityException> {
}
