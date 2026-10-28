package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "curriculum",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_curriculum_program_subject_year",
                        columnNames = {
                                "program_id",
                                "subject_id",
                                "curriculum_year"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id")
    private Long curriculumId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "curriculum_year", nullable = false, length = 20)
    private String curriculumYear;

    @Column(name = "year_level", nullable = false)
    private Short yearLevel;

    @Column(name = "semester", nullable = false, length = 30)
    private String semester;

    @Column(name = "is_required", nullable = false)
    private Boolean required;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}