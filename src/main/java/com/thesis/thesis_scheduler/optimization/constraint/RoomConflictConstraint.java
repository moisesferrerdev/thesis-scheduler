
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import java.util.List;

public class RoomConflictConstraint {

    public boolean hasConflict(ScheduleChromosome chromosome) {
        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        List<ScheduleGene> genes = chromosome.getGenes();

        for (int i = 0; i < genes.size(); i++) {
            ScheduleGene first = genes.get(i);

            if (first == null
                    || first.getRoomId() == null
                    || first.getTimeslotId() == null) {
                continue;
            }

            for (int j = i + 1; j < genes.size(); j++) {
                ScheduleGene second = genes.get(j);

                if (second == null
                        || second.getRoomId() == null
                        || second.getTimeslotId() == null) {
                    continue;
                }

                boolean sameRoom = first.getRoomId()
                        .equals(second.getRoomId());

                boolean sameTimeslot = first.getTimeslotId()
                        .equals(second.getTimeslotId());

                if (sameRoom && sameTimeslot) {
                    return true;
                }
            }
        }

        return false;
    }
}
