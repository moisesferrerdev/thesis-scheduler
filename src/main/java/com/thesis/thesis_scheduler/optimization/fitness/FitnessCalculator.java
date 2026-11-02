
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ConstraintViolation;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FitnessCalculator {

    /*
     * Existing constraint penalty calculation.
     * Retained for compatibility with the current implementation.
     */
    public int calculatePenalty(
            ScheduleChromosome chromosome,
            List<ConstraintViolation> violations) {

        if (chromosome == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must not be null.");
        }

        if (violations == null || violations.isEmpty()) {
            return 0;
        }

        return violations.stream()
                .filter(violation -> violation != null)
                .mapToInt(ConstraintViolation::getPenalty)
                .sum();
    }

    /*
     * Existing fitness calculation.
     * This is retained for the current penalty-based implementation.
     */
    public double calculateFitness(
            ScheduleChromosome chromosome,
            List<ConstraintViolation> violations) {

        int totalPenalty = calculatePenalty(chromosome, violations);

        return 1.0 / (1.0 + totalPenalty);
    }

    /*
     * Calculates the thesis objective function:
     *
     * Z = w1(CV) + w2(WB) + w3(RP) + w4(SP)
     */
    public double calculateObjectiveValue(
            ObjectiveMetrics metrics,
            ObjectiveWeights weights) {

        validateMetrics(metrics);
        validateWeights(weights);

        return (weights.cvWeight() * metrics.cv())
                + (weights.wbWeight() * metrics.wb())
                + (weights.rpWeight() * metrics.rp())
                + (weights.spWeight() * metrics.sp());
    }

    /*
     * Converts the objective value into fitness:
     *
     * F(C) = 1 / (1 + Z)
     *
     * A lower objective value produces a higher fitness score.
     */
    public double calculateObjectiveFitness(
            ObjectiveMetrics metrics,
            ObjectiveWeights weights) {

        double objectiveValue =
                calculateObjectiveValue(metrics, weights);

        return 1.0 / (1.0 + objectiveValue);
    }

    private void validateMetrics(ObjectiveMetrics metrics) {

        if (metrics == null) {
            throw new IllegalArgumentException(
                    "Objective metrics must not be null.");
        }

        validateNonNegative("CV", metrics.cv());
        validateNonNegative("WB", metrics.wb());
        validateNonNegative("RP", metrics.rp());
        validateNonNegative("SP", metrics.sp());
    }

    private void validateWeights(ObjectiveWeights weights) {

        if (weights == null) {
            throw new IllegalArgumentException(
                    "Objective weights must not be null.");
        }

        validateNonNegative("CV weight", weights.cvWeight());
        validateNonNegative("WB weight", weights.wbWeight());
        validateNonNegative("RP weight", weights.rpWeight());
        validateNonNegative("SP weight", weights.spWeight());

        double totalWeight =
                weights.cvWeight()
                        + weights.wbWeight()
                        + weights.rpWeight()
                        + weights.spWeight();

        if (Math.abs(totalWeight - 1.0) > 0.000001) {
            throw new IllegalArgumentException(
                    "Objective weights must sum to 1.0. Current total: "
                            + totalWeight);
        }
    }

    private void validateNonNegative(String field, double value) {

        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(
                    field + " must be a finite, non-negative value.");
        }
    }

    /*
     * Represents the four calculated thesis objective metrics.
     *
     * CV should already incorporate:
     * CV = 100(HC) + 10(MC)
     *
     * WB, RP, and SP must follow their respective
     * definitions in the thesis.
     */
    public record ObjectiveMetrics(
            double cv,
            double wb,
            double rp,
            double sp) {
    }

    /*
     * Objective weights are configurable.
     * No experimental weight combination is hardcoded here.
     */
    public record ObjectiveWeights(
            double cvWeight,
            double wbWeight,
            double rpWeight,
            double spWeight) {
    }
}
