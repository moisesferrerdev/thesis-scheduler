
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.entity.Timeslot;

import com.thesis.thesis_scheduler.repository.TimeslotRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StudentScheduleCompactnessEvaluator {

    private static final String CONSTRAINT_CODE = "SC3";

    private final TimeslotRepository timeslotRepository;

    public SoftConstraintCalculator.SoftConstraintPenalty evaluate(
            ScheduleChromosome chromosome,
            int penaltyPerGapHour) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (penaltyPerGapHour < 0) {
            throw new IllegalArgumentException(
                    "SC3 penalty cannot be negative.");
        }

        Map<SectionDayKey, List<TimeInterval>> schedules =
                new HashMap<>();

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

            SectionDayKey key = new SectionDayKey(
                    gene.getSectionId(),
                    timeslot.getDayOfWeek()
            );

            schedules.computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(new TimeInterval(
                            timeslot.getStartTime().toSecondOfDay(),
                            timeslot.getEndTime().toSecondOfDay()
                    ));
        }

        int totalGapMinutes = 0;

        for (List<TimeInterval> intervals : schedules.values()) {

            intervals.sort(Comparator.comparingInt(TimeInterval::start));

            int previousEnd = intervals.get(0).end();

            for (int i = 1; i < intervals.size(); i++) {

                TimeInterval current = intervals.get(i);

                if (current.start() > previousEnd) {
                    totalGapMinutes += current.start() - previousEnd;
                }

                previousEnd = Math.max(previousEnd, current.end());
            }
        }

        int totalGapHours = (int) Math.ceil(totalGapMinutes / 60.0);

        int totalPenalty = Math.multiplyExact(
                totalGapHours,
                penaltyPerGapHour
        );

        return new SoftConstraintCalculator.SoftConstraintPenalty(
                CONSTRAINT_CODE,
                totalPenalty
        );
    }

    private record SectionDayKey(
            Long sectionId,
            Short dayOfWeek) {
    }

    private record TimeInterval(
            int start,
            int end) {
    }
}
