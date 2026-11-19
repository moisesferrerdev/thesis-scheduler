package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.SchedulingConfigRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConfigResponse;

import java.util.List;

public interface SchedulingConfigService {

    List<SchedulingConfigResponse> getAllConfigurations();

    SchedulingConfigResponse getConfiguration(Long termId);

    SchedulingConfigResponse saveConfiguration(
            SchedulingConfigRequest request
    );
}