package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleWithdrawalRequest {

    @NotBlank
    @Size(max = 500)
    private String remarks;

    @Size(max = 100)
    private String performedBy;
}