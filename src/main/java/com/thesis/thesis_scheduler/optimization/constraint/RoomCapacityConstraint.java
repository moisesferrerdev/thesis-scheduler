 package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import org.springframework.stereotype.Component;

@Component
public class RoomCapacityConstraint {

    public boolean hasConflict(
            ScheduleChromosome chromosome,
            CapacityLookup capacityLookup) {

        if (chromosome == null
                || chromosome.getGenes() == null
                || capacityLookup == null) {
            return true;
        }

        for (ScheduleGene gene : chromosome.getGenes()) {

            if (gene == null
                    || gene.getRoomId() == null
                    || gene.getSectionId() == null) {
                return true;
            }

            Integer roomCapacity =
                    capacityLookup.getRoomCapacity(gene.getRoomId());

            Integer studentCount =
                    capacityLookup.getStudentCount(gene.getSectionId());

            // Missing or invalid data must not pass the check.
            if (roomCapacity == null
                    || roomCapacity <= 0
                    || studentCount == null
                    || studentCount < 0) {
                return true;
            }

            // A room must accommodate the entire section.
            if (studentCount > roomCapacity) {
                return true;
            }
        }

        return false;
    }

    public interface CapacityLookup {

        Integer getRoomCapacity(Long roomId);

        Integer getStudentCount(Long sectionId);
    }
}