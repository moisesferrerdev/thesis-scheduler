package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;

import java.util.List;

public interface GeneratedScheduleService {

    GeneratedScheduleResponse getById(Long id);

    List<GeneratedScheduleResponse> getAll();

    List<GeneratedScheduleResponse> getByOptimizationRunId(
            Long optimizationRunId
    );
}