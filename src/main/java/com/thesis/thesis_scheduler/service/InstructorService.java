
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.InstructorRequest;
import com.thesis.thesis_scheduler.dto.InstructorResponse;
import java.util.List;

public interface InstructorService {

    InstructorResponse create(InstructorRequest request);

    InstructorResponse getById(Long id);

    List<InstructorResponse> getAll();

    InstructorResponse update(Long id, InstructorRequest request);

    void delete(Long id);
}
