
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.optimization.constraint.ConstraintEvaluator;
import com.thesis.thesis_scheduler.optimization.model.ConstraintViolation;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompleteScheduleValidator {

    private final ConstraintEvaluator constraintEvaluator;

    private final RoomCapacityLookup roomCapacityLookup;

    public ValidationResult validate(
            ScheduleChromosome chromosome,
            Long termId) {

        if (chromosome == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome is required.");
        }

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Term ID must be a positive value.");
        }

        if (chromosome.getGenes() == null
                || chromosome.getGenes().isEmpty()) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must contain schedule genes.");
        }

        List<ConstraintViolation> violations =
                constraintEvaluator.evaluate(
                        chromosome,
                        termId,
                        roomCapacityLookup
                );

        if (violations == null) {
            throw new IllegalStateException(
                    "Constraint evaluator returned a null result.");
        }

        List<ConstraintViolation> detectedViolations =
                List.copyOf(violations);

        return new ValidationResult(
                detectedViolations.isEmpty(),
                detectedViolations
        );
    }

    public boolean isFeasible(
            ScheduleChromosome chromosome,
            Long termId) {

        return validate(chromosome, termId).feasible();
    }

    public record ValidationResult(
            boolean feasible,
            List<ConstraintViolation> violations) {

        public ValidationResult {
            violations = List.copyOf(violations);
        }
    }
}
