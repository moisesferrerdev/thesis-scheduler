 package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionPlacementBuilder {

    private static final int BLOCK_DURATION_MINUTES = 30;

    private static final String LECTURE = "LECTURE";
    private static final String LABORATORY = "LABORATORY";

    private final SessionFeasibilityChecker sessionFeasibilityChecker;

    public List<ScheduleGene> buildSession(
            ScheduleChromosome existingSchedule,
            TeachingAssignment assignment,
            Room room,
            List<Timeslot> sessionSlots,
            String sessionType,
            Long termId) {

        validateInputs(
                existingSchedule,
                assignment,
                room,
                sessionSlots,
                sessionType,
                termId
        );

        if (assignment.getInstructor() == null
                || assignment.getInstructor().getInstructorId() == null) {
            throw new IllegalArgumentException(
                    "Teaching assignment must have a valid instructor."
            );
        }

        if (assignment.getSectionSubject() == null
                || assignment.getSectionSubject().getSection() == null
                || assignment.getSectionSubject().getSection().getSectionId() == null) {
            throw new IllegalArgumentException(
                    "Teaching assignment must have a valid student section."
            );
        }

        if (room.getRoomId() == null) {
            throw new IllegalArgumentException(
                    "Room must have a valid ID."
            );
        }

        validateConsecutiveSlots(sessionSlots, termId);

        Long assignmentId = assignment.getAssignmentId();
        Long instructorId =
                assignment.getInstructor().getInstructorId();

        Long sectionId =
                assignment.getSectionSubject()
                        .getSection()
                        .getSectionId();

        Long roomId = room.getRoomId();

        String sessionGroupId = UUID.randomUUID().toString();

        List<ScheduleGene> proposedGenes = new ArrayList<>();

        for (Timeslot timeslot : sessionSlots) {

            ScheduleGene gene = new ScheduleGene(
                    assignmentId,
                    instructorId,
                    sectionId,
                    roomId,
                    timeslot.getTimeslotId()
            );

            gene.setSessionType(sessionType);
            gene.setSessionGroupId(sessionGroupId);

            proposedGenes.add(gene);
        }

        List<ScheduleGene> result = List.copyOf(proposedGenes);

        if (!sessionFeasibilityChecker.canAddSession(
                existingSchedule,
                result,
                termId
        )) {
            return List.of();
        }

        return result;
    }

    private void validateInputs(
            ScheduleChromosome existingSchedule,
            TeachingAssignment assignment,
            Room room,
            List<Timeslot> sessionSlots,
            String sessionType,
            Long termId) {

        if (existingSchedule == null
                || existingSchedule.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Existing schedule must not be null."
            );
        }

        if (assignment == null || assignment.getAssignmentId() == null) {
            throw new IllegalArgumentException(
                    "A valid teaching assignment is required."
            );
        }

        if (room == null || room.getRoomId() == null) {
            throw new IllegalArgumentException(
                    "A valid room is required."
            );
        }

        if (sessionSlots == null || sessionSlots.isEmpty()) {
            throw new IllegalArgumentException(
                    "Session timeslots must not be empty."
            );
        }

        if (!LECTURE.equals(sessionType)
                && !LABORATORY.equals(sessionType)) {
            throw new IllegalArgumentException(
                    "Session type must be LECTURE or LABORATORY."
            );
        }

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Term ID must be a positive value."
            );
        }
    }

    private void validateConsecutiveSlots(
            List<Timeslot> sessionSlots,
            Long termId) {

        for (int i = 0; i < sessionSlots.size(); i++) {

            Timeslot current = sessionSlots.get(i);

            if (current == null
                    || current.getTimeslotId() == null
                    || current.getTerm() == null
                    || current.getTerm().getTermId() == null
                    || !current.getTerm().getTermId().equals(termId)
                    || current.getDayOfWeek() == null
                    || current.getStartTime() == null
                    || current.getEndTime() == null
                    || current.getDurationMinutes() == null
                    || current.getDurationMinutes() != BLOCK_DURATION_MINUTES) {

                throw new IllegalArgumentException(
                        "Every session timeslot must be a valid 30-minute block "
                                + "belonging to the requested academic term."
                );
            }

            if (!current.getEndTime().isAfter(current.getStartTime())) {
                throw new IllegalArgumentException(
                        "Timeslot end time must be after start time."
                );
            }

            if (i > 0) {

                Timeslot previous = sessionSlots.get(i - 1);

                if (!previous.getDayOfWeek().equals(current.getDayOfWeek())
                        || !previous.getEndTime().equals(current.getStartTime())) {

                    throw new IllegalArgumentException(
                            "Session timeslots must be consecutive "
                                    + "and belong to the same day."
                    );
                }
            }
        }
    }
}
