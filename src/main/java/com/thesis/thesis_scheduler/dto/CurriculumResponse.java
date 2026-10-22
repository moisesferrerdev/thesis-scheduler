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
public class CurriculumResponse {

    private Long curriculumId;

    private Long programId;
    private String programCode;
    private String programName;

    private Long subjectId;
    private String subjectCode;
    private String subjectName;

    private String curriculumYear;
    private Short yearLevel;
    private String semester;
    private Boolean required;

    private OffsetDateTime createdAt;
}