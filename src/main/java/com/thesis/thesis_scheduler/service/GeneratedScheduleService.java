
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleRequest;
import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;
import java.util.List;

public interface GeneratedScheduleService {

    GeneratedScheduleResponse create(GeneratedScheduleRequest request);

    GeneratedScheduleResponse getById(Long id);

    List<GeneratedScheduleResponse> getAll();

    GeneratedScheduleResponse update(
            Long id,
            GeneratedScheduleRequest request);

    void delete(Long id);
}
