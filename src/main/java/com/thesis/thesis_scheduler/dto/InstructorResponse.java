package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorResponse {

    private Long instructorId;

    private String employeeNo;
    private String firstName;
    private String lastName;
    private String middleName;

    private String employmentType;
    private BigDecimal maxLoadHours;
    private String status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}