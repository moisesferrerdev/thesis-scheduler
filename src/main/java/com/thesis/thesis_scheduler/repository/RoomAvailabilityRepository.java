package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomAvailabilityRepository
        extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability>
    findByRoom_RoomIdAndTerm_TermId(
            Long roomId,
            Long termId
    );

    List<RoomAvailability>
    findByTerm_TermIdAndDayOfWeek(
            Long termId,
            Short dayOfWeek
    );
}