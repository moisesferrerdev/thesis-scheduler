package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class ManualScheduleEntryRequest {

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