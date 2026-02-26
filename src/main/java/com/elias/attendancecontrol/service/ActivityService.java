package com.elias.attendancecontrol.service;
import com.elias.attendancecontrol.model.entity.Activity;
import com.elias.attendancecontrol.model.entity.ActivityStatus;

import java.util.List;
public interface ActivityService {
    Activity createActivity(Activity activity);
    Activity updateActivity(Long id, Activity activity);
    void activateActivity(Long id);
    List<Activity> listActivitiesSorted();
    Activity getActivityById(Long id);
    List<Activity> findActiveActivities();
    List<Activity> findByResponsible(Long userId);
    void pauseActivity(Long activityId);
    void completeActivity(Long activityId);
    void cancelActivity(Long activityId);
    boolean canPublish(Long activityId);
    boolean canComplete(Long activityId);
    void changeStatus(Long activityId, ActivityStatus newStatus);
    List<Activity> searchActivities(String query, Long userId, String role);
    boolean isResponsible(Long activityId, Long userId);
    long countAll();
    List<Activity> findAllByUserResponsibleAndEnrolled(long userId);
}
