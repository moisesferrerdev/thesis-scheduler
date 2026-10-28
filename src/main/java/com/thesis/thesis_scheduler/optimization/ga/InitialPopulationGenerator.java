
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialPopulationGenerator {

    private final TeachingAssignmentRepository
            teachingAssignmentRepository;

    private final RoomRepository roomRepository;

    private final TimeslotRepository timeslotRepository;
}
