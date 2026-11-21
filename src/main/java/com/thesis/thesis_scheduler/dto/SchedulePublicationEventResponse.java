package com.thesis.thesis_scheduler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SchedulePublicationEventResponse {

    private Long publicationEventId;

    private Long officialScheduleId;

    private Long scheduleVersionId;

    private Integer versionNumber;

    private String eventType;

    private Long relatedEventId;

    private String performedBy;

    private String remarks;

    private OffsetDateTime eventAt;
}