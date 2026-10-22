package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectOfferingRequest {

    @NotNull
    private Long termId;

    @NotNull
    private Long curriculumId;

    @NotNull
    private Long subjectId;

    @NotBlank
    @Size(max = 40)
    private String offeringCode;

    @NotBlank
    @Size(max = 20)
    private String status;
}