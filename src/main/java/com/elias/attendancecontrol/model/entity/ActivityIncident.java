package com.elias.attendancecontrol.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_exceptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Activity (many-to-one)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "original_date", nullable = false)
    private LocalDate originalDate;

    @Column(name = "new_date")
    private LocalDate newDate;

    @Column(name = "cancelled", nullable = false)
    private Boolean cancelled = false;

    @Column(name = "reason", length = 500)
    private String reason;

    // Relación con User (many-to-one)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdByUser;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}

