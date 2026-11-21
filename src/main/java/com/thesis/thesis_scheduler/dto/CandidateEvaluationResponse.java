package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateEvaluationResponse {

    private Boolean feasible;

    private Integer hardConstraintViolations;

    private BigDecimal fitnessScore;

    private BigDecimal softConstraintPenalty;
}