package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.ScheduleVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleVersionRepository
        extends JpaRepository<ScheduleVersion, Long> {

    List<ScheduleVersion>
    findByGeneratedSchedule_GeneratedScheduleIdOrderByVersionNumberAsc(
            Long generatedScheduleId
    );

    Optional<ScheduleVersion>
    findByGeneratedSchedule_GeneratedScheduleIdAndVersionNumber(
            Long generatedScheduleId,
            Integer versionNumber
    );

    List<ScheduleVersion>
    findByParentVersion_ScheduleVersionId(
            Long parentVersionId
    );

    List<ScheduleVersion> findByVersionStatus(String versionStatus);
}