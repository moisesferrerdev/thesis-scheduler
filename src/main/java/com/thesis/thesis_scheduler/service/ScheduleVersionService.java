
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.ScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionResponse;
import java.util.List;

public interface ScheduleVersionService {

    ScheduleVersionResponse create(ScheduleVersionRequest request);

    ScheduleVersionResponse getById(Long id);

    List<ScheduleVersionResponse> getAll();

    ScheduleVersionResponse update(
            Long id,
            ScheduleVersionRequest request);

    void delete(Long id);
}
