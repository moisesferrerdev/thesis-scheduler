
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import com.thesis.thesis_scheduler.optimization.ga.InstructorScheduleAssessment.SchedulingProfile;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialPopulationGenerator {

    private final TeachingAssignmentRepository
            teachingAssignmentRepository;

    private final CompleteScheduleValidator
            completeScheduleValidator;

    private final RoomRepository roomRepository;

    private final TimeslotRepository timeslotRepository;

    private final InstructorScheduleAssessment instructorScheduleAssessment;

    private final SessionPatternPlacementGenerator
            sessionPatternPlacementGenerator;

    public SchedulingResources loadResources(Long termId) {

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Term ID must be a positive value.");
        }

        List<TeachingAssignment> assignments =
                teachingAssignmentRepository
                        .findActiveAssignmentsByTermId(termId);

        List<Timeslot> timeslots =
                timeslotRepository
                        .findByTerm_TermId(termId);

        List<Room> rooms =
                roomRepository.findByStatus("ACTIVE");

        if (assignments.isEmpty()) {
            throw new IllegalStateException(
                    "No active teaching assignments found for term "
                            + termId);
        }

        if (timeslots.isEmpty()) {
            throw new IllegalStateException(
                    "No timeslots found for term " + termId);
        }

        if (rooms.isEmpty()) {
            throw new IllegalStateException(
                    "No active rooms found.");
        }

        return new SchedulingResources(
                assignments,
                timeslots,
                rooms
        );
    }

    public List<ScheduleChromosome> generatePopulation(
            Long termId,
            int populationSize) {

        if (populationSize <= 0) {
            throw new IllegalArgumentException(
                    "Population size must be greater than zero.");
        }

        SchedulingResources resources = loadResources(termId);

        List<ScheduleChromosome> population = new ArrayList<>();

        int maxAttempts = Math.max(populationSize * 20, 50);

        for (int attempt = 0;
             attempt < maxAttempts && population.size() < populationSize;
             attempt++) {

            ScheduleChromosome chromosome =
                    generateCandidateSchedule(resources, termId);

            if (chromosome == null) {
                continue;
            }

            CompleteScheduleValidator.ValidationResult validationResult =
                    completeScheduleValidator.validate(chromosome, termId);

            if (validationResult.feasible()) {
                population.add(chromosome);
            }
        }

        if (population.size() < populationSize) {
            throw new IllegalStateException(
                    "Unable to generate the requested initial population. "
                            + "Generated " + population.size()
                            + " out of " + populationSize
                            + " candidate schedules. "
                            + "Check instructor availability, room availability, "
                            + "timeslots, teaching assignments, and constraints."
            );
        }

        return List.copyOf(population);
    }

    private ScheduleChromosome generateCandidateSchedule(
            SchedulingResources resources,
            Long termId) {

        List<TeachingAssignment> assignments =
                new ArrayList<>(resources.assignments());

        // Randomize assignment order to create different candidate schedules.
        Collections.shuffle(assignments);

        List<ScheduleGene> scheduleGenes = new ArrayList<>();

        for (TeachingAssignment assignment : assignments) {

            if (assignment.getInstructor() == null
                    || assignment.getSectionSubject() == null
                    || assignment.getSectionSubject().getSection() == null) {
                return null;
            }

            SchedulingProfile profile =
                    instructorScheduleAssessment.assess(
                            assignment.getInstructor()
                    );

            BigDecimal lectureHours =
                    assignment.getSectionSubject()
                            .getRequiredLectureHours();

            BigDecimal laboratoryHours =
                    assignment.getSectionSubject()
                            .getRequiredLabHours();

            if (lectureHours == null || laboratoryHours == null) {
                throw new IllegalStateException(
                        "Lecture and laboratory hours must be configured "
                                + "for SectionSubject ID "
                                + assignment.getSectionSubject()
                                .getSectionSubjectId()
                );
            }

            if (lectureHours.signum() < 0
                    || laboratoryHours.signum() < 0) {
                throw new IllegalStateException(
                        "Required teaching hours cannot be negative."
                );
            }

            // Generate the lecture placement.
            if (lectureHours.signum() > 0) {

                List<ScheduleGene> lecturePlacement =
                        findRandomFeasiblePlacement(
                                scheduleGenes,
                                assignment,
                                resources,
                                lectureHours,
                                "LECTURE",
                                profile,
                                termId
                        );

                if (lecturePlacement == null) {
                    return null;
                }

                scheduleGenes.addAll(lecturePlacement);
            }

            // Generate the laboratory placement.
            if (laboratoryHours.signum() > 0) {

                List<ScheduleGene> laboratoryPlacement =
                        findRandomFeasiblePlacement(
                                scheduleGenes,
                                assignment,
                                resources,
                                laboratoryHours,
                                "LABORATORY",
                                profile,
                                termId
                        );

                if (laboratoryPlacement == null) {
                    return null;
                }

                scheduleGenes.addAll(laboratoryPlacement);
            }

            if (lectureHours.signum() == 0
                    && laboratoryHours.signum() == 0) {
                throw new IllegalStateException(
                        "An assignment must have lecture or laboratory hours."
                );
            }
        }

        if (scheduleGenes.isEmpty()) {
            return null;
        }

        return new ScheduleChromosome(scheduleGenes);
    }

    private List<ScheduleGene> findRandomFeasiblePlacement(
            List<ScheduleGene> existingGenes,
            TeachingAssignment assignment,
            SchedulingResources resources,
            BigDecimal requiredHours,
            String sessionType,
            SchedulingProfile profile,
            Long termId) {

        List<Room> randomizedRooms =
                new ArrayList<>(resources.rooms());

        Collections.shuffle(randomizedRooms);

        ScheduleChromosome existingSchedule =
                new ScheduleChromosome(
                        new ArrayList<>(existingGenes)
                );

        List<List<ScheduleGene>> possiblePlacements =
                new ArrayList<>();

        for (Room room : randomizedRooms) {

            List<List<ScheduleGene>> roomPlacements =
                    sessionPatternPlacementGenerator.generatePlacements(
                            existingSchedule,
                            assignment,
                            room,
                            resources.timeslots(),
                            requiredHours,
                            sessionType,
                            profile,
                            termId
                    );

            possiblePlacements.addAll(roomPlacements);
        }

        if (possiblePlacements.isEmpty()) {
            return null;
        }

        // Randomly select one of the feasible placements.
        Collections.shuffle(possiblePlacements);

        return possiblePlacements.get(0);
    }

    public record SchedulingResources(
            List<TeachingAssignment> assignments,
            List<Timeslot> timeslots,
            List<Room> rooms) {

        public SchedulingResources {
            assignments = List.copyOf(assignments);
            timeslots = List.copyOf(timeslots);
            rooms = List.copyOf(rooms);
        }
    }
}
