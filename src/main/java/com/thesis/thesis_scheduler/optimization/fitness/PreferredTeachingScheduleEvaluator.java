
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PreferredTeachingScheduleEvaluator {

    private static final String CONSTRAINT_CODE = "SC1";

    private final TimeslotRepository timeslotRepository;

    public SoftConstraintCalculator.SoftConstraintPenalty evaluate(
            ScheduleChromosome chromosome,
            Map<Long, InstructorTeachingPreference> preferences) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (preferences == null) {
            throw new IllegalArgumentException(
                    "Instructor preferences must not be null.");
        }

        if (chromosome.getGenes().isEmpty() || preferences.isEmpty()) {
            return new SoftConstraintCalculator.SoftConstraintPenalty(
                    CONSTRAINT_CODE, 0);
        }

        Set<Long> timeslotIds = new HashSet<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null || gene.getTimeslotId() == null) {
                throw new IllegalArgumentException(
                        "Schedule genes must contain valid timeslot IDs.");
            }

            timeslotIds.add(gene.getTimeslotId());
        }

        Map<Long, Timeslot> timeslots = new HashMap<>();

        for (Timeslot timeslot :
                timeslotRepository.findAllById(timeslotIds)) {

            timeslots.put(timeslot.getTimeslotId(), timeslot);
        }

        if (timeslots.size() != timeslotIds.size()) {
            throw new IllegalStateException(
                    "One or more schedule timeslots could not be found.");
        }

        Map<SessionKey, Boolean> sessionViolations = new HashMap<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            InstructorTeachingPreference preference =
                    preferences.get(gene.getInstructorId());

            // No recorded preference means no SC1 penalty.
            if (preference == null) {
                continue;
            }

            Timeslot timeslot = timeslots.get(gene.getTimeslotId());

            validateTimeslot(timeslot);
            validatePreference(preference);

            SessionKey sessionKey = new SessionKey(
                    gene.getInstructorId(),
                    gene.getSessionGroupId()
            );

            boolean violatesPreference =
                    violatesPreference(timeslot, preference);

            if (violatesPreference) {
                sessionViolations.put(sessionKey, true);
            } else {
                sessionViolations.putIfAbsent(sessionKey, false);
            }
        }

        int totalPenalty = 0;

        for (Map.Entry<SessionKey, Boolean> entry :
                sessionViolations.entrySet()) {

            if (entry.getValue()) {

                InstructorTeachingPreference preference =
                        preferences.get(entry.getKey().instructorId());

                totalPenalty = Math.addExact(
                        totalPenalty,
                        preference.penaltyPerViolation()
                );
            }
        }

        return new SoftConstraintCalculator.SoftConstraintPenalty(
                CONSTRAINT_CODE,
                totalPenalty
        );
    }

    private boolean violatesPreference(
            Timeslot timeslot,
            InstructorTeachingPreference preference) {

        boolean violatesDay =
                !preference.preferredDays().isEmpty()
                        && !preference.preferredDays()
                        .contains(timeslot.getDayOfWeek());

        boolean violatesTime = false;

        if (preference.preferredStartTime() != null) {
            violatesTime =
                    timeslot.getStartTime()
                            .isBefore(preference.preferredStartTime());
        }

        if (preference.preferredEndTime() != null) {
            violatesTime = violatesTime
                    || timeslot.getEndTime()
                    .isAfter(preference.preferredEndTime());
        }

        return violatesDay || violatesTime;
    }

    private void validateTimeslot(Timeslot timeslot) {

        if (timeslot.getDayOfWeek() == null
                || timeslot.getStartTime() == null
                || timeslot.getEndTime() == null) {
            throw new IllegalStateException(
                    "Timeslot has incomplete scheduling information.");
        }
    }

    private void validatePreference(
            InstructorTeachingPreference preference) {

        if (preference.preferredDays() == null) {
            throw new IllegalArgumentException(
                    "Preferred teaching days must not be null.");
        }

        if (preference.penaltyPerViolation() < 0) {
            throw new IllegalArgumentException(
                    "SC1 penalty cannot be negative.");
        }

        if (preference.preferredStartTime() != null
                && preference.preferredEndTime() != null
                && !preference.preferredStartTime()
                .isBefore(preference.preferredEndTime())) {

            throw new IllegalArgumentException(
                    "Preferred start time must be before preferred end time.");
        }
    }

    public record InstructorTeachingPreference(
            Set<Short> preferredDays,
            LocalTime preferredStartTime,
            LocalTime preferredEndTime,
            int penaltyPerViolation) {

        public InstructorTeachingPreference {
            if (preferredDays != null) {
                preferredDays = Set.copyOf(preferredDays);
            }
        }
    }

    private record SessionKey(
            Long instructorId,
            String sessionGroupId) {
    }
}
