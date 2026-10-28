
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ConstraintViolation;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;

import java.util.List;

public class FitnessCalculator {

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

    public double calculateFitness(
            ScheduleChromosome chromosome,
            List<ConstraintViolation> violations) {

        int totalPenalty = calculatePenalty(chromosome, violations);

        return 1.0 / (1.0 + totalPenalty);
    }
}
