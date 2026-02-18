package com.elias.attendancecontrol.persistence.repository;

import com.elias.attendancecontrol.model.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Long, Activity> {
}
