package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.GeneratedSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GeneratedScheduleRepository
        extends JpaRepository<GeneratedSchedule, Long> {

    List<GeneratedSchedule>
    findByOptimizationRun_OptimizationRunId(
            Long optimizationRunId
    );

    List<GeneratedSchedule>
    findByOptimizationRun_OptimizationRunIdAndFeasible(
            Long optimizationRunId,
            Boolean feasible
    );

    Optional<GeneratedSchedule>
    findByOptimizationRun_OptimizationRunIdAndCandidateNumber(
            Long optimizationRunId,
            Integer candidateNumber
    );
}