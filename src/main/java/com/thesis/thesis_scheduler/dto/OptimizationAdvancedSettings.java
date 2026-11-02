
package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OptimizationAdvancedSettings {

    // Genetic Algorithm settings
    @Min(2)
    private Integer tournamentSize;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal targetFitness;

    // Objective function weights
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal cvWeight;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal wbWeight;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal rpWeight;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal spWeight;

    // Soft constraint penalty rates
    @Min(0)
    private Integer sc3PenaltyPerGapHour;

    @Min(0)
    private Integer sc4PenaltyPerGapHour;

    @Min(0)
    private Integer sc5PenaltyPerImbalanceHour;
}
