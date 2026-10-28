
package com.thesis.thesis_scheduler.optimization.model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleChromosome {

    private List<ScheduleGene> genes = new ArrayList<>();

    public ScheduleChromosome() {
    }

    public ScheduleChromosome(List<ScheduleGene> genes) {
        this.genes = new ArrayList<>(genes);
    }

    public List<ScheduleGene> getGenes() {
        return genes;
    }

    public void setGenes(List<ScheduleGene> genes) {
        this.genes = new ArrayList<>(genes);
    }
}
