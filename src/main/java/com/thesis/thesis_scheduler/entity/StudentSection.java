package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "student_section",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_section_term_code",
                        columnNames = {"term_id", "section_code"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StudentSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm academicTerm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "section_code", nullable = false, length = 30)
    private String sectionCode;

    @Column(name = "year_level", nullable = false)
    private Short yearLevel;

    @Column(name = "student_count", nullable = false)
    private Integer studentCount = 0;

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