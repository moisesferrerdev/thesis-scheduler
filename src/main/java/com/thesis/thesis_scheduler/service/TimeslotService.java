
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.TimeslotRequest;
import com.thesis.thesis_scheduler.dto.TimeslotResponse;
import java.util.List;

public interface TimeslotService {

    TimeslotResponse create(TimeslotRequest request);

    TimeslotResponse getById(Long id);

    List<TimeslotResponse> getAll();

    TimeslotResponse update(Long id, TimeslotRequest request);

    void delete(Long id);
}
