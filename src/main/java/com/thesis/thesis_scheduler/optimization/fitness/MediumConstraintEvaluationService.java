
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediumConstraintEvaluationService {

    private final FullTimeFacultyPrioritizationEvaluator
            fullTimeFacultyPrioritizationEvaluator;

    public MediumConstraintResult evaluate(
            ScheduleChromosome chromosome,
            Map<Long, FullTimeFacultyPrioritizationEvaluator
                    .AssignmentPriorityAssessment> mc2Assessments) {

        if (chromosome == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must not be null.");
        }

        var mc2Result = fullTimeFacultyPrioritizationEvaluator.evaluate(
                chromosome,
                mc2Assessments
        );

        return new MediumConstraintResult(
                mc2Result.penaltyValue()
        );
    }

    public record MediumConstraintResult(
            int mediumConstraintViolations) {
    }
}
