 package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Instructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InstructorScheduleAssessment {

    public SchedulingProfile assess(Instructor instructor) {

        if (instructor == null) {
            throw new IllegalArgumentException(
                    "Instructor must not be null."
            );
        }

        String employmentType = instructor.getEmploymentType();

        if (employmentType == null || employmentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Instructor employment type must not be empty."
            );
        }

        String normalizedType = employmentType
                .trim()
                .toUpperCase();

        return switch (normalizedType) {

            case "PART_TIME" -> new SchedulingProfile(
                    normalizedType,
                    true,
                    false
            );

            case "FULL_TIME" -> new SchedulingProfile(
                    normalizedType,
                    false,
                    true
            );

            default -> new SchedulingProfile(
                    normalizedType,
                    false,
                    false
            );
        };
    }

    public record SchedulingProfile(
            String employmentType,
            boolean preferContinuousSessions,
            boolean allowFlexibleSessionSplitting
    ) {
    }
}
