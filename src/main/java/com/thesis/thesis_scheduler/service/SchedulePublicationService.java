package com.thesis.thesis_scheduler.service;

import com.thesis.thesis_scheduler.dto.SchedulePublicationEventResponse;
import com.thesis.thesis_scheduler.dto.SchedulePublicationRequest;
import com.thesis.thesis_scheduler.dto.ScheduleWithdrawalRequest;

import java.util.List;

public interface SchedulePublicationService {

    SchedulePublicationEventResponse publish(
            Long officialScheduleId,
            SchedulePublicationRequest request
    );

    SchedulePublicationEventResponse withdraw(
            Long officialScheduleId,
            ScheduleWithdrawalRequest request
    );

    List<SchedulePublicationEventResponse> getPublicationHistory(
            Long officialScheduleId
    );
}