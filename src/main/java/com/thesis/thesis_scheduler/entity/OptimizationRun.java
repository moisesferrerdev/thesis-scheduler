
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "optimization_run")
@Getter
@Setter
@NoArgsConstructor
public class OptimizationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "optimization_run_id")
    private Long optimizationRunId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(name = "run_name", nullable = false, length = 100)
    private String runName;

    @Column(name = "algorithm_name", nullable = false, length = 50)
    private String algorithmName;

    @Column(name = "population_size", nullable = false)
    private Integer populationSize;

    @Column(name = "generation_limit", nullable = false)
    private Integer generationLimit;

    @Column(
            name = "mutation_rate",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal mutationRate;

    @Column(
            name = "crossover_rate",
            nullable = false,
            precision = 6,
            scale = 5
    )
    private BigDecimal crossoverRate;

    @Column(name = "run_status", nullable = false, length = 20)
    private String runStatus;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
