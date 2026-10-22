package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorQualificationResponse {

    private Long qualificationId;

    private Long instructorId;
    private String employeeNo;
    private String instructorName;

    private Long subjectId;
    private String subjectCode;
    private String subjectName;

    private String qualificationLevel;
    private Boolean approved;

    private OffsetDateTime createdAt;
}