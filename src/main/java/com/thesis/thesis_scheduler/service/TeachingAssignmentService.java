
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.TeachingAssignmentRequest;
import com.thesis.thesis_scheduler.dto.TeachingAssignmentResponse;
import java.util.List;

public interface TeachingAssignmentService {

    TeachingAssignmentResponse create(TeachingAssignmentRequest request);

    TeachingAssignmentResponse getById(Long id);

    List<TeachingAssignmentResponse> getAll();

    TeachingAssignmentResponse update(
            Long id,
            TeachingAssignmentRequest request);

    void delete(Long id);
}
