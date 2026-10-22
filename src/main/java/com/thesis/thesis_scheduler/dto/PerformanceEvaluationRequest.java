package com.thesis.thesis_scheduler.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceEvaluationRequest {

    private Long generatedScheduleId;

    private Long scheduleVersionId;

    private JsonNode evaluationDetails;

    @AssertTrue(message =
            "Either generatedScheduleId or scheduleVersionId must be provided")
    public boolean isScheduleReferenceProvided() {
        return generatedScheduleId != null
                || scheduleVersionId != null;
    }
}