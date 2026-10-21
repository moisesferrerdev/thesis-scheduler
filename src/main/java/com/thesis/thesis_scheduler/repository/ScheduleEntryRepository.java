package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleEntryRepository
        extends JpaRepository<ScheduleEntry, Long> {

    List<ScheduleEntry>
    findByScheduleVersion_ScheduleVersionId(
            Long scheduleVersionId
    );

    List<ScheduleEntry>
    findByScheduleVersion_ScheduleVersionIdAndRoom_RoomId(
            Long scheduleVersionId,
            Long roomId
    );

    List<ScheduleEntry>
    findByScheduleVersion_ScheduleVersionIdAndTimeslot_TimeslotId(
            Long scheduleVersionId,
            Long timeslotId
    );

    List<ScheduleEntry>
    findByAssignment_AssignmentId(
            Long assignmentId
    );
}