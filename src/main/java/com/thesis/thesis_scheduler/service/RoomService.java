
package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.RoomRequest;
import com.thesis.thesis_scheduler.dto.RoomResponse;
import java.util.List;

public interface RoomService {

    RoomResponse create(RoomRequest request);

    RoomResponse getById(Long id);

    List<RoomResponse> getAll();

    RoomResponse update(Long id, RoomRequest request);

    void delete(Long id);
}
