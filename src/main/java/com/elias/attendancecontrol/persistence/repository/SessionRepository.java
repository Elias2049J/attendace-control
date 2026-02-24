package com.elias.attendancecontrol.persistence.repository;
import com.elias.attendancecontrol.model.entity.Activity;
import com.elias.attendancecontrol.model.entity.Session;
import com.elias.attendancecontrol.model.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByActivityOrderBySessionDateAsc(Activity activity);
    List<Session> findBySessionDateBetweenOrderBySessionDateAsc(LocalDate start, LocalDate end);
    List<Session> findByActivityAndSessionDateBetween(Activity activity, LocalDate start, LocalDate end);
    Optional<Session> findByActivityAndSessionDate(Activity activity, LocalDate date);
    boolean existsByActivityAndSessionDate(Activity activity, LocalDate date);
    List<Session> findByStatus(SessionStatus status);
    List<Session> findByActivityAndStatus(Activity activity, SessionStatus status);
    long countByActivityAndStatus(Activity activity, SessionStatus status);

    @Query("SELECT s FROM Session s WHERE s.activity.organization.id = :orgId ORDER BY s.sessionDate ASC")
    List<Session> findByActivityOrganizationId(@Param("orgId") Long organizationId);

    @Query("SELECT s FROM Session s WHERE s.sessionDate BETWEEN :startDate AND :endDate " +
           "AND s.activity.organization.id = :orgId ORDER BY s.sessionDate ASC")
    List<Session> findBySessionDateBetweenAndActivityOrganizationId(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("orgId") Long organizationId);

    @Query("SELECT s FROM Session s " +
           "JOIN FETCH s.activity a " +
           "JOIN FETCH a.organization " +
           "WHERE s.id = :id")
    Optional<Session> findByIdWithActivityAndOrganization(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Session s WHERE s.activity.id = :activityId " +
           "AND s.status NOT IN ('CLOSED', 'CANCELLED')")
    boolean existsActiveOrPlannedSessionsByActivityId(@Param("activityId") Long activityId);
}
