
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.ScheduleEntryRequest;
import com.thesis.thesis_scheduler.dto.ScheduleEntryResponse;
import java.util.List;

public interface ScheduleEntryService {

    ScheduleEntryResponse create(ScheduleEntryRequest request);

    ScheduleEntryResponse getById(Long id);

    List<ScheduleEntryResponse> getAll();

    ScheduleEntryResponse update(
            Long id,
            ScheduleEntryRequest request);

    void delete(Long id);
}
