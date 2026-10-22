package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SectionSubjectRequest {

    @NotNull
    private Long sectionId;

    @NotNull
    private Long offeringId;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal requiredLectureHours;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal requiredLabHours;
}