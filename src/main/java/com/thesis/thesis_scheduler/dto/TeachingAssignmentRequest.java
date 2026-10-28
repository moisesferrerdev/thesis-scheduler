package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TeachingAssignmentRequest {

    @NotNull
    private Long sectionSubjectId;

    @NotNull
    private Long instructorId;

    @NotNull
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Assigned load hours must be greater than zero"
    )
    @Digits(integer = 4, fraction = 1)
    private BigDecimal assignedLoadHours;

    @NotBlank
    @Size(max = 20)
    private String assignmentStatus;
}