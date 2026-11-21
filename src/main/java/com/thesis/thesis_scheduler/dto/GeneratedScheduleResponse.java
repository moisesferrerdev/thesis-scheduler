package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedScheduleResponse {

    private Long generatedScheduleId;

    private Long optimizationRunId;
    private Integer candidateNumber;

    // Existing candidate information
    private BigDecimal fitnessScore;
    private Integer hardConstraintViolations;
    private BigDecimal softConstraintPenalty;
    private Boolean feasible;

    private OffsetDateTime createdAt;

    // Candidate evaluation details
    private CandidateEvaluationResponse evaluation;

    // Candidate timetable entries
    private List<GeneratedScheduleEntryResponse> entries =
            new ArrayList<>();
}