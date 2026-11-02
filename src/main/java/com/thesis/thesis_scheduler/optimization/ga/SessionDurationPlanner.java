package com.thesis.thesis_scheduler.optimization.ga;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class SessionDurationPlanner {

    private static final int BLOCK_DURATION_MINUTES = 30;

    private static final String LECTURE = "LECTURE";
    private static final String LABORATORY = "LABORATORY";

    public List<List<Integer>> generateSessionPatterns(
            BigDecimal requiredHours,
            String sessionType,
            InstructorScheduleAssessment.SchedulingProfile profile) {

        validateInputs(requiredHours, sessionType, profile);

        int totalMinutes = requiredHours
                .multiply(BigDecimal.valueOf(60))
                .intValueExact();

        if (totalMinutes % BLOCK_DURATION_MINUTES != 0) {
            throw new IllegalArgumentException(
                    "Required hours must be divisible into 30-minute blocks."
            );
        }

        List<List<Integer>> patterns = new ArrayList<>();

        if (LECTURE.equals(sessionType)) {

            // Three-hour lecture: continuous or two 1.5-hour sessions.
            if (totalMinutes == 180) {
                patterns.add(List.of(180));
                patterns.add(List.of(90, 90));
            } else {
                patterns.add(List.of(totalMinutes));
            }

        } else if (LABORATORY.equals(sessionType)) {

            // Laboratory sessions remain continuous.
            patterns.add(List.of(totalMinutes));
        }

        // Prioritize continuous arrangements when preferred.
        if (profile.preferContinuousSessions()) {
            patterns.sort(Comparator.comparingInt(
                    pattern -> pattern.size()
            ));
        } else if (profile.allowFlexibleSessionSplitting()) {
            patterns.sort(Comparator.comparingInt(
                    (List<Integer> pattern) -> pattern.size()
            ).reversed());
        }

        return patterns.stream()
                .map(List::copyOf)
                .toList();
    }

    private void validateInputs(
            BigDecimal requiredHours,
            String sessionType,
            InstructorScheduleAssessment.SchedulingProfile profile) {

        if (requiredHours == null
                || requiredHours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Required hours must be greater than zero."
            );
        }

        if (sessionType == null
                || (!LECTURE.equals(sessionType)
                && !LABORATORY.equals(sessionType))) {
            throw new IllegalArgumentException(
                    "Session type must be LECTURE or LABORATORY."
            );
        }

        if (profile == null) {
            throw new IllegalArgumentException(
                    "Instructor scheduling profile is required."
            );
        }
    }
}
