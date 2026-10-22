package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorAvailabilityResponse {

    private Long availabilityId;

    private Long instructorId;
    private String employeeNo;
    private String instructorName;

    private Long termId;
    private String termName;

    private Short dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String availabilityType;

    private OffsetDateTime createdAt;
}