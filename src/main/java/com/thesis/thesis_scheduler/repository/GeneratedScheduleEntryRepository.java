package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.GeneratedScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedScheduleEntryRepository
        extends JpaRepository<GeneratedScheduleEntry, Long> {

    List<GeneratedScheduleEntry>
    findByGeneratedSchedule_GeneratedScheduleId(
            Long generatedScheduleId
    );
}