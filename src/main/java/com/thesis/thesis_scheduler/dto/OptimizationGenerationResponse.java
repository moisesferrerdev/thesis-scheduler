package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationGenerationResponse {

    private Long optimizationGenerationId;

    private Long optimizationRunId;

    private Integer generationNumber;

    private BigDecimal bestFitness;
    private BigDecimal averageFitness;

    private Integer feasibleCandidateCount;
    private Integer evaluatedCandidateCount;

    private OffsetDateTime recordedAt;
}