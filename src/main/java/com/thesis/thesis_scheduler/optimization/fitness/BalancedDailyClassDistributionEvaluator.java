
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BalancedDailyClassDistributionEvaluator {

    private static final String CONSTRAINT_CODE = "SC5";

    private final TimeslotRepository timeslotRepository;

    public SoftConstraintCalculator.SoftConstraintPenalty evaluate(
            ScheduleChromosome chromosome,
            int penaltyPerImbalanceHour) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (penaltyPerImbalanceHour < 0) {
            throw new IllegalArgumentException(
                    "SC5 penalty cannot be negative.");
        }

        // Section ID -> Day of week -> Total scheduled minutes
        Map<Long, Map<Short, Integer>> dailyHours = new HashMap<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null
                    || gene.getSectionId() == null
                    || gene.getTimeslotId() == null) {
                throw new IllegalArgumentException(
                        "Schedule gene must contain section and timeslot IDs.");
            }

            Timeslot timeslot = timeslotRepository
                    .findById(gene.getTimeslotId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Timeslot not found: " + gene.getTimeslotId()));

            if (timeslot.getDayOfWeek() == null
                    || timeslot.getStartTime() == null
                    || timeslot.getEndTime() == null
                    || !timeslot.getEndTime().isAfter(timeslot.getStartTime())) {
                throw new IllegalArgumentException(
                        "Invalid timeslot: " + gene.getTimeslotId());
            }

            int durationMinutes = timeslot.getDurationMinutes() != null
                    ? timeslot.getDurationMinutes()
                    : (int) java.time.Duration.between(
                    timeslot.getStartTime(),
                    timeslot.getEndTime()
            ).toMinutes();

            if (durationMinutes <= 0) {
                throw new IllegalArgumentException(
                        "Timeslot duration must be positive.");
            }

            dailyHours
                    .computeIfAbsent(gene.getSectionId(), ignored -> new HashMap<>())
                    .merge(timeslot.getDayOfWeek(), durationMinutes, Integer::sum);
        }

        double totalImbalanceHours = 0.0;

        for (Map<Short, Integer> sectionSchedule : dailyHours.values()) {

            double averageMinutes = sectionSchedule.values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            for (int dailyMinutes : sectionSchedule.values()) {
                totalImbalanceHours += Math.abs(dailyMinutes - averageMinutes) / 60.0;
            }
        }

        int totalPenalty = (int) Math.ceil(
                totalImbalanceHours * penaltyPerImbalanceHour
        );

        return new SoftConstraintCalculator.SoftConstraintPenalty(
                CONSTRAINT_CODE,
                totalPenalty
        );
    }
}
