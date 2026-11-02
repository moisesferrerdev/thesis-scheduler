
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class FullTimeFacultyPrioritizationEvaluator {

    private static final String CONSTRAINT_CODE = "MC2";

    public MediumConstraintPenalty evaluate(
            ScheduleChromosome chromosome,
            Map<Long, AssignmentPriorityAssessment> assessments) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (assessments == null) {
            throw new IllegalArgumentException(
                    "Assignment priority assessments must not be null.");
        }

        // Each teaching assignment is evaluated only once,
        // regardless of how many schedule genes it contains.
        Set<Long> evaluatedAssignments = new HashSet<>();

        int totalPenalty = 0;

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null || gene.getTeachingAssignmentId() == null) {
                throw new IllegalArgumentException(
                        "Schedule gene must contain a teaching assignment ID.");
            }

            Long assignmentId = gene.getTeachingAssignmentId();

            if (!evaluatedAssignments.add(assignmentId)) {
                continue;
            }

            AssignmentPriorityAssessment assessment =
                    assessments.get(assignmentId);

            if (assessment == null) {
                throw new IllegalArgumentException(
                        "Missing MC2 assessment for teaching assignment: "
                                + assignmentId);
            }

            validateAssessment(assessment, assignmentId);

            boolean assignedToPartTime =
                    "PART_TIME".equalsIgnoreCase(
                            assessment.assignedEmploymentType());

            boolean eligibleFullTimeAvailable =
                    !assessment.eligibleAvailableFullTimeInstructorIds().isEmpty();

            // Apply a penalty only when a part-time instructor
            // receives an assignment despite a suitable full-time
            // instructor being available and having sufficient capacity.
            if (assignedToPartTime && eligibleFullTimeAvailable) {
                totalPenalty = Math.addExact(
                        totalPenalty,
                        assessment.penaltyValue()
                );
            }
        }

        return new MediumConstraintPenalty(
                CONSTRAINT_CODE,
                totalPenalty
        );
    }

    private void validateAssessment(
            AssignmentPriorityAssessment assessment,
            Long assignmentId) {

        if (assessment.assignedEmploymentType() == null
                || assessment.assignedEmploymentType().isBlank()) {
            throw new IllegalArgumentException(
                    "Employment type is missing for assignment: "
                            + assignmentId);
        }

        if (assessment.eligibleAvailableFullTimeInstructorIds() == null) {
            throw new IllegalArgumentException(
                    "Eligible full-time instructor list must not be null.");
        }

        if (assessment.eligibleAvailableFullTimeInstructorIds().contains(null)) {
            throw new IllegalArgumentException(
                    "Eligible full-time instructor IDs cannot contain null.");
        }

        if (assessment.penaltyValue() < 0) {
            throw new IllegalArgumentException(
                    "MC2 penalty cannot be negative.");
        }
    }

    public record AssignmentPriorityAssessment(
            String assignedEmploymentType,
            Set<Long> eligibleAvailableFullTimeInstructorIds,
            int penaltyValue) {

        public AssignmentPriorityAssessment {
            if (eligibleAvailableFullTimeInstructorIds != null) {
                eligibleAvailableFullTimeInstructorIds =
                        Set.copyOf(eligibleAvailableFullTimeInstructorIds);
            }
        }
    }

    public record MediumConstraintPenalty(
            String constraintCode,
            int penaltyValue) {
    }
}