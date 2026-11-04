package com.thesis.thesis_scheduler.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ManualScheduleVersionRequest {

    @NotNull
    private Long parentVersionId;

    @NotBlank
    @Size(max = 100)
    private String changeSummary;

    @Size(max = 100)
    private String createdBy;

    @NotEmpty
    @Valid
    private List<ManualScheduleEntryRequest> entries;
}