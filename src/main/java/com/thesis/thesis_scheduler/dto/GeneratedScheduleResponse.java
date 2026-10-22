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
public class GeneratedScheduleResponse {

    private Long generatedScheduleId;

    private Long optimizationRunId;
    private Integer candidateNumber;

    private BigDecimal fitnessScore;
    private Integer hardConstraintViolations;
    private BigDecimal softConstraintPenalty;
    private Boolean feasible;

    private OffsetDateTime createdAt;
}