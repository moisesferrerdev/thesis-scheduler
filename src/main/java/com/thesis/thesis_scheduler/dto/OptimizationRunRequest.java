package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class OptimizationRunRequest {

    @NotNull
    private Long termId;

    @NotBlank
    @Size(max = 100)
    private String runName;

    @NotBlank
    @Size(max = 50)
    private String algorithmName;

    @NotNull
    @Min(5)
    private Integer populationSize;

    @NotNull
    @Min(1)
    private Integer generationLimit;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal mutationRate;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal crossoverRate;
}