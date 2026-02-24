package com.elias.attendancecontrol.service;

import com.elias.attendancecontrol.model.entity.Attendance;
import com.elias.attendancecontrol.model.entity.AttendanceStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface AttendanceService {
    Attendance registerAttendance(Long userId, String qrToken);
    List<Attendance> manualRegistrationBatch(Long sessionId, Map<Long, AttendanceStatus> userAttendances);
    List<Attendance> listAttendance();
    boolean validateSession(Long sessionId);
    boolean validateUser(Long userId);
    boolean validateTime(Long sessionId);
    boolean checkDuplicate(Long sessionId, Long userId);
    List<Attendance> getAttendanceBySession(Long sessionId);
    List<Attendance> getAttendanceByUser(Long userId);
    Map<String, Object> getAttendanceCountByUserIdAndActivityId(Long userId, Long activityId);
    List<Attendance> getAttendancesByUserAndActivity(Long userId, Long activityId);
}

