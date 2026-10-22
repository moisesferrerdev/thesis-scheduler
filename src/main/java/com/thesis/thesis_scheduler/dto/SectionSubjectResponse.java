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
public class SectionSubjectResponse {

    private Long sectionSubjectId;

    private Long sectionId;
    private String sectionCode;

    private Long offeringId;
    private String offeringCode;

    private BigDecimal requiredLectureHours;
    private BigDecimal requiredLabHours;

    private OffsetDateTime createdAt;
}