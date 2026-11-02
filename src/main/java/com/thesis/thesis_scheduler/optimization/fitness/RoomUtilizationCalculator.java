
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.StudentSection;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.StudentSectionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomUtilizationCalculator {

    private final RoomRepository roomRepository;

    private final StudentSectionRepository studentSectionRepository;

    public double calculateRoomUtilization(
            ScheduleChromosome chromosome) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (chromosome.getGenes().isEmpty()) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must contain genes.");
        }

        Set<Long> roomIds = new HashSet<>();
        Set<Long> sectionIds = new HashSet<>();

        Map<SessionKey, Boolean> scheduledSessions = new HashMap<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null
                    || gene.getRoomId() == null
                    || gene.getSectionId() == null
                    || gene.getSessionGroupId() == null
                    || gene.getSessionGroupId().isBlank()) {

                throw new IllegalArgumentException(
                        "Each schedule gene must have a room ID, "
                                + "section ID, and session group ID.");
            }

            roomIds.add(gene.getRoomId());
            sectionIds.add(gene.getSectionId());

            SessionKey sessionKey = new SessionKey(
                    gene.getSessionGroupId(),
                    gene.getRoomId(),
                    gene.getSectionId()
            );

            scheduledSessions.put(sessionKey, Boolean.TRUE);
        }

        Map<Long, Room> rooms =
                roomRepository.findAllById(roomIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Room::getRoomId,
                                room -> room
                        ));

        Map<Long, StudentSection> sections =
                studentSectionRepository.findAllById(sectionIds)
                        .stream()
                        .collect(Collectors.toMap(
                                StudentSection::getSectionId,
                                section -> section
                        ));

        validateResources(roomIds, sectionIds, rooms, sections);

        double roomUtilizationPenalty = 0.0;

        for (SessionKey session : scheduledSessions.keySet()) {

            Room room = rooms.get(session.roomId());
            StudentSection section = sections.get(session.sectionId());

            Integer capacity = room.getCapacity();
            Integer studentCount = section.getStudentCount();

            if (capacity == null || capacity <= 0) {
                throw new IllegalStateException(
                        "Invalid room capacity for room ID "
                                + room.getRoomId());
            }

            if (studentCount == null || studentCount < 0) {
                throw new IllegalStateException(
                        "Invalid student count for section ID "
                                + section.getSectionId());
            }

            if (studentCount > capacity) {
                throw new IllegalStateException(
                        "Section size exceeds room capacity. Section ID: "
                                + section.getSectionId()
                                + ", Room ID: " + room.getRoomId());
            }

            double utilizationPenalty =
                    1.0 - ((double) studentCount / capacity);

            roomUtilizationPenalty += utilizationPenalty;
        }

        return roomUtilizationPenalty;
    }

    private void validateResources(
            Set<Long> roomIds,
            Set<Long> sectionIds,
            Map<Long, Room> rooms,
            Map<Long, StudentSection> sections) {

        if (rooms.size() != roomIds.size()) {
            throw new IllegalStateException(
                    "One or more rooms referenced by the schedule "
                            + "could not be found.");
        }

        if (sections.size() != sectionIds.size()) {
            throw new IllegalStateException(
                    "One or more sections referenced by the schedule "
                            + "could not be found.");
        }
    }

    private record SessionKey(
            String sessionGroupId,
            Long roomId,
            Long sectionId) {
    }
}
