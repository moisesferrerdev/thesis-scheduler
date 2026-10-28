
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.optimization.model.ConstraintViolation;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.repository.InstructorAvailabilityRepository;
import com.thesis.thesis_scheduler.repository.RoomAvailabilityRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import com.thesis.thesis_scheduler.repository.SectionSubjectRepository;
import com.thesis.thesis_scheduler.repository.StudentSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConstraintEvaluator {

    private final InstructorAvailabilityRepository instructorAvailabilityRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final TimeslotRepository timeslotRepository;
    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final SectionSubjectRepository sectionSubjectRepository;
    private final StudentSectionRepository studentSectionRepository;

    private final RoomCapacityConstraint roomCapacityConstraint =
            new RoomCapacityConstraint();

    private final RoomConflictConstraint roomConflictConstraint =
            new RoomConflictConstraint();

    private final InstructorConflictConstraint instructorConflictConstraint =
            new InstructorConflictConstraint();

    private final SectionConflictConstraint sectionConflictConstraint =
            new SectionConflictConstraint();

    public List<ConstraintViolation> evaluate(
            ScheduleChromosome chromosome,
            Long termId,
            RoomCapacityConstraint.CapacityLookup capacityLookup) {

        List<ConstraintViolation> violations = new ArrayList<>();

        if (roomConflictConstraint.hasConflict(chromosome)) {
            violations.add(new ConstraintViolation(
                    "ROOM_CONFLICT",
                    "Multiple assignments use the same room and timeslot.",
                    1
            ));
        }

        if (instructorConflictConstraint.hasConflict(chromosome)) {
            violations.add(new ConstraintViolation(
                    "INSTRUCTOR_CONFLICT",
                    "An instructor has multiple assignments in the same timeslot.",
                    1
            ));
        }

        if (sectionConflictConstraint.hasConflict(chromosome)) {
            violations.add(new ConstraintViolation(
                    "SECTION_CONFLICT",
                    "A section has multiple assignments in the same timeslot.",
                    1
            ));
        }

        if (capacityLookup != null
                && roomCapacityConstraint.hasConflict(
                chromosome, capacityLookup)) {
            violations.add(new ConstraintViolation(
                    "ROOM_CAPACITY",
                    "A room cannot accommodate the assigned section.",
                    1
            ));
        }

        InstructorAvailabilityConstraint instructorAvailabilityConstraint =
                new InstructorAvailabilityConstraint(
                        instructorAvailabilityRepository,
                        timeslotRepository
                );

        if (instructorAvailabilityConstraint.hasConflict(
                chromosome, termId)) {
            violations.add(new ConstraintViolation(
                    "INSTRUCTOR_AVAILABILITY",
                    "An instructor is scheduled outside permitted availability.",
                    1
            ));
        }

        RoomAvailabilityConstraint roomAvailabilityConstraint =
                new RoomAvailabilityConstraint(
                        roomAvailabilityRepository,
                        timeslotRepository
                );

        if (roomAvailabilityConstraint.hasConflict(chromosome, termId)) {
            violations.add(new ConstraintViolation(
                    "ROOM_AVAILABILITY",
                    "A room is scheduled outside permitted availability.",
                    1
            ));
        }

        InstructorLoadConstraint instructorLoadConstraint =
                new InstructorLoadConstraint(teachingAssignmentRepository);

        if (instructorLoadConstraint.hasConflict(chromosome)) {
            violations.add(new ConstraintViolation(
                    "MAXIMUM_TEACHING_LOAD",
                    "An instructor exceeds the maximum teaching load.",
                    1
            ));
        }

        CurriculumCompletenessConstraint curriculumConstraint =
                new CurriculumCompletenessConstraint(
                        studentSectionRepository,
                        sectionSubjectRepository,
                        teachingAssignmentRepository
                );

        if (curriculumConstraint.hasConflict(chromosome, termId)) {
            violations.add(new ConstraintViolation(
                    "CURRICULUM_COMPLETENESS",
                    "A required section-subject is missing from the schedule.",
                    1
            ));
        }

        return violations;
    }
}
