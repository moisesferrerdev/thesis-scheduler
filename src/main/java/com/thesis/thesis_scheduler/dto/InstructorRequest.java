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
public class InstructorRequest {

    @NotBlank
    @Size(max = 30)
    private String employeeNo;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Size(max = 100)
    private String middleName;

    @NotBlank
    @Size(max = 30)
    private String employmentType;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 4, fraction = 1)
    private BigDecimal maxLoadHours;

    @NotBlank
    @Size(max = 20)
    private String status;
}