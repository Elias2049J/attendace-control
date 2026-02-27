package com.elias.attendancecontrol.service;
import com.elias.attendancecontrol.model.dto.ActivityUserReportDTO;
import com.elias.attendancecontrol.model.dto.ActivityAttendanceSummaryDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
public interface ReportService {
    Map<String, Object> generateActivityReport(Long activityId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> generateUserReport(Long userId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> generateActivitiesUsersGeneralReport(LocalDate startDate, LocalDate endDate);
    Map<String, Object> generateLogsReport(LocalDate startDate, LocalDate endDate);
    double calculateAttendanceRate(Long activityId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> calculateStatistics(Long activityId);
    Map<String, Object> aggregateData(Long activityId, LocalDate startDate, LocalDate endDate);
    Map<String, Object> generateEnrollmentAttendanceReport(Long activityId);
    Map<String, Object> getParticipantStatistics(Long activityId, Long userId);
}
