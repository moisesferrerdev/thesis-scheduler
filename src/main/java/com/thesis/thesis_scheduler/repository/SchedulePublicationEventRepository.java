package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.SchedulePublicationEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchedulePublicationEventRepository
        extends JpaRepository<SchedulePublicationEvent, Long> {

    List<SchedulePublicationEvent>
    findByOfficialSchedule_OfficialScheduleIdOrderByEventAtDescPublicationEventIdDesc(
            Long officialScheduleId
    );

    Optional<SchedulePublicationEvent>
    findTopByOfficialSchedule_OfficialScheduleIdOrderByEventAtDescPublicationEventIdDesc(
            Long officialScheduleId
    );

    List<SchedulePublicationEvent>
    findByScheduleVersion_ScheduleVersionIdOrderByEventAtDescPublicationEventIdDesc(
            Long scheduleVersionId
    );
}