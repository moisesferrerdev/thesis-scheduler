package com.thesis.thesis_scheduler.dto;

import com.fasterxml.jackson.databind.JsonNode;
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
public class SchedulingConstraintRequest {

    @NotNull
    private Long termId;

    @NotBlank
    @Size(max = 30)
    private String constraintCode;

    @NotBlank
    @Size(max = 150)
    private String constraintName;

    @NotBlank
    @Size(max = 20)
    private String constraintType;

    @NotBlank
    @Size(max = 20)
    private String constraintCategory;

    @DecimalMin("0.0")
    @Digits(integer = 5, fraction = 3)
    private BigDecimal weight;

    private JsonNode configuration;

    @NotNull
    private Boolean active;
}