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
public class ScheduleVersionResponse {

    private Long scheduleVersionId;

    private Long officialScheduleId;
    private Long generatedScheduleId;

    private Long parentVersionId;
    private Long copiedFromVersionId;

    private Integer versionNumber;

    private String versionType;
    private String versionStatus;

    private String changeSummary;
    private String createdBy;

    private OffsetDateTime createdAt;
}