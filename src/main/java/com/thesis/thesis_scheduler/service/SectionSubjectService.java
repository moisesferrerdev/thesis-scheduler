
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.SectionSubjectRequest;
import com.thesis.thesis_scheduler.dto.SectionSubjectResponse;
import java.util.List;

public interface SectionSubjectService {

    SectionSubjectResponse create(SectionSubjectRequest request);

    SectionSubjectResponse getById(Long id);

    List<SectionSubjectResponse> getAll();

    SectionSubjectResponse update(Long id, SectionSubjectRequest request);

    void delete(Long id);
}
