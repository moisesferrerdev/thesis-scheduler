package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingConfigResponse {

    private Long configId;
    private Long termId;
    private String termName;
    private String academicYear;
    private String semester;
    private List<Integer> operatingDays;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Integer timeslotIntervalMinutes;
}