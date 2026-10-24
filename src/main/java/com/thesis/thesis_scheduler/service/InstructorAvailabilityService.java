
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.InstructorAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.InstructorAvailabilityResponse;
import java.util.List;

public interface InstructorAvailabilityService {

    InstructorAvailabilityResponse create(
            InstructorAvailabilityRequest request);

    InstructorAvailabilityResponse getById(Long id);

    List<InstructorAvailabilityResponse> getAll();

    InstructorAvailabilityResponse update(
            Long id,
            InstructorAvailabilityRequest request);

    void delete(Long id);
}
