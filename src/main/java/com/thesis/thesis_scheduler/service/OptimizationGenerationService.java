package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.OptimizationGenerationResponse;

import java.util.List;

public interface OptimizationGenerationService {

    List<OptimizationGenerationResponse> getByOptimizationRunId(
            Long optimizationRunId
    );
}