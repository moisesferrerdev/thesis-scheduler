
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SectionConflictConstraint {

    public boolean hasConflict(ScheduleChromosome chromosome) {
        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        Set<String> occupiedSectionSlots = new HashSet<>();

        for (ScheduleGene gene : chromosome.getGenes()) {
            if (gene == null
                    || gene.getSectionId() == null
                    || gene.getTimeslotId() == null) {
                continue;
            }

            String key = gene.getSectionId()
                    + "-" + gene.getTimeslotId();

            if (!occupiedSectionSlots.add(key)) {
                return true;
            }
        }

        return false;
    }
}
