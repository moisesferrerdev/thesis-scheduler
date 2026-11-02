
package com.thesis.thesis_scheduler.optimization.fitness;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SoftConstraintCalculator {

    private static final Set<String> SUPPORTED_CONSTRAINTS =
            Set.of("SC1", "SC2", "SC3", "SC4", "SC5");

    /*
     * SP = Sum of all individual soft constraint penalties.
     */
    public SoftConstraintResult calculate(
            List<SoftConstraintPenalty> penalties) {

        if (penalties == null) {
            throw new IllegalArgumentException(
                    "Soft constraint penalties must not be null.");
        }

        Map<String, Integer> penaltyBreakdown =
                new LinkedHashMap<>();

        for (String constraintCode : SUPPORTED_CONSTRAINTS) {
            penaltyBreakdown.put(constraintCode, 0);
        }

        for (SoftConstraintPenalty penalty : penalties) {

            if (penalty == null) {
                throw new IllegalArgumentException(
                        "Soft constraint penalty must not be null.");
            }

            String constraintCode = penalty.constraintCode();

            if (constraintCode == null
                    || !SUPPORTED_CONSTRAINTS.contains(constraintCode)) {
                throw new IllegalArgumentException(
                        "Unsupported soft constraint code: "
                                + constraintCode);
            }

            if (penalty.penaltyValue() < 0) {
                throw new IllegalArgumentException(
                        "Soft constraint penalty cannot be negative.");
            }

            penaltyBreakdown.merge(
                    constraintCode,
                    penalty.penaltyValue(),
                    Integer::sum
            );
        }

        int totalPenalty = penaltyBreakdown.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        return new SoftConstraintResult(
                totalPenalty,
                penaltyBreakdown
        );
    }

    public record SoftConstraintPenalty(
            String constraintCode,
            int penaltyValue) {
    }

    public record SoftConstraintResult(
            int totalPenalty,
            Map<String, Integer> penaltyBreakdown) {

        public SoftConstraintResult {
            penaltyBreakdown = Map.copyOf(penaltyBreakdown);
        }
    }
}
