package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "academic_term")
@Getter
@Setter
@NoArgsConstructor
public class AcademicTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long termId;

    @Column(name = "term_name", nullable = false, length = 100)
    private String termName;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "semester", nullable = false, length = 30)
    private String semester;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}