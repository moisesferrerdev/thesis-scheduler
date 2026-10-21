package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.PerformanceEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceEvaluationRepository
        extends JpaRepository<PerformanceEvaluation, Long> {

    List<PerformanceEvaluation>
    findByGeneratedSchedule_GeneratedScheduleId(
            Long generatedScheduleId
    );

    List<PerformanceEvaluation>
    findByScheduleVersion_ScheduleVersionId(
            Long scheduleVersionId
    );
}