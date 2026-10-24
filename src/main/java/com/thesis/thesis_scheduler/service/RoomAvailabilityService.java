
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.RoomAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.RoomAvailabilityResponse;
import java.util.List;

public interface RoomAvailabilityService {

    RoomAvailabilityResponse create(RoomAvailabilityRequest request);

    RoomAvailabilityResponse getById(Long id);

    List<RoomAvailabilityResponse> getAll();

    RoomAvailabilityResponse update(
            Long id,
            RoomAvailabilityRequest request);

    void delete(Long id);
}
