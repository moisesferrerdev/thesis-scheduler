
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "official_schedule",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_official_schedule_term",
                        columnNames = "term_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OfficialSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "official_schedule_id")
    private Long officialScheduleId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, unique = true)
    private AcademicTerm term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id")
    private ScheduleVersion currentVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_version_id")
    private ScheduleVersion publishedVersion;

    @Column(name = "schedule_status", nullable = false, length = 20)
    private String scheduleStatus = "DRAFT";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
