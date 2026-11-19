package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.RoomClassification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomClassificationRepository
        extends JpaRepository<RoomClassification, Long> {

    List<RoomClassification>
    findByStatusOrderByRoomTypeAscClassificationNameAsc(String status);

    List<RoomClassification>
    findAllByOrderByRoomTypeAscClassificationNameAsc();

    Optional<RoomClassification>
    findByRoomTypeIgnoreCaseAndClassificationNameIgnoreCase(
            String roomType,
            String classificationName
    );

    boolean existsByRoomTypeIgnoreCaseAndClassificationNameIgnoreCaseAndClassificationIdNot(
            String roomType,
            String classificationName,
            Long classificationId
    );
}