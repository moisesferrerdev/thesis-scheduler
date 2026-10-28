
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "generated_schedule",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_generated_schedule_run_candidate",
                        columnNames = {"optimization_run_id", "candidate_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class GeneratedSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generated_schedule_id")
    private Long generatedScheduleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "optimization_run_id", nullable = false)
    private OptimizationRun optimizationRun;

    @Column(name = "candidate_number", nullable = false)
    private Integer candidateNumber;

    @Column(name = "fitness_score", precision = 12, scale = 4)
    private BigDecimal fitnessScore;

    @Column(name = "hard_constraint_violations", nullable = false)
    private Integer hardConstraintViolations;

    @Column(
            name = "soft_constraint_penalty",
            precision = 12,
            scale = 4
    )
    private BigDecimal softConstraintPenalty;

    @Column(name = "is_feasible", nullable = false)
    private Boolean feasible;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
