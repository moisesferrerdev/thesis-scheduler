
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ScheduleCrossoverOperator {

    public ScheduleChromosome crossover(
            ScheduleChromosome parent1,
            ScheduleChromosome parent2) {

        validateParents(parent1, parent2);

        Map<Long, List<ScheduleGene>> parent1Assignments =
                groupByTeachingAssignment(parent1);

        Map<Long, List<ScheduleGene>> parent2Assignments =
                groupByTeachingAssignment(parent2);

        if (!parent1Assignments.keySet()
                .equals(parent2Assignments.keySet())) {
            throw new IllegalArgumentException(
                    "Both parents must contain the same teaching assignments.");
        }

        List<ScheduleGene> offspringGenes = new ArrayList<>();

        ThreadLocalRandom random =
                ThreadLocalRandom.current();

        for (Long assignmentId : parent1Assignments.keySet()) {

            List<ScheduleGene> selectedGenes =
                    random.nextBoolean()
                            ? parent1Assignments.get(assignmentId)
                            : parent2Assignments.get(assignmentId);

            for (ScheduleGene gene : selectedGenes) {
                offspringGenes.add(copyGene(gene));
            }
        }

        if (offspringGenes.isEmpty()) {
            throw new IllegalStateException(
                    "Crossover produced an empty offspring schedule.");
        }

        return new ScheduleChromosome(offspringGenes);
    }

    private Map<Long, List<ScheduleGene>> groupByTeachingAssignment(
            ScheduleChromosome chromosome) {

        Map<Long, List<ScheduleGene>> groupedGenes =
                new HashMap<>();

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null || gene.getTeachingAssignmentId() == null) {
                throw new IllegalArgumentException(
                        "Every gene must have a teaching assignment ID.");
            }

            groupedGenes.computeIfAbsent(
                    gene.getTeachingAssignmentId(),
                    key -> new ArrayList<>()
            ).add(gene);
        }

        return groupedGenes;
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

    private void validateParents(
            ScheduleChromosome parent1,
            ScheduleChromosome parent2) {

        if (parent1 == null || parent2 == null) {
            throw new IllegalArgumentException(
                    "Parent chromosomes must not be null.");
        }

        if (parent1.getGenes() == null
                || parent2.getGenes() == null
                || parent1.getGenes().isEmpty()
                || parent2.getGenes().isEmpty()) {

            throw new IllegalArgumentException(
                    "Both parent chromosomes must contain genes.");
        }
    }
}
