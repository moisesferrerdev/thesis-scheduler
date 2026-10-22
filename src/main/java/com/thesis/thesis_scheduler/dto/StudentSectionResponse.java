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
public class StudentSectionResponse {

    private Long sectionId;

    private Long termId;
    private String termName;

    private Long programId;
    private String programCode;
    private String programName;

    private String sectionCode;
    private Short yearLevel;
    private Integer studentCount;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}