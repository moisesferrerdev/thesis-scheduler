package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentSectionRequest {

    @NotNull
    private Long termId;

    @NotNull
    private Long programId;

    @NotBlank
    @Size(max = 30)
    private String sectionCode;

    @NotNull
    @Min(1)
    private Short yearLevel;

    @NotNull
    @Min(0)
    private Integer studentCount;
}