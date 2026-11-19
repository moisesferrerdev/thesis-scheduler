
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.entity.SchedulingConfig;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.SchedulingConfigRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ensures every assigned timeslot falls inside the institution's configured
 * operating days and hours for the selected academic term.
 */
@Component
@RequiredArgsConstructor
public class InstitutionSchedulingWindowConstraint {

    private final SchedulingConfigRepository schedulingConfigRepository;
    private final TimeslotRepository timeslotRepository;

    public boolean hasConflict(
            ScheduleChromosome chromosome,
            Long termId) {

        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        if (termId == null) {
            return true;
        }

        SchedulingConfig config = schedulingConfigRepository
                .findByTerm_TermId(termId)
                .orElse(null);

        // A term must have a configured institution window before it can be optimized.
        if (config == null
                || config.getOperatingDays() == null
                || config.getOpeningTime() == null
                || config.getClosingTime() == null) {

            return true;
        }

        if (config.getTimeslotIntervalMinutes() == null
                || config.getTimeslotIntervalMinutes() <= 0) {

            return true;
        }

        Set<Integer> operatingDays = Arrays.stream(
                        config.getOperatingDays().split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null || gene.getTimeslotId() == null) {
                continue;
            }

            Timeslot timeslot = timeslotRepository
                    .findById(gene.getTimeslotId())
                    .orElse(null);

            if (timeslot == null
                    || timeslot.getTerm() == null
                    || !termId.equals(timeslot.getTerm().getTermId())
                    || timeslot.getDayOfWeek() == null
                    || timeslot.getStartTime() == null
                    || timeslot.getEndTime() == null) {

                return true;
            }

            int day = timeslot.getDayOfWeek().intValue();

            long intervalSeconds =
                    config.getTimeslotIntervalMinutes() * 60L;

            long startOffsetSeconds = Duration.between(
                    config.getOpeningTime(),
                    timeslot.getStartTime()
            ).getSeconds();

            long endOffsetSeconds = Duration.between(
                    config.getOpeningTime(),
                    timeslot.getEndTime()
            ).getSeconds();

            if (!operatingDays.contains(day)
                    || timeslot.getStartTime().isBefore(
                    config.getOpeningTime())
                    || timeslot.getEndTime().isAfter(
                    config.getClosingTime())
                    || !timeslot.getStartTime().isBefore(
                    timeslot.getEndTime())
                    || startOffsetSeconds < 0
                    || endOffsetSeconds < 0
                    || startOffsetSeconds % intervalSeconds != 0
                    || endOffsetSeconds % intervalSeconds != 0) {

                return true;
            }
        }

        return false;
    }
}
