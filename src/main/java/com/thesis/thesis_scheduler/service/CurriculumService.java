
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.CurriculumRequest;
import com.thesis.thesis_scheduler.dto.CurriculumResponse;
import java.util.List;

public interface CurriculumService {

    CurriculumResponse create(CurriculumRequest request);

    CurriculumResponse getById(Long id);

    List<CurriculumResponse> getAll();

    CurriculumResponse update(Long id, CurriculumRequest request);

    void delete(Long id);
}
