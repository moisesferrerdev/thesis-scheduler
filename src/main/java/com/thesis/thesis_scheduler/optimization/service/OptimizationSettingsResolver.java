
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.config.OptimizationDefaultsProperties;
import com.thesis.thesis_scheduler.dto.OptimizationAdvancedSettings;
import com.thesis.thesis_scheduler.optimization.fitness.FitnessCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OptimizationSettingsResolver {

    private final OptimizationDefaultsProperties defaults;

    public ResolvedOptimizationSettings resolve(
            OptimizationAdvancedSettings advancedSettings) {

        // Genetic Algorithm settings
        Integer tournamentSize = resolveValue(
                advancedSettings == null ? null :
                        advancedSettings.getTournamentSize(),
                defaults.getTournamentSize()
        );

        Double targetFitness = resolveDecimal(
                advancedSettings == null ? null :
                        advancedSettings.getTargetFitness(),
                defaults.getTargetFitness()
        );

        // Objective function weights
        double cvWeight = resolveDecimal(
                advancedSettings == null ? null :
                        advancedSettings.getCvWeight(),
                defaults.getCvWeight()
        );

        double wbWeight = resolveDecimal(
                advancedSettings == null ? null :
                        advancedSettings.getWbWeight(),
                defaults.getWbWeight()
        );

        double rpWeight = resolveDecimal(
                advancedSettings == null ? null :
                        advancedSettings.getRpWeight(),
                defaults.getRpWeight()
        );

        double spWeight = resolveDecimal(
                advancedSettings == null ? null :
                        advancedSettings.getSpWeight(),
                defaults.getSpWeight()
        );

        validateWeights(
                cvWeight,
                wbWeight,
                rpWeight,
                spWeight
        );

        // Soft constraint penalty rates
        Integer sc3Penalty = resolveValue(
                advancedSettings == null ? null :
                        advancedSettings.getSc3PenaltyPerGapHour(),
                defaults.getSc3PenaltyPerGapHour()
        );

        Integer sc4Penalty = resolveValue(
                advancedSettings == null ? null :
                        advancedSettings.getSc4PenaltyPerGapHour(),
                defaults.getSc4PenaltyPerGapHour()
        );

        Integer sc5Penalty = resolveValue(
                advancedSettings == null ? null :
                        advancedSettings.getSc5PenaltyPerImbalanceHour(),
                defaults.getSc5PenaltyPerImbalanceHour()
        );

        validateSettings(
                tournamentSize,
                targetFitness,
                sc3Penalty,
                sc4Penalty,
                sc5Penalty
        );

        FitnessCalculator.ObjectiveWeights weights =
                new FitnessCalculator.ObjectiveWeights(
                        cvWeight,
                        wbWeight,
                        rpWeight,
                        spWeight
                );

        return new ResolvedOptimizationSettings(
                tournamentSize,
                targetFitness,
                weights,
                sc3Penalty,
                sc4Penalty,
                sc5Penalty
        );
    }

    private Integer resolveValue(
            Integer requestedValue,
            Integer defaultValue) {

        return requestedValue != null
                ? requestedValue
                : defaultValue;
    }

    private Double resolveDecimal(
            BigDecimal requestedValue,
            Double defaultValue) {

        return requestedValue != null
                ? requestedValue.doubleValue()
                : defaultValue;
    }

    private void validateWeights(
            double cvWeight,
            double wbWeight,
            double rpWeight,
            double spWeight) {

        double total = cvWeight + wbWeight + rpWeight + spWeight;

        if (Math.abs(total - 1.0) > 0.000001) {
            throw new IllegalArgumentException(
                    "Objective function weights must total exactly 1.0."
            );
        }
    }

    private void validateSettings(
            Integer tournamentSize,
            Double targetFitness,
            Integer sc3Penalty,
            Integer sc4Penalty,
            Integer sc5Penalty) {

        if (tournamentSize == null || tournamentSize < 2) {
            throw new IllegalArgumentException(
                    "Tournament size must be at least 2."
            );
        }

        if (targetFitness == null ||
                !Double.isFinite(targetFitness) ||
                targetFitness < 0.0 ||
                targetFitness > 1.0) {

            throw new IllegalArgumentException(
                    "Target fitness must be between 0.0 and 1.0."
            );
        }

        if (sc3Penalty == null || sc3Penalty < 0 ||
                sc4Penalty == null || sc4Penalty < 0 ||
                sc5Penalty == null || sc5Penalty < 0) {

            throw new IllegalArgumentException(
                    "Soft constraint penalty rates cannot be negative."
            );
        }
    }

    public record ResolvedOptimizationSettings(
            Integer tournamentSize,
            Double targetFitness,
            FitnessCalculator.ObjectiveWeights weights,
            Integer sc3PenaltyPerGapHour,
            Integer sc4PenaltyPerGapHour,
            Integer sc5PenaltyPerImbalanceHour
    ) {
    }
}
