
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.OptimizationRunRequest;
import com.thesis.thesis_scheduler.dto.OptimizationRunResponse;
import java.util.List;

public interface OptimizationRunService {

    OptimizationRunResponse create(OptimizationRunRequest request);

    OptimizationRunResponse getById(Long id);

    List<OptimizationRunResponse> getAll();

    OptimizationRunResponse update(
            Long id,
            OptimizationRunRequest request);

    void delete(Long id);
}
