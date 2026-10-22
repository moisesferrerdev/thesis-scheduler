package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleVersionRequest {

    @NotNull
    private Long generatedScheduleId;

    private Long parentVersionId;

    @NotBlank
    @Size(max = 20)
    private String versionType;

    @Size(max = 100)
    private String changeSummary;

    @Size(max = 100)
    private String createdBy;
}