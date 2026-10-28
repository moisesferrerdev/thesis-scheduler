
package com.thesis.thesis_scheduler.optimization.constraint;

import com.thesis.thesis_scheduler.entity.RoomAvailability;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.repository.RoomAvailabilityRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoomAvailabilityConstraint {

    private static final String AVAILABLE = "AVAILABLE";
    private static final String UNAVAILABLE = "UNAVAILABLE";

    private final RoomAvailabilityRepository availabilityRepository;
    private final TimeslotRepository timeslotRepository;

    public boolean hasConflict(
            ScheduleChromosome chromosome,
            Long termId) {

        if (chromosome == null || chromosome.getGenes() == null) {
            return false;
        }

        if (termId == null) {
            return true;
        }

        for (ScheduleGene gene : chromosome.getGenes()) {
            if (gene == null
                    || gene.getRoomId() == null
                    || gene.getTimeslotId() == null) {
                continue;
            }

            Timeslot timeslot = timeslotRepository
                    .findById(gene.getTimeslotId())
                    .orElse(null);

            if (timeslot == null
                    || timeslot.getTerm() == null
                    || !termId.equals(timeslot.getTerm().getTermId())) {
                return true;
            }

            List<RoomAvailability> records =
                    availabilityRepository
                            .findByRoom_RoomIdAndTerm_TermId(
                                    gene.getRoomId(), termId);

            List<RoomAvailability> sameDayRecords =
                    records.stream()
                            .filter(record ->
                                    record.getDayOfWeek() != null
                                            && record.getDayOfWeek()
                                            .equals(timeslot.getDayOfWeek()))
                            .toList();

            boolean hasAvailableWindow = sameDayRecords.stream()
                    .filter(record -> AVAILABLE.equalsIgnoreCase(
                            record.getAvailabilityType()))
                    .anyMatch(record ->
                            !timeslot.getStartTime()
                                    .isBefore(record.getStartTime())
                                    && !timeslot.getEndTime()
                                    .isAfter(record.getEndTime()));

            if (!hasAvailableWindow) {
                return true;
            }

            boolean overlapsUnavailableWindow = sameDayRecords.stream()
                    .filter(record -> UNAVAILABLE.equalsIgnoreCase(
                            record.getAvailabilityType()))
                    .anyMatch(record ->
                            timeslot.getStartTime()
                                    .isBefore(record.getEndTime())
                                    && timeslot.getEndTime()
                                    .isAfter(record.getStartTime()));

            if (overlapsUnavailableWindow) {
                return true;
            }
        }

        return false;
    }
}
