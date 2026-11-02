
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.optimization.ga.InstructorScheduleAssessment.SchedulingProfile;
import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.entity.Timeslot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SessionPatternPlacementGenerator {

    private final SessionDurationPlanner sessionDurationPlanner;
    private final SessionSlotFinder sessionSlotFinder;
    private final SessionPlacementBuilder sessionPlacementBuilder;

    public List<List<ScheduleGene>> generatePlacements(
            ScheduleChromosome existingSchedule,
            TeachingAssignment assignment,
            Room room,
            List<Timeslot> availableTimeslots,
            BigDecimal requiredHours,
            String sessionType,
            SchedulingProfile profile,
            Long termId) {

        validateInputs(
                existingSchedule,
                assignment,
                room,
                availableTimeslots,
                requiredHours,
                sessionType,
                profile,
                termId
        );

        List<List<Integer>> patterns =
                sessionDurationPlanner.generateSessionPatterns(
                        requiredHours,
                        sessionType,
                        profile
                );

        List<List<ScheduleGene>> placements = new ArrayList<>();

        for (List<Integer> pattern : patterns) {

            generatePatternPlacements(
                    existingSchedule,
                    assignment,
                    room,
                    availableTimeslots,
                    pattern,
                    sessionType,
                    termId,
                    0,
                    new HashSet<>(),
                    new ArrayList<>(),
                    placements
            );
        }

        return placements.stream()
                .map(List::copyOf)
                .toList();
    }

    private void generatePatternPlacements(
            ScheduleChromosome existingSchedule,
            TeachingAssignment assignment,
            Room room,
            List<Timeslot> availableTimeslots,
            List<Integer> pattern,
            String sessionType,
            Long termId,
            int sessionIndex,
            Set<Short> usedDays,
            List<ScheduleGene> currentPlacement,
            List<List<ScheduleGene>> placements) {

        // All sessions in this pattern have been placed.
        if (sessionIndex == pattern.size()) {
            placements.add(new ArrayList<>(currentPlacement));
            return;
        }

        int durationMinutes = pattern.get(sessionIndex);

        List<Timeslot> orderedTimeslots = availableTimeslots.stream()
                .filter(slot -> slot.getDayOfWeek() != null)
                .filter(slot -> !usedDays.contains(slot.getDayOfWeek()))
                .sorted(Comparator
                        .comparing(Timeslot::getDayOfWeek)
                        .thenComparing(Timeslot::getStartTime))
                .toList();

        Set<Short> checkedDays = new HashSet<>();

        for (Timeslot timeslot : orderedTimeslots) {

            Short dayOfWeek = timeslot.getDayOfWeek();

            // Only evaluate each day once.
            if (!checkedDays.add(dayOfWeek)) {
                continue;
            }

            List<List<Timeslot>> possibleSessions =
                    sessionSlotFinder.findSessions(
                            availableTimeslots,
                            termId,
                            dayOfWeek,
                            durationMinutes
                    );

            for (List<Timeslot> sessionSlots : possibleSessions) {

                ScheduleChromosome scheduleToCheck =
                        createSchedule(existingSchedule, currentPlacement);

                List<ScheduleGene> proposedSession =
                        sessionPlacementBuilder.buildSession(
                                scheduleToCheck,
                                assignment,
                                room,
                                sessionSlots,
                                sessionType,
                                termId
                        );

                if (proposedSession.isEmpty()) {
                    continue;
                }

                currentPlacement.addAll(proposedSession);
                usedDays.add(dayOfWeek);

                generatePatternPlacements(
                        existingSchedule,
                        assignment,
                        room,
                        availableTimeslots,
                        pattern,
                        sessionType,
                        termId,
                        sessionIndex + 1,
                        usedDays,
                        currentPlacement,
                        placements
                );

                // Backtrack so other possible placements can be explored.
                currentPlacement.subList(
                        currentPlacement.size() - proposedSession.size(),
                        currentPlacement.size()
                ).clear();

                usedDays.remove(dayOfWeek);
            }
        }
    }

    private ScheduleChromosome createSchedule(
            ScheduleChromosome existingSchedule,
            List<ScheduleGene> currentPlacement) {

        List<ScheduleGene> combinedGenes = new ArrayList<>();

        if (existingSchedule != null && existingSchedule.getGenes() != null) {
            combinedGenes.addAll(existingSchedule.getGenes());
        }

        combinedGenes.addAll(currentPlacement);

        return new ScheduleChromosome(combinedGenes);
    }

    private void validateInputs(
            ScheduleChromosome existingSchedule,
            TeachingAssignment assignment,
            Room room,
            List<Timeslot> availableTimeslots,
            BigDecimal requiredHours,
            String sessionType,
            SchedulingProfile profile,
            Long termId) {

        if (assignment == null) {
            throw new IllegalArgumentException("Teaching assignment is required.");
        }

        if (assignment.getInstructor() == null
                || assignment.getSectionSubject() == null
                || assignment.getSectionSubject().getSection() == null) {
            throw new IllegalArgumentException(
                    "Assignment must have an instructor and section."
            );
        }

        if (room == null || room.getRoomId() == null) {
            throw new IllegalArgumentException("A valid room is required.");
        }

        if (availableTimeslots == null || availableTimeslots.isEmpty()) {
            throw new IllegalArgumentException("Available timeslots are required.");
        }

        if (requiredHours == null || requiredHours.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Required hours must be greater than zero."
            );
        }

        if (sessionType == null
                || (!sessionType.equalsIgnoreCase("LECTURE")
                && !sessionType.equalsIgnoreCase("LABORATORY"))) {
            throw new IllegalArgumentException(
                    "Session type must be LECTURE or LABORATORY."
            );
        }

        if (profile == null) {
            throw new IllegalArgumentException(
                    "Instructor scheduling profile is required."
            );
        }

        if (termId == null) {
            throw new IllegalArgumentException("Academic term ID is required.");
        }
    }
}
