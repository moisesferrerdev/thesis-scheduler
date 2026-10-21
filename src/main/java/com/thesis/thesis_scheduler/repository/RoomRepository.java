package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository
        extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    List<Room> findByRoomType(String roomType);

    List<Room> findByStatus(String status);

    List<Room> findByCapacityGreaterThanEqual(Integer capacity);
}