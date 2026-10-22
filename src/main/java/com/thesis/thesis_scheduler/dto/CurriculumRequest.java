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
public class CurriculumRequest {

    @NotNull
    private Long programId;

    @NotNull
    private Long subjectId;

    @NotBlank
    @Size(max = 20)
    private String curriculumYear;

    @NotNull
    private Short yearLevel;

    @NotBlank
    @Size(max = 30)
    private String semester;

    @NotNull
    private Boolean required;
}