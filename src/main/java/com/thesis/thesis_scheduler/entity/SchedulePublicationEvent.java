package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "schedule_publication_event",
        indexes = {
                @Index(
                        name = "idx_publication_event_official_schedule",
                        columnList = "official_schedule_id,event_at"
                ),
                @Index(
                        name = "idx_publication_event_schedule_version",
                        columnList = "schedule_version_id"
                ),
                @Index(
                        name = "idx_publication_event_related",
                        columnList = "related_event_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SchedulePublicationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publication_event_id")
    private Long publicationEventId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "official_schedule_id", nullable = false)
    private OfficialSchedule officialSchedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_version_id", nullable = false)
    private ScheduleVersion scheduleVersion;

    @Column(name = "event_type", nullable = false, length = 20)
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_event_id")
    private SchedulePublicationEvent relatedEvent;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "event_at", nullable = false)
    private OffsetDateTime eventAt;

    @PrePersist
    protected void onCreate() {
        if (eventAt == null) {
            eventAt = OffsetDateTime.now();
        }
    }
}