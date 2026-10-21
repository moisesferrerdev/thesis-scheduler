
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "section_subject",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_section_subject_section_offering",
                        columnNames = {"section_id", "offering_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SectionSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_subject_id")
    private Long sectionSubjectId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private StudentSection section;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false)
    private SubjectOffering offering;

    @Column(
            name = "required_lecture_hours",
            nullable = false,
            precision = 4,
            scale = 1
    )
    private BigDecimal requiredLectureHours;

    @Column(
            name = "required_lab_hours",
            nullable = false,
            precision = 4,
            scale = 1
    )
    private BigDecimal requiredLabHours;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
