package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RevertScheduleVersionRequest {

    @NotNull
    @Positive
    private Long sourceVersionId;

    @NotBlank
    @Size(max = 500)
    private String changeSummary;

    @Size(max = 100)
    private String createdBy;
}