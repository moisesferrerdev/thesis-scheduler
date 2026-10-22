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
public class SubjectOfferingResponse {

    private Long offeringId;

    private Long termId;
    private String termName;

    private Long curriculumId;
    private String curriculumYear;

    private Long subjectId;
    private String subjectCode;
    private String subjectName;

    private String offeringCode;
    private String status;

    private OffsetDateTime createdAt;
}