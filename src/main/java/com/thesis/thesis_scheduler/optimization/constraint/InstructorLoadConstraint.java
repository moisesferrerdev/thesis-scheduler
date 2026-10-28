
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.entity.Instructor;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class InstructorLoadConstraint {

    private final TeachingAssignmentRepository teachingAssignmentRepository;

    public boolean hasConflict(ScheduleChromosome chromosome) {

        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        Map<Long, BigDecimal> instructorLoads = new HashMap<>();
        Map<Long, BigDecimal> maximumLoads = new HashMap<>();
        Set<Long> processedAssignments = new HashSet<>();

        for (ScheduleGene gene : chromosome.getGenes()) {
            if (gene == null || gene.getTeachingAssignmentId() == null) {
                continue;
            }

            Long assignmentId = gene.getTeachingAssignmentId();

            // Count each teaching assignment only once.
            if (!processedAssignments.add(assignmentId)) {
                continue;
            }

            TeachingAssignment assignment = teachingAssignmentRepository
                    .findById(assignmentId)
                    .orElse(null);

            if (assignment == null
                    || assignment.getInstructor() == null
                    || assignment.getAssignedLoadHours() == null
                    || assignment.getInstructor().getMaxLoadHours() == null) {
                return true;
            }

            Instructor instructor = assignment.getInstructor();
            Long instructorId = instructor.getInstructorId();

            instructorLoads.merge(
                    instructorId,
                    assignment.getAssignedLoadHours(),
                    BigDecimal::add
            );

            maximumLoads.putIfAbsent(
                    instructorId,
                    (instructor.getMaxLoadHours())
            );
        }

        for (Map.Entry<Long, BigDecimal> entry : instructorLoads.entrySet()) {
            BigDecimal maximumLoad = maximumLoads.get(entry.getKey());

            if (maximumLoad == null
                    || entry.getValue().compareTo(maximumLoad) > 0) {
                return true;
            }
        }

        return false;
    }
}
