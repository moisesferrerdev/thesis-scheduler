
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "instructor_qualification",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_instructor_qualification_instructor_subject",
                        columnNames = {"instructor_id", "subject_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class InstructorQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_id")
    private Long qualificationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "qualification_level", length = 50)
    private String qualificationLevel;

    @Column(name = "is_approved", nullable = false)
    private Boolean approved;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
