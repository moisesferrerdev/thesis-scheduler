package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.OfficialScheduleRequest;
import com.thesis.thesis_scheduler.dto.OfficialScheduleResponse;

import java.util.List;

public interface OfficialScheduleService {

    OfficialScheduleResponse createFromCandidate(
            OfficialScheduleRequest request
    );

    OfficialScheduleResponse getById(Long id);

    OfficialScheduleResponse getByTermId(Long termId);

    List<OfficialScheduleResponse> getAll();
}