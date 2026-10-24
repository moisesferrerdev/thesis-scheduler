
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.InstructorQualificationRequest;
import com.thesis.thesis_scheduler.dto.InstructorQualificationResponse;
import java.util.List;

public interface InstructorQualificationService {

    InstructorQualificationResponse create(
            InstructorQualificationRequest request);

    InstructorQualificationResponse getById(Long id);

    List<InstructorQualificationResponse> getAll();

    InstructorQualificationResponse update(
            Long id,
            InstructorQualificationRequest request);

    void delete(Long id);
}
