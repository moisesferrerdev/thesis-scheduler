
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SoftConstraintEvaluationService {

    private final PreferredTeachingScheduleEvaluator preferredTeachingScheduleEvaluator;
    private final PreferredRoomAssignmentEvaluator preferredRoomAssignmentEvaluator;
    private final StudentScheduleCompactnessEvaluator studentScheduleCompactnessEvaluator;
    private final VacantHourMinimizationEvaluator vacantHourMinimizationEvaluator;
    private final BalancedDailyClassDistributionEvaluator balancedDailyClassDistributionEvaluator;
    private final SoftConstraintCalculator softConstraintCalculator;

    public SoftConstraintCalculator.SoftConstraintResult evaluate(
            ScheduleChromosome chromosome,
            Map<Long, PreferredTeachingScheduleEvaluator.InstructorTeachingPreference> instructorPreferences,
            Map<Long, PreferredRoomAssignmentEvaluator.PreferredRoomPreference> roomPreferences,
            int sc3PenaltyPerGapHour,
            int sc4PenaltyPerGapHour,
            int sc5PenaltyPerImbalanceHour) {

        if (chromosome == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must not be null.");
        }

        var sc1 = preferredTeachingScheduleEvaluator.evaluate(
                chromosome,
                instructorPreferences
        );

        var sc2 = preferredRoomAssignmentEvaluator.evaluate(
                chromosome,
                roomPreferences
        );

        var sc3 = studentScheduleCompactnessEvaluator.evaluate(
                chromosome,
                sc3PenaltyPerGapHour
        );

        var sc4 = vacantHourMinimizationEvaluator.evaluate(
                chromosome,
                sc4PenaltyPerGapHour
        );

        var sc5 = balancedDailyClassDistributionEvaluator.evaluate(
                chromosome,
                sc5PenaltyPerImbalanceHour
        );

        List<SoftConstraintCalculator.SoftConstraintPenalty> penalties =
                List.of(sc1, sc2, sc3, sc4, sc5);

        return softConstraintCalculator.calculate(penalties);
    }
}
