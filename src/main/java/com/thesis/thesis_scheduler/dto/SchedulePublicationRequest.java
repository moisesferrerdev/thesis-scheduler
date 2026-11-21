package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchedulePublicationRequest {

    @Size(max = 100)
    private String performedBy;

    @Size(max = 500)
    private String remarks;
}