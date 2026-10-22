package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotNull;
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
}