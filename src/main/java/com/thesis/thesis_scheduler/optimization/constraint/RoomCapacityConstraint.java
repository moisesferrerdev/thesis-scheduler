
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

public class RoomCapacityConstraint {

    public boolean hasConflict(
            ScheduleChromosome chromosome,
            CapacityLookup capacityLookup) {

        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        for (ScheduleGene gene : chromosome.getGenes()) {
            if (gene == null
                    || gene.getRoomId() == null
                    || gene.getSectionId() == null) {
                continue;
            }

            Integer roomCapacity =
                    capacityLookup.getRoomCapacity(gene.getRoomId());

            Integer studentCount =
                    capacityLookup.getStudentCount(gene.getSectionId());

            if (roomCapacity != null
                    && studentCount != null
                    && studentCount > roomCapacity) {
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
