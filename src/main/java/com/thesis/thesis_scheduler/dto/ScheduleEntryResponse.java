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
public class ScheduleEntryResponse {

    private Long scheduleEntryId;

    private Long scheduleVersionId;
    private Integer versionNumber;

    private Long assignmentId;
    private String instructorName;
    private String sectionCode;
    private String offeringCode;

    private Long roomId;
    private String roomCode;

    private Long timeslotId;
    private Short dayOfWeek;
    private String startTime;
    private String endTime;
    private String slotLabel;

    private OffsetDateTime createdAt;
}