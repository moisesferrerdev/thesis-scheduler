package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleEntryRequest {

    @NotNull
    private Long scheduleVersionId;

    @NotNull
    private Long assignmentId;

    @NotNull
    private Long roomId;

    @NotNull
    private Long timeslotId;

    @NotBlank
    @Pattern(regexp = "LECTURE|LABORATORY",
            message = "Session type must be LECTURE or LABORATORY")
    private String sessionType;
}