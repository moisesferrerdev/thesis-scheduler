package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SchedulingConfigRequest {

    @NotNull
    private Long termId;

    @NotEmpty
    private List<@NotNull Integer> operatingDays;

    @NotNull
    private LocalTime openingTime;

    @NotNull
    private LocalTime closingTime;

    @NotNull
    private Integer timeslotIntervalMinutes;
}