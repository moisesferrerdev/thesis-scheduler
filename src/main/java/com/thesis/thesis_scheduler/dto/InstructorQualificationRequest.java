package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InstructorQualificationRequest {

    @NotNull
    private Long instructorId;

    @NotNull
    private Long subjectId;

    @Size(max = 50)
    private String qualificationLevel;

    @NotNull
    private Boolean approved;
}