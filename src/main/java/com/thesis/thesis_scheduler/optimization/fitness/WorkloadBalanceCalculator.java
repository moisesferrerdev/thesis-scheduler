
package com.thesis.thesis_scheduler.optimization.fitness;

import com.thesis.thesis_scheduler.entity.Instructor;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.InstructorRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkloadBalanceCalculator {

    private final InstructorRepository instructorRepository;

    private final TimeslotRepository timeslotRepository;

    public double calculateWorkloadBalance(
            ScheduleChromosome chromosome) {

        if (chromosome == null || chromosome.getGenes() == null) {
            throw new IllegalArgumentException(
                    "Schedule chromosome and genes must not be null.");
        }

        if (chromosome.getGenes().isEmpty()) {
            throw new IllegalArgumentException(
                    "Schedule chromosome must contain genes.");
        }

        Set<Long> instructorIds = new HashSet<>();
        Set<Long> timeslotIds = new HashSet<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null
                    || gene.getInstructorId() == null
                    || gene.getTimeslotId() == null) {
                throw new IllegalArgumentException(
                        "Each schedule gene must have an instructor ID "
                                + "and timeslot ID.");
            }

            instructorIds.add(gene.getInstructorId());
            timeslotIds.add(gene.getTimeslotId());
        }

        Map<Long, Instructor> instructors =
                instructorRepository.findAllById(instructorIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Instructor::getInstructorId,
                                instructor -> instructor
                        ));

        Map<Long, Timeslot> timeslots =
                timeslotRepository.findAllById(timeslotIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Timeslot::getTimeslotId,
                                timeslot -> timeslot
                        ));

        validateResources(instructorIds, timeslotIds,
                instructors, timeslots);

        // Store the actual scheduled hours per instructor.
        Map<Long, BigDecimal> actualLoads = new HashMap<>();

        for (Long instructorId : instructorIds) {
            actualLoads.put(instructorId, BigDecimal.ZERO);
        }

        // Every gene represents one scheduled timeslot block.
        for (ScheduleGene gene : chromosome.getGenes()) {

            Timeslot timeslot =
                    timeslots.get(gene.getTimeslotId());

            BigDecimal durationHours =
                    BigDecimal.valueOf(timeslot.getDurationMinutes())
                            .divide(BigDecimal.valueOf(60));

            Long instructorId = gene.getInstructorId();

            actualLoads.merge(
                    instructorId,
                    durationHours,
                    BigDecimal::add
            );
        }

        // WB = Sum of (Actual Load - Target Load)^2
        BigDecimal workloadBalance = BigDecimal.ZERO;

        for (Long instructorId : instructorIds) {

            Instructor instructor = instructors.get(instructorId);

            BigDecimal targetLoad =
                    instructor.getTargetLoadHours();

            BigDecimal actualLoad =
                    actualLoads.get(instructorId);

            if (targetLoad == null || targetLoad.signum() < 0) {
                throw new IllegalStateException(
                        "Invalid target workload for instructor ID "
                                + instructorId);
            }

            BigDecimal difference =
                    actualLoad.subtract(targetLoad);

            BigDecimal squaredDifference =
                    difference.multiply(difference);

            workloadBalance =
                    workloadBalance.add(squaredDifference);
        }

        return workloadBalance.doubleValue();
    }

    private void validateResources(
            Set<Long> instructorIds,
            Set<Long> timeslotIds,
            Map<Long, Instructor> instructors,
            Map<Long, Timeslot> timeslots) {

        if (instructors.size() != instructorIds.size()) {
            throw new IllegalStateException(
                    "One or more instructors referenced by the schedule "
                            + "could not be found.");
        }

        if (timeslots.size() != timeslotIds.size()) {
            throw new IllegalStateException(
                    "One or more timeslots referenced by the schedule "
                            + "could not be found.");
        }

        for (Timeslot timeslot : timeslots.values()) {

            if (timeslot.getDurationMinutes() == null
                    || timeslot.getDurationMinutes() <= 0) {
                throw new IllegalStateException(
                        "Timeslot must have a valid duration. Timeslot ID: "
                                + timeslot.getTimeslotId());
            }
        }
    }
}
