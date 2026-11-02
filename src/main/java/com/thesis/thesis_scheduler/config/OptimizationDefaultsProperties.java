
package com.thesis.thesis_scheduler.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "optimization.defaults")
public class OptimizationDefaultsProperties {

    // Genetic Algorithm settings
    private Integer tournamentSize = 3;

    private Double targetFitness = 0.95;

    // Objective function weights
    private Double cvWeight = 0.25;

    private Double wbWeight = 0.25;

    private Double rpWeight = 0.25;

    private Double spWeight = 0.25;

    // Soft constraint penalty rates
    private Integer sc3PenaltyPerGapHour = 10;

    private Integer sc4PenaltyPerGapHour = 10;

    private Integer sc5PenaltyPerImbalanceHour = 10;
}
