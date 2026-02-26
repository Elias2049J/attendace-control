package com.elias.attendancecontrol.service.implementation;

import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.entity.Activity;
import com.elias.attendancecontrol.model.entity.Organization;
import com.elias.attendancecontrol.model.entity.Session;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.persistence.repository.ActivityRepository;
import com.elias.attendancecontrol.persistence.repository.SessionRepository;
import com.elias.attendancecontrol.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;
    private final SecurityUtils securityUtils;
    private final EnrollmentService enrollmentService;
    private final SessionService sessionService;
    private final ActivityService activityService;

    @Override
    @Transactional(readOnly = true)
    public List<Session> getCalendarView(LocalDate startDate, LocalDate endDate) {
        Optional<Organization> organizationOptional = securityUtils.getCurrentOrganization();
        User user = securityUtils.getCurrentUserOrThrow();
        Set<Session> sessionSet = new LinkedHashSet<>();

        if (organizationOptional.isPresent()) {
            Organization org = organizationOptional.get();
            log.debug("Getting calendar view for organization: {} from {} to {}", org.getSlug(), startDate, endDate);

            if (securityUtils.isOrganizationOwnerOrAdmin()) {
                sessionSet.addAll(getCalendarForOwnerOrAdmin(startDate, endDate, org));
            } else {
                sessionSet.addAll(getCalendarForMember(startDate, endDate, user));
            }
        } else {
            log.debug("No organization context, getting all sessions from {} to {}", startDate, endDate);
            sessionSet.addAll(sessionRepository.findBySessionDateBetweenOrderBySessionDateAsc(startDate, endDate));
        }

        return sessionSet.stream()
                .sorted(Comparator.comparing(Session::getSessionDate))
                .collect(Collectors.toList());
    }

    private List<Session> getCalendarForOwnerOrAdmin(LocalDate startDate, LocalDate endDate, Organization org) {
        return sessionRepository.findBySessionDateBetweenAndActivityOrganizationId(startDate, endDate, org.getId());
    }

    private List<Session> getCalendarForMember(LocalDate startDate, LocalDate endDate, User user) {
        List<Activity> activitiesFromUser = activityService.findAllByUserResponsibleAndEnrolled(user.getId());
        return activitiesFromUser.stream()
                .flatMap(a -> a.getSessions().stream())
                .filter(s -> !s.getSessionDate().isBefore(startDate) && !s.getSessionDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByDateRange(LocalDate startDate, LocalDate endDate) {
        return securityUtils.getCurrentOrganizationId()
                .map(orgId -> {
                    log.debug("Getting activities for organization: {} between {} and {}", orgId, startDate, endDate);
                    return activityRepository.findByOrganizationIdAndSessionsDateBetween(orgId, startDate, endDate);
                })
                .orElseGet(() -> {
                    log.debug("No organization context, getting all activities by date range from {} to {}", startDate, endDate);
                    return activityRepository.findBySessionsDateBetween(startDate, endDate);
                });
    }
}
