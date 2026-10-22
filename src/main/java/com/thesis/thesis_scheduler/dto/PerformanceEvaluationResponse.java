package com.thesis.thesis_scheduler.dto;

import com.fasterxml.jackson.databind.JsonNode;
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
public class PerformanceEvaluationResponse {

    private Long evaluationId;

    private Long generatedScheduleId;
    private Long scheduleVersionId;

    private BigDecimal hardConstraintScore;
    private BigDecimal softConstraintScore;
    private BigDecimal fitnessScore;

    private JsonNode evaluationDetails;

    private OffsetDateTime evaluatedAt;
}