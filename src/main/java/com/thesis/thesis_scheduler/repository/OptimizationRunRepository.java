package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.OptimizationRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptimizationRunRepository
        extends JpaRepository<OptimizationRun, Long> {

    List<OptimizationRun> findByTerm_TermId(Long termId);

    List<OptimizationRun> findByRunStatus(String runStatus);

    List<OptimizationRun>
    findByTerm_TermIdAndRunStatus(
            Long termId,
            String runStatus
    );
}