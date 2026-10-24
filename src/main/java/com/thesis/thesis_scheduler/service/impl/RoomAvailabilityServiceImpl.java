
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.RoomAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.RoomAvailabilityResponse;
import com.thesis.thesis_scheduler.entity.RoomAvailability;
import com.thesis.thesis_scheduler.mapper.RoomAvailabilityMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.RoomAvailabilityRepository;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.service.RoomAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomAvailabilityServiceImpl
        implements RoomAvailabilityService {

    private final RoomAvailabilityRepository repository;
    private final RoomRepository roomRepository;
    private final AcademicTermRepository academicTermRepository;
    private final RoomAvailabilityMapper mapper;

    @Override
    public RoomAvailabilityResponse create(RoomAvailabilityRequest request) {
        RoomAvailability entity = mapper.toEntity(request);
        setRelationships(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomAvailabilityResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAvailabilityResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public RoomAvailabilityResponse update(
            Long id, RoomAvailabilityRequest request) {
        RoomAvailability existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            RoomAvailability entity,
            RoomAvailabilityRequest request) {

        entity.setRoom(
                roomRepository.findById(request.getRoomId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Room not found: " + request.getRoomId())));

        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));
    }

    private RoomAvailability findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Room availability not found: " + id));
    }
}
