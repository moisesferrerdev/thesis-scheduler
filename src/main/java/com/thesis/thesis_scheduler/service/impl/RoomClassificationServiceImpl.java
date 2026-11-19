package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.RoomClassificationRequest;
import com.thesis.thesis_scheduler.dto.RoomClassificationResponse;
import com.thesis.thesis_scheduler.entity.RoomClassification;
import com.thesis.thesis_scheduler.repository.RoomClassificationRepository;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.service.RoomClassificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomClassificationServiceImpl
        implements RoomClassificationService {

    private final RoomClassificationRepository repository;
    private final RoomRepository roomRepository;

    @Override
    public RoomClassificationResponse create(
            RoomClassificationRequest request) {

        String roomType = normalizeRoomType(request.getRoomType());
        String classificationName = request.getClassificationName().trim();

        validateRoomType(roomType);

        validateStatus(request.getStatus());

        if (repository
                .findByRoomTypeIgnoreCaseAndClassificationNameIgnoreCase(
                        roomType,
                        classificationName
                ).isPresent()) {

            throw new IllegalArgumentException(
                    "A classification with this name already exists for the selected room type."
            );
        }

        RoomClassification classification = new RoomClassification();

        applyRequest(classification, request, roomType);

        return toResponse(repository.save(classification));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomClassificationResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomClassificationResponse> getAll(boolean activeOnly) {

        List<RoomClassification> classifications = activeOnly
                ? repository.findByStatusOrderByRoomTypeAscClassificationNameAsc(
                "ACTIVE"
        )
                : repository.findAllByOrderByRoomTypeAscClassificationNameAsc();

        return classifications.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RoomClassificationResponse update(
            Long id,
            RoomClassificationRequest request) {

        RoomClassification existing = findById(id);

        String roomType = normalizeRoomType(request.getRoomType());
        String classificationName = request.getClassificationName().trim();

        validateRoomType(roomType);
        validateStatus(request.getStatus());

        boolean duplicate = repository
                .existsByRoomTypeIgnoreCaseAndClassificationNameIgnoreCaseAndClassificationIdNot(
                        roomType,
                        classificationName,
                        id
                );

        if (duplicate) {
            throw new IllegalArgumentException(
                    "A classification with this name already exists for the selected room type."
            );
        }

        applyRequest(existing, request, roomType);

        return toResponse(repository.save(existing));
    }

    @Override
    public void deactivate(Long id) {

        RoomClassification classification = findById(id);

        boolean assignedToRoom =
                roomRepository.existsByClassification_ClassificationId(id);

        if (assignedToRoom) {
            throw new IllegalArgumentException(
                    "Cannot deactivate a room classification that is currently assigned to a room."
            );
        }

        classification.setStatus("INACTIVE");

        repository.save(classification);
    }

    private void applyRequest(
            RoomClassification entity,
            RoomClassificationRequest request,
            String roomType) {

        entity.setRoomType(roomType);
        entity.setClassificationName(
                request.getClassificationName().trim()
        );
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus().trim().toUpperCase());
    }

    private String normalizeRoomType(String roomType) {
        return roomType == null
                ? ""
                : roomType.trim().toUpperCase();
    }

    private void validateRoomType(String roomType) {

        if (!List.of("LECTURE", "LABORATORY").contains(roomType)) {
            throw new IllegalArgumentException(
                    "Room type must be LECTURE or LABORATORY."
            );
        }
    }

    private void validateStatus(String status) {

        if (status == null ||
                !List.of("ACTIVE", "INACTIVE")
                        .contains(status.trim().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Status must be ACTIVE or INACTIVE."
            );
        }
    }

    private RoomClassification findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Room classification not found: " + id
                        )
                );
    }

    private RoomClassificationResponse toResponse(
            RoomClassification entity) {

        return new RoomClassificationResponse(
                entity.getClassificationId(),
                entity.getRoomType(),
                entity.getClassificationName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}