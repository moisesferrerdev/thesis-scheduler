package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "optimization_run")
public class OptimizationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "optimization_run_id")
    private Long optimizationRunId;

    @Column(name = "term_id")
    private Long termId;

    @Column(name = "run_name")
    private String runName;

    @Column(name = "algorithm_name")
    private String algorithmName;

    @Column(name = "population_size")
    private Integer populationSize;

    @Column(name = "generation_limit")
    private Integer generationLimit;

    @Column(name = "mutation_rate")
    private BigDecimal mutationRate;

    @Column(name = "crossover_rate")
    private BigDecimal crossoverRate;

    @Column(name = "run_status")
    private String runStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
