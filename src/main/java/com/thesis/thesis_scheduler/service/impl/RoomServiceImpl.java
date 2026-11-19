package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.RoomRequest;
import com.thesis.thesis_scheduler.dto.RoomResponse;
import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.RoomClassification;
import com.thesis.thesis_scheduler.mapper.RoomMapper;
import com.thesis.thesis_scheduler.repository.RoomClassificationRepository;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomClassificationRepository classificationRepository;
    private final RoomMapper roomMapper;

    @Override
    public RoomResponse create(RoomRequest request) {

        Room room = roomMapper.toEntity(request);

        setClassification(room, request);

        return roomMapper.toResponse(
                roomRepository.save(room)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getById(Long id) {

        return roomMapper.toResponse(
                findById(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAll() {

        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {

        Room existing = findById(id);

        roomMapper.updateEntity(request, existing);

        setClassification(existing, request);

        return roomMapper.toResponse(
                roomRepository.save(existing)
        );
    }

    @Override
    public void delete(Long id) {

        roomRepository.delete(findById(id));
    }

    private void setClassification(
            Room room,
            RoomRequest request) {

        String roomType = request.getRoomType() == null
                ? ""
                : request.getRoomType().trim().toUpperCase();

        if (!List.of("LECTURE", "LABORATORY").contains(roomType)) {
            throw new IllegalArgumentException(
                    "Room type must be LECTURE or LABORATORY."
            );
        }

        if (request.getClassificationId() == null) {
            throw new IllegalArgumentException(
                    "Room classification is required."
            );
        }

        RoomClassification classification =
                classificationRepository.findById(
                        request.getClassificationId()
                ).orElseThrow(() ->
                        new EntityNotFoundException(
                                "Room classification not found: "
                                        + request.getClassificationId()
                        )
                );

        if (!"ACTIVE".equalsIgnoreCase(classification.getStatus())) {
            throw new IllegalArgumentException(
                    "Inactive room classifications cannot be assigned to rooms."
            );
        }

        if (!roomType.equalsIgnoreCase(classification.getRoomType())) {
            throw new IllegalArgumentException(
                    "Room classification must match the selected room type."
            );
        }

        room.setRoomType(roomType);
        room.setClassification(classification);
    }

    private Room findById(Long id) {

        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Room not found: " + id
                        )
                );
    }
}