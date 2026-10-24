
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.StudentSectionRequest;
import com.thesis.thesis_scheduler.dto.StudentSectionResponse;
import java.util.List;

public interface StudentSectionService {

    StudentSectionResponse create(StudentSectionRequest request);

    StudentSectionResponse getById(Long id);

    List<StudentSectionResponse> getAll();

    StudentSectionResponse update(Long id, StudentSectionRequest request);

    void delete(Long id);
}
