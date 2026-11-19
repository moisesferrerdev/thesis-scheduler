package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.RoomClassificationRequest;
import com.thesis.thesis_scheduler.dto.RoomClassificationResponse;

import java.util.List;

public interface RoomClassificationService {

    RoomClassificationResponse create(RoomClassificationRequest request);

    RoomClassificationResponse getById(Long id);

    List<RoomClassificationResponse> getAll(boolean activeOnly);

    RoomClassificationResponse update(
            Long id,
            RoomClassificationRequest request
    );

    void deactivate(Long id);
}