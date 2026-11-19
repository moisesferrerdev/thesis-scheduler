package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository
        extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    boolean existsByClassification_ClassificationId(Long classificationId);

    List<Room> findByRoomType(String roomType);

    @Query("""
    SELECT r
    FROM Room r
    LEFT JOIN FETCH r.classification
    WHERE r.status = :status
""")
    List<Room> findByStatus(@Param("status") String status);

    List<Room> findByCapacityGreaterThanEqual(Integer capacity);

    @Query("""
    SELECT r
    FROM Room r
    LEFT JOIN FETCH r.classification
    WHERE r.roomId = :roomId
""")
    Optional<Room> findWithClassificationById(
            @Param("roomId") Long roomId
    );

}