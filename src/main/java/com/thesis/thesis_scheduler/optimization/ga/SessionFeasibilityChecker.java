package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.optimization.constraint.InstructorAvailabilityConstraint;
import com.thesis.thesis_scheduler.optimization.constraint.InstructorConflictConstraint;
import com.thesis.thesis_scheduler.optimization.constraint.RoomAvailabilityConstraint;
import com.thesis.thesis_scheduler.optimization.constraint.RoomCapacityConstraint;
import com.thesis.thesis_scheduler.optimization.constraint.RoomConflictConstraint;
import com.thesis.thesis_scheduler.optimization.constraint.SectionConflictConstraint;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionFeasibilityChecker {

    private final InstructorAvailabilityConstraint instructorAvailabilityConstraint;
    private final RoomAvailabilityConstraint roomAvailabilityConstraint;
    private final InstructorConflictConstraint instructorConflictConstraint;
    private final SectionConflictConstraint sectionConflictConstraint;
    private final RoomConflictConstraint roomConflictConstraint;
    private final RoomCapacityConstraint roomCapacityConstraint;
    private final RoomCapacityLookup roomCapacityLookup;

    public boolean canAddSession(
            ScheduleChromosome existingSchedule,
            List<ScheduleGene> proposedSession,
            Long termId) {

        if (existingSchedule == null
                || existingSchedule.getGenes() == null
                || proposedSession == null
                || proposedSession.isEmpty()
                || termId == null
                || termId <= 0) {
            return false;
        }

        // Reject incomplete proposed genes.
        for (ScheduleGene gene : proposedSession) {
            if (gene == null
                    || gene.getTeachingAssignmentId() == null
                    || gene.getInstructorId() == null
                    || gene.getSectionId() == null
                    || gene.getRoomId() == null
                    || gene.getTimeslotId() == null) {
                return false;
            }
        }

        // Combine the existing schedule with the proposed session.
        List<ScheduleGene> combinedGenes =
                new ArrayList<>(existingSchedule.getGenes());

        combinedGenes.addAll(proposedSession);

        ScheduleChromosome candidate =
                new ScheduleChromosome(combinedGenes);

        // Validate the combined candidate against the current constraints.
        return !instructorAvailabilityConstraint
                .hasConflict(candidate, termId)
                && !roomAvailabilityConstraint
                .hasConflict(candidate, termId)
                && !instructorConflictConstraint
                .hasConflict(candidate)
                && !sectionConflictConstraint
                .hasConflict(candidate)
                && !roomConflictConstraint
                .hasConflict(candidate)
                && !roomCapacityConstraint
                .hasConflict(candidate, roomCapacityLookup);
    }
}