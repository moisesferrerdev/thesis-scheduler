package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "subject",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_subject_subject_code",
                        columnNames = "subject_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_code", nullable = false, length = 30, unique = true)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "lecture_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal lectureHours;

    @Column(name = "laboratory_hours", nullable = false, precision = 4, scale = 1)
    private BigDecimal laboratoryHours;

    @Column(name = "units", nullable = false, precision = 4, scale = 1)
    private BigDecimal units;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}