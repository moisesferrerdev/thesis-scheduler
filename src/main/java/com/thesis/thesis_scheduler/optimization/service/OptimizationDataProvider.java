
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.entity.*;
import com.thesis.thesis_scheduler.repository.*;

import com.thesis.thesis_scheduler.optimization.fitness.PreferredTeachingScheduleEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredRoomAssignmentEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.FullTimeFacultyPrioritizationEvaluator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OptimizationDataProvider {

    private static final String ACTIVE = "ACTIVE";
    private static final String FULL_TIME = "FULL_TIME";
    private static final String AVAILABLE = "AVAILABLE";

    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final InstructorRepository instructorRepository;
    private final InstructorQualificationRepository qualificationRepository;
    private final InstructorAvailabilityRepository instructorAvailabilityRepository;
    private final SchedulingConstraintRepository schedulingConstraintRepository;

    public OptimizationDataProvider(
            TeachingAssignmentRepository teachingAssignmentRepository,
            InstructorRepository instructorRepository,
            InstructorQualificationRepository qualificationRepository,
            InstructorAvailabilityRepository instructorAvailabilityRepository,
            SchedulingConstraintRepository schedulingConstraintRepository) {

        this.teachingAssignmentRepository = teachingAssignmentRepository;
        this.instructorRepository = instructorRepository;
        this.qualificationRepository = qualificationRepository;
        this.instructorAvailabilityRepository = instructorAvailabilityRepository;
        this.schedulingConstraintRepository = schedulingConstraintRepository;
    }

    @Transactional(readOnly = true)
    public OptimizationInput prepare(Long termId) {

        if (termId == null) {
            throw new IllegalArgumentException("Term ID is required.");
        }

        List<TeachingAssignment> assignments =
                teachingAssignmentRepository.findActiveAssignmentsByTermId(termId);

        if (assignments == null || assignments.isEmpty()) {
            throw new IllegalStateException(
                    "No active teaching assignments found for term ID: " + termId);
        }

        List<SchedulingConstraint> activeConstraints =
                schedulingConstraintRepository
                        .findByTerm_TermIdAndActive(termId, true);

        Map<Long, FullTimeFacultyPrioritizationEvaluator
                .AssignmentPriorityAssessment> mc2Assessments =
                buildMc2Assessments(assignments, termId);

        // No preference tables currently exist.
        // Empty maps mean no SC1 or SC2 preference penalties.
        Map<Long, PreferredTeachingScheduleEvaluator
                .InstructorTeachingPreference> instructorPreferences =
                Collections.emptyMap();

        Map<Long, PreferredRoomAssignmentEvaluator
                .PreferredRoomPreference> roomPreferences =
                Collections.emptyMap();

        return new OptimizationInput(
                assignments,
                activeConstraints,
                instructorPreferences,
                roomPreferences,
                mc2Assessments
        );
    }

    private Map<Long, FullTimeFacultyPrioritizationEvaluator
            .AssignmentPriorityAssessment> buildMc2Assessments(
            List<TeachingAssignment> assignments,
            Long termId) {

        Map<Long, FullTimeFacultyPrioritizationEvaluator
                .AssignmentPriorityAssessment> assessments =
                new HashMap<>();

        List<Instructor> instructors = instructorRepository.findAll();

        for (TeachingAssignment assignment : assignments) {

            Long assignmentId = assignment.getAssignmentId();

            Instructor assignedInstructor = assignment.getInstructor();

            SectionSubject sectionSubject = assignment.getSectionSubject();

            Long subjectId = sectionSubject
                    .getOffering()
                    .getSubject()
                    .getSubjectId();

            Set<Long> eligibleFullTimeInstructorIds = new HashSet<>();

            for (Instructor candidate : instructors) {

                if (!FULL_TIME.equalsIgnoreCase(candidate.getEmploymentType())
                        || !ACTIVE.equalsIgnoreCase(candidate.getStatus())) {
                    continue;
                }

                Long candidateId = candidate.getInstructorId();

                // Check approved subject qualification.
                boolean qualified = qualificationRepository
                        .findByInstructor_InstructorIdAndApproved(
                                candidateId, true)
                        .stream()
                        .anyMatch(q -> q.getSubject() != null
                                && subjectId.equals(q.getSubject().getSubjectId()));

                if (!qualified) {
                    continue;
                }

                // Check whether the instructor has available periods
                // during the selected academic term.
                boolean hasAvailability = instructorAvailabilityRepository
                        .findByInstructor_InstructorIdAndTerm_TermId(
                                candidateId, termId)
                        .stream()
                        .anyMatch(a -> AVAILABLE.equalsIgnoreCase(
                                a.getAvailabilityType()));

                if (!hasAvailability) {
                    continue;
                }

                // Calculate the candidate's current assigned teaching load.
                BigDecimal currentLoad = teachingAssignmentRepository
                        .sumAssignedLoadHoursByInstructor(candidateId, null);

                if (currentLoad == null) {
                    currentLoad = BigDecimal.ZERO;
                }

                BigDecimal assignmentLoad = assignment.getAssignedLoadHours();

                BigDecimal maximumLoad = candidate.getMaxLoadHours();

                if (assignmentLoad == null || maximumLoad == null) {
                    continue;
                }

                if (currentLoad.add(assignmentLoad)
                        .compareTo(maximumLoad) <= 0) {

                    eligibleFullTimeInstructorIds.add(candidateId);
                }
            }

            assessments.put(
                    assignmentId,
                    new FullTimeFacultyPrioritizationEvaluator
                            .AssignmentPriorityAssessment(
                            assignedInstructor.getEmploymentType(),
                            eligibleFullTimeInstructorIds,
                            1
                    )
            );
        }

        return assessments;
    }

    public record OptimizationInput(
            List<TeachingAssignment> assignments,
            List<SchedulingConstraint> activeConstraints,
            Map<Long, PreferredTeachingScheduleEvaluator
                    .InstructorTeachingPreference> instructorPreferences,
            Map<Long, PreferredRoomAssignmentEvaluator
                    .PreferredRoomPreference> roomPreferences,
            Map<Long, FullTimeFacultyPrioritizationEvaluator
                    .AssignmentPriorityAssessment> mc2Assessments
    ) {}
}
