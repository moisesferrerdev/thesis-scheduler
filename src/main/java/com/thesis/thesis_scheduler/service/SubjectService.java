
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.SubjectRequest;
import com.thesis.thesis_scheduler.dto.SubjectResponse;
import java.util.List;

public interface SubjectService {

    SubjectResponse create(SubjectRequest request);

    SubjectResponse getById(Long id);

    List<SubjectResponse> getAll();

    SubjectResponse update(Long id, SubjectRequest request);

    void delete(Long id);
}
