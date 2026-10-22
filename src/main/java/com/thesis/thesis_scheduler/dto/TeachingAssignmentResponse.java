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
public class TeachingAssignmentResponse {

    private Long assignmentId;

    private Long sectionSubjectId;
    private String sectionCode;
    private String offeringCode;

    private Long instructorId;
    private String employeeNo;
    private String instructorName;

    private BigDecimal assignedLoadHours;
    private String assignmentStatus;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}