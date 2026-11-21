package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.OptimizationGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptimizationGenerationRepository
        extends JpaRepository<OptimizationGeneration, Long> {

    List<OptimizationGeneration>
    findByOptimizationRun_OptimizationRunIdOrderByGenerationNumberAsc(
            Long optimizationRunId
    );
}