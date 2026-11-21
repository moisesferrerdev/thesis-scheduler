package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedScheduleEntryResponse {

    private Long generatedScheduleEntryId;

    private Long generatedScheduleId;

    private Long assignmentId;
    private String instructorName;
    private String sectionCode;
    private String offeringCode;

    private Long roomId;
    private String roomCode;

    private Long timeslotId;
    private Short dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotLabel;

    private String sessionType;

    private OffsetDateTime createdAt;
}