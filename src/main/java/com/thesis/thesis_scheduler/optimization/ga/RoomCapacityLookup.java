 package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.StudentSection;
import com.thesis.thesis_scheduler.optimization.constraint.RoomCapacityConstraint;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.StudentSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomCapacityLookup
        implements RoomCapacityConstraint.CapacityLookup {

    private final RoomRepository roomRepository;
    private final StudentSectionRepository studentSectionRepository;

    @Override
    public Integer getRoomCapacity(Long roomId) {
        if (roomId == null) {
            return null;
        }

        return roomRepository.findById(roomId)
                .map(Room::getCapacity)
                .orElse(null);
    }

    @Override
    public Integer getStudentCount(Long sectionId) {
        if (sectionId == null) {
            return null;
        }

        return studentSectionRepository.findById(sectionId)
                .map(StudentSection::getStudentCount)
                .orElse(null);
    }
}
