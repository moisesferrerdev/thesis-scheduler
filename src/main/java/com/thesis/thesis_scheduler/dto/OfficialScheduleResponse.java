package com.thesis.thesis_scheduler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OfficialScheduleResponse {

    private Long officialScheduleId;

    private Long termId;
    private String termName;

    private Long currentVersionId;
    private Integer currentVersionNumber;

    private Long publishedVersionId;

    private Long sourceGeneratedScheduleId;
    private Integer sourceCandidateNumber;

    private String scheduleStatus;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}