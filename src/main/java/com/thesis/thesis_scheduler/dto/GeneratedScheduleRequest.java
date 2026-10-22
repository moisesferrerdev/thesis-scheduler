package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeneratedScheduleRequest {

    @NotNull
    private Long optimizationRunId;

    @NotNull
    @Min(1)
    private Integer candidateNumber;
}