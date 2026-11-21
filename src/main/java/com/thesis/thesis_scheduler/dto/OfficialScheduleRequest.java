package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfficialScheduleRequest {

    @NotNull
    @Positive
    private Long termId;

    @NotNull
    @Positive
    private Long generatedScheduleId;

    @Size(max = 100)
    private String createdBy;

    @Size(max = 500)
    private String changeSummary;
}