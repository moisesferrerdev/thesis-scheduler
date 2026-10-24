
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.ProgramRequest;
import com.thesis.thesis_scheduler.dto.ProgramResponse;
import java.util.List;

public interface ProgramService {

    ProgramResponse create(ProgramRequest request);

    ProgramResponse getById(Long id);

    List<ProgramResponse> getAll();

    ProgramResponse update(Long id, ProgramRequest request);

    void delete(Long id);
}
