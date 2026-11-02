
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PreferredRoomAssignmentEvaluator {

    private static final String CONSTRAINT_CODE = "SC2";

    public SoftConstraintCalculator.SoftConstraintPenalty evaluate(
            ScheduleChromosome chromosome,
            Map<Long, PreferredRoomPreference> preferences) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (preferences == null) {
            throw new IllegalArgumentException(
                    "Preferred room preferences must not be null.");
        }

        Map<SessionKey, SessionPlacement> sessions = new HashMap<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null
                    || gene.getTeachingAssignmentId() == null
                    || gene.getRoomId() == null
                    || gene.getSessionGroupId() == null
                    || gene.getSessionGroupId().isBlank()) {

                throw new IllegalArgumentException(
                        "Schedule gene must contain teaching assignment, "
                                + "room, and session group IDs.");
            }

            SessionKey key = new SessionKey(
                    gene.getTeachingAssignmentId(),
                    gene.getSessionGroupId()
            );

            SessionPlacement placement = new SessionPlacement(
                    gene.getTeachingAssignmentId(),
                    gene.getRoomId()
            );

            SessionPlacement existing = sessions.putIfAbsent(key, placement);

            if (existing != null
                    && !existing.roomId().equals(gene.getRoomId())) {

                throw new IllegalStateException(
                        "One session group cannot use multiple rooms. "
                                + "Session group: " + gene.getSessionGroupId());
            }
        }

        int totalPenalty = 0;

        for (SessionPlacement session : sessions.values()) {

            PreferredRoomPreference preference =
                    preferences.get(session.teachingAssignmentId());

            // No preference means no SC2 penalty.
            if (preference == null) {
                continue;
            }

            validatePreference(preference);

            if (!preference.preferredRoomIds()
                    .contains(session.roomId())) {

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

    private void validatePreference(
            PreferredRoomPreference preference) {

        if (preference.preferredRoomIds() == null
                || preference.preferredRoomIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one preferred room ID is required.");
        }

        if (preference.preferredRoomIds().contains(null)) {
            throw new IllegalArgumentException(
                    "Preferred room IDs cannot contain null.");
        }

        if (preference.penaltyPerViolation() < 0) {
            throw new IllegalArgumentException(
                    "SC2 penalty cannot be negative.");
        }
    }

    public record PreferredRoomPreference(
            Set<Long> preferredRoomIds,
            int penaltyPerViolation) {

        public PreferredRoomPreference {
            if (preferredRoomIds != null) {
                preferredRoomIds = Set.copyOf(preferredRoomIds);
            }
        }
    }

    private record SessionKey(
            Long teachingAssignmentId,
            String sessionGroupId) {
    }

    private record SessionPlacement(
            Long teachingAssignmentId,
            Long roomId) {
    }
}
