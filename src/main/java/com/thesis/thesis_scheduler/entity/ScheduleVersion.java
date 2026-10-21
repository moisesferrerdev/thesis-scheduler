
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "schedule_version",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_schedule_version_schedule_version",
                        columnNames = {"generated_schedule_id", "version_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ScheduleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_version_id")
    private Long scheduleVersionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generated_schedule_id", nullable = false)
    private GeneratedSchedule generatedSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_version_id")
    private ScheduleVersion parentVersion;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "version_type", nullable = false, length = 20)
    private String versionType;

    @Column(name = "version_status", nullable = false, length = 20)
    private String versionStatus;

    @Column(name = "change_summary", columnDefinition = "text")
    private String changeSummary;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
