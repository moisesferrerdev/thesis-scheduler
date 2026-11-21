package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "optimization_generation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_optimization_generation_run_number",
                        columnNames = {
                                "optimization_run_id",
                                "generation_number"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OptimizationGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "optimization_generation_id")
    private Long optimizationGenerationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "optimization_run_id", nullable = false)
    private OptimizationRun optimizationRun;

    @Column(name = "generation_number", nullable = false)
    private Integer generationNumber;

    @Column(name = "best_fitness", precision = 12, scale = 8)
    private BigDecimal bestFitness;

    @Column(name = "average_fitness", precision = 12, scale = 8)
    private BigDecimal averageFitness;

    @Column(name = "feasible_candidate_count", nullable = false)
    private Integer feasibleCandidateCount;

    @Column(name = "evaluated_candidate_count", nullable = false)
    private Integer evaluatedCandidateCount;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = OffsetDateTime.now();
        }
    }
}