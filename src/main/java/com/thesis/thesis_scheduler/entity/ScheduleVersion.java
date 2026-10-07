package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "schedule_version")
public class ScheduleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_version_id")
    private Long scheduleVersionId;

    @Column(name = "generated_schedule_id")
    private Long generatedScheduleId;

    @Column(name = "parent_version_id")
    private Long parentVersionId;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "version_type")
    private String versionType;

    @Column(name = "version_status")
    private String versionStatus;

    @Column(name = "change_summary")
    private String changeSummary;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
