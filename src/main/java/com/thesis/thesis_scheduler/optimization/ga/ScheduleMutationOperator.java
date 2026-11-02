
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ScheduleMutationOperator {

    public ScheduleChromosome mutate(
            ScheduleChromosome chromosome,
            List<Timeslot> availableTimeslots,
            List<Room> availableRooms,
            double mutationRate) {

        validateInputs(
                chromosome,
                availableTimeslots,
                availableRooms,
                mutationRate
        );

        List<ScheduleGene> mutatedGenes =
                chromosome.getGenes().stream()
                        .map(this::copyGene)
                        .toList();

        ScheduleChromosome offspring =
                new ScheduleChromosome(
                        new ArrayList<>(mutatedGenes)
                );

        ThreadLocalRandom random =
                ThreadLocalRandom.current();

        if (random.nextDouble() >= mutationRate) {
            return offspring;
        }

        Map<SessionKey, List<ScheduleGene>> sessions =
                groupSessions(offspring);

        if (sessions.isEmpty()) {
            return offspring;
        }

        List<SessionKey> sessionKeys =
                new ArrayList<>(sessions.keySet());

        SessionKey selectedKey =
                sessionKeys.get(random.nextInt(sessionKeys.size()));

        List<ScheduleGene> selectedSession =
                sessions.get(selectedKey);

        List<ScheduleGene> orderedSession =
                new ArrayList<>(selectedSession);

        Map<Long, Timeslot> timeslotMap =
                new HashMap<>();

        for (Timeslot timeslot : availableTimeslots) {
            timeslotMap.put(timeslot.getTimeslotId(), timeslot);
        }

        orderedSession.sort(
                Comparator.comparing(gene ->
                        getTimeslot(gene, timeslotMap).getStartTime())
        );

        List<List<Timeslot>> possiblePlacements =
                findPossiblePlacements(
                        orderedSession,
                        availableTimeslots,
                        timeslotMap
                );

        if (possiblePlacements.isEmpty()) {
            return offspring;
        }

        List<Timeslot> selectedPlacement =
                possiblePlacements.get(
                        random.nextInt(possiblePlacements.size())
                );

        List<Room> rooms =
                new ArrayList<>(availableRooms);

        rooms.removeIf(room -> room.getRoomId() == null);

        if (rooms.isEmpty()) {
            return offspring;
        }

        Room selectedRoom =
                rooms.get(random.nextInt(rooms.size()));

        for (int i = 0; i < orderedSession.size(); i++) {

            ScheduleGene gene = orderedSession.get(i);

            gene.setTimeslotId(
                    selectedPlacement.get(i).getTimeslotId()
            );

            gene.setRoomId(selectedRoom.getRoomId());
        }

        return offspring;
    }

    private List<List<Timeslot>> findPossiblePlacements(
            List<ScheduleGene> session,
            List<Timeslot> availableTimeslots,
            Map<Long, Timeslot> timeslotMap) {

        List<List<Timeslot>> placements =
                new ArrayList<>();

        List<Timeslot> sortedTimeslots =
                availableTimeslots.stream()
                        .filter(slot ->
                                slot.getTimeslotId() != null
                                        && slot.getDayOfWeek() != null
                                        && slot.getStartTime() != null
                                        && slot.getEndTime() != null
                                        && slot.getDurationMinutes() != null
                                        && slot.getDurationMinutes() > 0
                        )
                        .sorted(
                                Comparator.comparing(
                                        Timeslot::getDayOfWeek
                                ).thenComparing(
                                        Timeslot::getStartTime
                                )
                        )
                        .toList();

        for (int i = 0;
             i + session.size() <= sortedTimeslots.size();
             i++) {

            List<Timeslot> candidate =
                    sortedTimeslots.subList(
                            i,
                            i + session.size()
                    );

            if (isContiguous(candidate, session, timeslotMap)) {
                placements.add(new ArrayList<>(candidate));
            }
        }

        return placements;
    }

    private boolean isContiguous(
            List<Timeslot> candidate,
            List<ScheduleGene> originalSession,
            Map<Long, Timeslot> timeslotMap) {

        if (candidate.size() != originalSession.size()) {
            return false;
        }

        Short day = candidate.get(0).getDayOfWeek();

        int expectedDuration = 0;

        for (ScheduleGene gene : originalSession) {

            Timeslot original =
                    getTimeslot(gene, timeslotMap);

            expectedDuration += original.getDurationMinutes();
        }

        int candidateDuration = 0;

        for (int i = 0; i < candidate.size(); i++) {

            Timeslot current = candidate.get(i);

            if (!day.equals(current.getDayOfWeek())) {
                return false;
            }

            candidateDuration += current.getDurationMinutes();

            if (i > 0) {

                Timeslot previous = candidate.get(i - 1);

                if (!previous.getEndTime().equals(
                        current.getStartTime())) {
                    return false;
                }
            }
        }

        return expectedDuration == candidateDuration;
    }

    private Map<SessionKey, List<ScheduleGene>> groupSessions(
            ScheduleChromosome chromosome) {

        Map<SessionKey, List<ScheduleGene>> sessions =
                new HashMap<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene.getTeachingAssignmentId() == null
                    || gene.getSessionGroupId() == null
                    || gene.getSessionGroupId().isBlank()) {
                throw new IllegalArgumentException(
                        "Genes must have assignment and session group IDs.");
            }

            SessionKey key = new SessionKey(
                    gene.getTeachingAssignmentId(),
                    gene.getSessionGroupId()
            );

            sessions.computeIfAbsent(
                    key,
                    ignored -> new ArrayList<>()
            ).add(gene);
        }

        return sessions;
    }

    private Timeslot getTimeslot(
            ScheduleGene gene,
            Map<Long, Timeslot> timeslotMap) {

        Timeslot timeslot =
                timeslotMap.get(gene.getTimeslotId());

        if (timeslot == null) {
            throw new IllegalStateException(
                    "Timeslot not found: " + gene.getTimeslotId());
        }

        return timeslot;
    }

    private ScheduleGene copyGene(ScheduleGene source) {

        ScheduleGene copy = new ScheduleGene(
                source.getTeachingAssignmentId(),
                source.getInstructorId(),
                source.getSectionId(),
                source.getRoomId(),
                source.getTimeslotId()
        );

        copy.setSessionType(source.getSessionType());
        copy.setSessionGroupId(source.getSessionGroupId());

        return copy;
    }

    private void validateInputs(
            ScheduleChromosome chromosome,
            List<Timeslot> timeslots,
            List<Room> rooms,
            double mutationRate) {

        if (chromosome == null
                || chromosome.getGenes() == null
                || chromosome.getGenes().isEmpty()) {
            throw new IllegalArgumentException(
                    "Chromosome must contain genes.");
        }

        if (timeslots == null || timeslots.isEmpty()) {
            throw new IllegalArgumentException(
                    "Available timeslots must not be empty.");
        }

        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException(
                    "Available rooms must not be empty.");
        }

        if (!Double.isFinite(mutationRate)
                || mutationRate < 0
                || mutationRate > 1) {
            throw new IllegalArgumentException(
                    "Mutation rate must be between 0 and 1.");
        }
    }

    private record SessionKey(
            Long teachingAssignmentId,
            String sessionGroupId) {
    }
}
