package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "schedule_entry")
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_entry_id")
    private Long scheduleEntryId;

    @Column(name = "schedule_version_id")
    private Long scheduleVersionId;

    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "timeslot_id")
    private Long timeslotId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
