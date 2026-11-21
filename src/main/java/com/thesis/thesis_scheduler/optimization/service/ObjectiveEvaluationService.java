
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.optimization.constraint.ConstraintEvaluator;
import com.thesis.thesis_scheduler.optimization.constraint.RoomCapacityConstraint;
import com.thesis.thesis_scheduler.optimization.fitness.FitnessCalculator;
import com.thesis.thesis_scheduler.optimization.fitness.FullTimeFacultyPrioritizationEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.MediumConstraintEvaluationService;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredRoomAssignmentEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredTeachingScheduleEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.RoomUtilizationCalculator;
import com.thesis.thesis_scheduler.optimization.fitness.SoftConstraintEvaluationService;
import com.thesis.thesis_scheduler.optimization.fitness.WorkloadBalanceCalculator;
import com.thesis.thesis_scheduler.optimization.model.ConstraintViolation;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

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

        validateInputs(chromosome, termId, capacityLookup, weights);

        /*
         * 1. Evaluate hard constraints.
         *
         * Hard-constraint feasibility is independent from
         * whether the other objective metrics can be calculated.
         */
        List<ConstraintViolation> hardViolations =
                constraintEvaluator.evaluate(
                        chromosome,
                        termId,
                        capacityLookup
                );

        boolean feasible = hardViolations.isEmpty();

        int hardConstraintCount = hardViolations.size();

        /*
         * 2. Evaluate medium constraints.
         *
         * CV = 100(HC) + 10(MC)
         *
         * This is calculated for both feasible and infeasible
         * candidates.
         */
        var mediumResult =
                mediumConstraintEvaluationService.evaluate(
                        chromosome,
                        mc2Assessments
                );

        int mediumConstraintCount =
                mediumResult.mediumConstraintViolations();

        double cv =
                (100.0 * hardConstraintCount)
                        + (10.0 * mediumConstraintCount);

        /*
         * 3. Calculate workload balance.
         *
         * This calculation is independent of hard-constraint
         * feasibility, provided the chromosome contains valid
         * instructor and timeslot references.
         */
        double wb =
                workloadBalanceCalculator.calculateWorkloadBalance(
                        chromosome
                );

        /*
         * 4. Calculate room utilization.
         *
         * RP is unavailable when a section exceeds room capacity.
         * We do not substitute zero or invent another penalty.
         */
        OptionalDouble rpResult =
                roomUtilizationCalculator.calculateRoomUtilizationIfValid(
                        chromosome
                );

        Double rp = rpResult.isPresent()
                ? rpResult.getAsDouble()
                : null;

        /*
         * 5. Evaluate soft constraints.
         *
         * Soft constraints are evaluated even when hard violations
         * exist, so the final candidate retains its research metrics.
         */
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

        /*
         * 6. Retain every individually calculated metric.
         *
         * A null RP explicitly means unavailable, not zero.
         */
        MetricValues metricValues = new MetricValues(
                cv,
                wb,
                rp,
                sp
        );

        List<String> unavailableMetrics = new ArrayList<>();

        if (rp == null) {
            unavailableMetrics.add("RP");
        }

        /*
         * 7. Calculate the overall objective and fitness only
         * when all four objective metrics are available.
         *
         * The existing FitnessCalculator formula and weights
         * remain unchanged.
         */
        if (!unavailableMetrics.isEmpty()) {

            return new EvaluationResult(
                    feasible,
                    hardViolations,
                    metricValues,
                    null,  // metrics
                    null,  // objectiveValue
                    null,  // fitness
                    false, // metricsComplete
                    unavailableMetrics
            );
        }

        FitnessCalculator.ObjectiveMetrics metrics =
                new FitnessCalculator.ObjectiveMetrics(
                        cv,
                        wb,
                        rp,
                        sp
                );

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
                feasible,
                hardViolations,
                metricValues,
                metrics,
                objectiveValue,
                fitness,
                true,
                List.of()
        );
    }

    private void validateInputs(
            ScheduleChromosome chromosome,
            Long termId,
            RoomCapacityConstraint.CapacityLookup capacityLookup,
            FitnessCalculator.ObjectiveWeights weights) {

        if (chromosome == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must not be null.");
        }

        if (chromosome.getGenes() == null
                || chromosome.getGenes().isEmpty()) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must contain genes.");
        }

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Academic term ID must be a positive value.");
        }

        if (capacityLookup == null) {
            throw new IllegalArgumentException(
                    "Room capacity lookup must not be null.");
        }

        if (weights == null) {
            throw new IllegalArgumentException(
                    "Objective weights must not be null.");
        }
    }

    /**
     * Holds the individually calculated objective metrics.
     *
     * A null value means that the metric could not be calculated.
     * It must never be interpreted as a measured zero.
     */
    public record MetricValues(
            Double cv,
            Double wb,
            Double rp,
            Double sp
    ) {
    }

    /**
     * Represents the complete evaluation of one candidate.
     *
     * metrics, objectiveValue, and fitness are unavailable
     * when one or more objective metrics could not be calculated.
     *
     * Feasibility is determined only by hard-constraint violations.
     */
    public record EvaluationResult(
            boolean feasible,
            List<ConstraintViolation> hardViolations,
            MetricValues metricValues,
            FitnessCalculator.ObjectiveMetrics metrics,
            Double objectiveValue,
            Double fitness,
            boolean metricsComplete,
            List<String> unavailableMetrics
    ) {

        public EvaluationResult {
            hardViolations = hardViolations == null
                    ? List.of()
                    : List.copyOf(hardViolations);

            unavailableMetrics = unavailableMetrics == null
                    ? List.of()
                    : List.copyOf(unavailableMetrics);

            if (metricValues == null) {
                throw new IllegalArgumentException(
                        "Metric values must not be null.");
            }

            if (feasible && !hardViolations.isEmpty()) {
                throw new IllegalArgumentException(
                        "A feasible candidate cannot contain hard violations.");
            }

            if (!feasible && hardViolations.isEmpty()) {
                throw new IllegalArgumentException(
                        "An infeasible candidate must contain hard violations.");
            }

            if (metricsComplete) {

                if (metrics == null
                        || objectiveValue == null
                        || fitness == null
                        || !unavailableMetrics.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Complete metrics require objective metrics, "
                                    + "objective value, and fitness.");
                }

                if (!Double.isFinite(objectiveValue)
                        || !Double.isFinite(fitness)
                        || fitness < 0.0
                        || fitness > 1.0) {

                    throw new IllegalArgumentException(
                            "Objective value and fitness must be finite "
                                    + "and fitness must be between 0 and 1.");
                }

            } else if (metrics != null
                    || objectiveValue != null
                    || fitness != null
                    || unavailableMetrics.isEmpty()) {

                throw new IllegalArgumentException(
                        "Incomplete metrics must identify unavailable metrics "
                                + "and must not contain an overall objective "
                                + "value or fitness.");
            }
        }
    }
}
