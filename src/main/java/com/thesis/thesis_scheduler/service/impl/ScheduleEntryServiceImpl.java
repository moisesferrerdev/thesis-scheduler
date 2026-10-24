
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.ScheduleEntryRequest;
import com.thesis.thesis_scheduler.dto.ScheduleEntryResponse;
import com.thesis.thesis_scheduler.entity.ScheduleEntry;
import com.thesis.thesis_scheduler.mapper.ScheduleEntryMapper;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.ScheduleEntryRepository;
import com.thesis.thesis_scheduler.repository.ScheduleVersionRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import com.thesis.thesis_scheduler.service.ScheduleEntryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleEntryServiceImpl implements ScheduleEntryService {

    private final ScheduleEntryRepository repository;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;
    private final ScheduleEntryMapper mapper;

    @Override
    public ScheduleEntryResponse create(ScheduleEntryRequest request) {
        ScheduleEntry entity = mapper.toEntity(request);
        setRelationships(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleEntryResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEntryResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ScheduleEntryResponse update(
            Long id, ScheduleEntryRequest request) {
        ScheduleEntry existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            ScheduleEntry entity, ScheduleEntryRequest request) {

        entity.setScheduleVersion(
                scheduleVersionRepository.findById(
                                request.getScheduleVersionId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Schedule version not found: "
                                        + request.getScheduleVersionId())));

        entity.setAssignment(
                teachingAssignmentRepository.findById(
                                request.getAssignmentId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Teaching assignment not found: "
                                        + request.getAssignmentId())));

        entity.setRoom(
                roomRepository.findById(request.getRoomId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Room not found: " + request.getRoomId())));

        entity.setTimeslot(
                timeslotRepository.findById(request.getTimeslotId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Timeslot not found: "
                                        + request.getTimeslotId())));
    }

    private ScheduleEntry findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Schedule entry not found: " + id));
    }
}
