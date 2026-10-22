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
public class OptimizationRunResponse {

    private Long optimizationRunId;

    private Long termId;
    private String termName;

    private String runName;
    private String algorithmName;
    private Integer populationSize;
    private Integer generationLimit;
    private BigDecimal mutationRate;
    private BigDecimal crossoverRate;

    private String runStatus;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;
}