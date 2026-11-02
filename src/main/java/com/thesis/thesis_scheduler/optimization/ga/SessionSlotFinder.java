
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Timeslot;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class SessionSlotFinder {

    private static final int BLOCK_DURATION_MINUTES = 30;

    /**
     * Finds all possible consecutive timeslot sequences
     * for the requested duration.
     *
     * Each returned list represents one possible session.
     */
    public List<List<Timeslot>> findSessions(
            List<Timeslot> timeslots,
            Long termId,
            Short dayOfWeek,
            int durationMinutes) {

        if (timeslots == null) {
            throw new IllegalArgumentException(
                    "Timeslots must not be null.");
        }

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Term ID must be a positive value.");
        }

        if (dayOfWeek == null
                || dayOfWeek < 1
                || dayOfWeek > 6) {
            throw new IllegalArgumentException(
                    "Day of week must be between 1 (Monday) "
                            + "and 6 (Saturday).");
        }

        if (durationMinutes <= 0
                || durationMinutes % BLOCK_DURATION_MINUTES != 0) {
            throw new IllegalArgumentException(
                    "Duration must be a positive multiple of 30 minutes.");
        }

        int requiredBlocks =
                durationMinutes / BLOCK_DURATION_MINUTES;

        List<Timeslot> dailySlots = timeslots.stream()
                .filter(Objects::nonNull)
                .filter(slot -> slot.getTerm() != null)
                .filter(slot -> slot.getTerm().getTermId().equals(termId))
                .filter(slot -> slot.getDayOfWeek() != null
                        && slot.getDayOfWeek().equals(dayOfWeek))
                .filter(slot -> slot.getStartTime() != null
                        && slot.getEndTime() != null)
                .filter(slot -> slot.getDurationMinutes() != null
                        && slot.getDurationMinutes()
                        == BLOCK_DURATION_MINUTES)
                .filter(slot -> slot.getEndTime().isAfter(
                        slot.getStartTime()))
                .sorted(Comparator.comparing(Timeslot::getStartTime))
                .toList();

        List<List<Timeslot>> sessions = new ArrayList<>();

        for (int start = 0;
             start + requiredBlocks <= dailySlots.size();
             start++) {

            List<Timeslot> candidate = new ArrayList<>();
            boolean consecutive = true;

            for (int offset = 0;
                 offset < requiredBlocks;
                 offset++) {

                Timeslot current = dailySlots.get(start + offset);

                if (offset > 0) {
                    Timeslot previous =
                            dailySlots.get(start + offset - 1);

                    LocalTime previousEnd = previous.getEndTime();
                    LocalTime currentStart = current.getStartTime();

                    if (!previousEnd.equals(currentStart)) {
                        consecutive = false;
                        break;
                    }
                }

                candidate.add(current);
            }

            if (consecutive) {
                sessions.add(List.copyOf(candidate));
            }
        }

        return List.copyOf(sessions);
    }
}
