
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
public class SubjectRequest {

    @NotBlank
    @Size(max = 30)
    private String subjectCode;

    @NotBlank
    @Size(max = 150)
    private String subjectName;

    private String description;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal lectureHours;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal laboratoryHours;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal units;

    @NotBlank
    @Size(max = 20)
    private String status;
}
