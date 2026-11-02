
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.optimization.constraint.RoomCapacityConstraint;
import com.thesis.thesis_scheduler.optimization.fitness.FullTimeFacultyPrioritizationEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredRoomAssignmentEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredTeachingScheduleEvaluator;
import com.thesis.thesis_scheduler.optimization.ga.GeneticAlgorithmOptimizer;
import com.thesis.thesis_scheduler.optimization.ga.RoomCapacityLookup;
import com.thesis.thesis_scheduler.optimization.service.OptimizationSettingsResolver.ResolvedOptimizationSettings;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OptimizationWorkerService {

    private final GeneticAlgorithmOptimizer geneticAlgorithmOptimizer;

    private final RoomCapacityLookup roomCapacityLookup;

    @Async("optimizationTaskExecutor")
    public CompletableFuture<GeneticAlgorithmOptimizer.OptimizationResult>
    executeOptimization(
            Long termId,
            Integer populationSize,
            Integer generationLimit,
            BigDecimal crossoverRate,
            BigDecimal mutationRate,
            ResolvedOptimizationSettings settings,
            Map<Long, PreferredTeachingScheduleEvaluator.InstructorTeachingPreference> instructorPreferences,
            Map<Long, PreferredRoomAssignmentEvaluator.PreferredRoomPreference> roomPreferences,
            Map<Long, FullTimeFacultyPrioritizationEvaluator.AssignmentPriorityAssessment> mc2Assessments
    ) {

        if (settings == null) {
            throw new IllegalArgumentException(
                    "Optimization settings must not be null."
            );
        }

        GeneticAlgorithmOptimizer.OptimizationResult result =
                geneticAlgorithmOptimizer.optimize(
                        termId,
                        populationSize,
                        generationLimit,
                        settings.tournamentSize(),
                        crossoverRate.doubleValue(),
                        mutationRate.doubleValue(),
                        settings.targetFitness(),
                        roomCapacityLookup,
                        instructorPreferences,
                        roomPreferences,
                        settings.sc3PenaltyPerGapHour(),
                        settings.sc4PenaltyPerGapHour(),
                        settings.sc5PenaltyPerImbalanceHour(),
                        mc2Assessments,
                        settings.weights()
                );

        return CompletableFuture.completedFuture(result);
    }
}
