
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.SchedulingConstraintRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConstraintResponse;
import java.util.List;

public interface SchedulingConstraintService {

    SchedulingConstraintResponse create(
            SchedulingConstraintRequest request);

    SchedulingConstraintResponse getById(Long id);

    List<SchedulingConstraintResponse> getAll();

    SchedulingConstraintResponse update(
            Long id,
            SchedulingConstraintRequest request);

    void delete(Long id);
}
