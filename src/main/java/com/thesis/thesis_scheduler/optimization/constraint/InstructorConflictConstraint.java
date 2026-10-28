
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import java.util.HashSet;
import java.util.Set;

public class InstructorConflictConstraint {

    public boolean hasConflict(ScheduleChromosome chromosome) {
        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        Set<String> occupiedInstructorSlots = new HashSet<>();

        for (ScheduleGene gene : chromosome.getGenes()) {
            if (gene == null
                    || gene.getInstructorId() == null
                    || gene.getTimeslotId() == null) {
                continue;
            }

            String key = gene.getInstructorId()
                    + "-" + gene.getTimeslotId();

            if (!occupiedInstructorSlots.add(key)) {
                return true;
            }
        }

        return false;
    }
}
