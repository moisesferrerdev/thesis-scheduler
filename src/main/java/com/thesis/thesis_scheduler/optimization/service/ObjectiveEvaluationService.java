
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.optimization.constraint.ConstraintEvaluator;
import com.thesis.thesis_scheduler.optimization.constraint.RoomCapacityConstraint;
import com.thesis.thesis_scheduler.optimization.fitness.FitnessCalculator;
import com.thesis.thesis_scheduler.optimization.fitness.MediumConstraintEvaluationService;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredRoomAssignmentEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredTeachingScheduleEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.RoomUtilizationCalculator;
import com.thesis.thesis_scheduler.optimization.fitness.SoftConstraintEvaluationService;
import com.thesis.thesis_scheduler.optimization.fitness.WorkloadBalanceCalculator;
import com.thesis.thesis_scheduler.optimization.model.ConstraintViolation;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.fitness.FullTimeFacultyPrioritizationEvaluator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ObjectiveEvaluationService {

    private final ConstraintEvaluator constraintEvaluator;
    private final MediumConstraintEvaluationService mediumConstraintEvaluationService;
    private final WorkloadBalanceCalculator workloadBalanceCalculator;
    private final RoomUtilizationCalculator roomUtilizationCalculator;
    private final SoftConstraintEvaluationService softConstraintEvaluationService;
    private final FitnessCalculator fitnessCalculator;

    public EvaluationResult evaluate(
            ScheduleChromosome chromosome,
            Long termId,
            RoomCapacityConstraint.CapacityLookup capacityLookup,
            Map<Long, PreferredTeachingScheduleEvaluator.InstructorTeachingPreference> instructorPreferences,
            Map<Long, PreferredRoomAssignmentEvaluator.PreferredRoomPreference> roomPreferences,
            int sc3PenaltyPerGapHour,
            int sc4PenaltyPerGapHour,
            int sc5PenaltyPerImbalanceHour,
            Map<Long, FullTimeFacultyPrioritizationEvaluator.AssignmentPriorityAssessment> mc2Assessments,
            FitnessCalculator.ObjectiveWeights weights) {

        if (chromosome == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must not be null.");
        }

        if (termId == null) {
            throw new IllegalArgumentException(
                    "Academic term ID must not be null.");
        }

        if (capacityLookup == null) {
            throw new IllegalArgumentException(
                    "Room capacity lookup must not be null.");
        }

        if (weights == null) {
            throw new IllegalArgumentException(
                    "Objective weights must not be null.");
        }

        // 1. Evaluate hard constraints.
        List<ConstraintViolation> hardViolations =
                constraintEvaluator.evaluate(
                        chromosome,
                        termId,
                        capacityLookup
                );

        int hardConstraintCount = hardViolations.size();

        // 2. Reject infeasible chromosomes before calculating
        // the remaining objective metrics.
        if (hardConstraintCount > 0) {

            double cv = 100.0 * hardConstraintCount;

            FitnessCalculator.ObjectiveMetrics metrics =
                    new FitnessCalculator.ObjectiveMetrics(
                            cv,
                            0.0,
                            0.0,
                            0.0
                    );

            return new EvaluationResult(
                    false,
                    hardViolations,
                    metrics,
                    Double.POSITIVE_INFINITY,
                    0.0
            );
        }

        // 3. Evaluate medium constraints (MC2).
        var mediumResult =
                mediumConstraintEvaluationService.evaluate(
                        chromosome,
                        mc2Assessments
                );

        int mediumConstraintCount =
                mediumResult.mediumConstraintViolations();

        // 4. Calculate constraint violations.
        double cv =
                (100.0 * hardConstraintCount)
                        + (10.0 * mediumConstraintCount);

        // 5. Calculate workload balance (WB).
        double wb =
                workloadBalanceCalculator.calculateWorkloadBalance(
                        chromosome
                );

        // 6. Calculate room utilization penalty (RP).
        double rp =
                roomUtilizationCalculator.calculateRoomUtilization(
                        chromosome
                );

        // 7. Evaluate soft constraints (SP).
        var softResult =
                softConstraintEvaluationService.evaluate(
                        chromosome,
                        instructorPreferences,
                        roomPreferences,
                        sc3PenaltyPerGapHour,
                        sc4PenaltyPerGapHour,
                        sc5PenaltyPerImbalanceHour
                );

        double sp = softResult.totalPenalty();

        // 8. Build the four objective metrics.
        FitnessCalculator.ObjectiveMetrics metrics =
                new FitnessCalculator.ObjectiveMetrics(
                        cv,
                        wb,
                        rp,
                        sp
                );

        // 9. Calculate objective value and fitness.
        double objectiveValue =
                fitnessCalculator.calculateObjectiveValue(
                        metrics,
                        weights
                );

        double fitness =
                fitnessCalculator.calculateObjectiveFitness(
                        metrics,
                        weights
                );

        return new EvaluationResult(
                true,
                hardViolations,
                metrics,
                objectiveValue,
                fitness
        );
    }

    public record EvaluationResult(
            boolean feasible,
            List<ConstraintViolation> hardViolations,
            FitnessCalculator.ObjectiveMetrics metrics,
            double objectiveValue,
            double fitness
    ) {
    }
}
