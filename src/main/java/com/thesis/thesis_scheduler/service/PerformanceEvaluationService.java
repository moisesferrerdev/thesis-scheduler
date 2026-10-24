
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.PerformanceEvaluationRequest;
import com.thesis.thesis_scheduler.dto.PerformanceEvaluationResponse;
import java.util.List;

public interface PerformanceEvaluationService {

    PerformanceEvaluationResponse create(
            PerformanceEvaluationRequest request);

    PerformanceEvaluationResponse getById(Long id);

    List<PerformanceEvaluationResponse> getAll();

    PerformanceEvaluationResponse update(
            Long id,
            PerformanceEvaluationRequest request);

    void delete(Long id);
}
