
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.ScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionResponse;
import com.thesis.thesis_scheduler.entity.ScheduleVersion;
import com.thesis.thesis_scheduler.mapper.ScheduleVersionMapper;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleRepository;
import com.thesis.thesis_scheduler.repository.ScheduleVersionRepository;
import com.thesis.thesis_scheduler.service.ScheduleVersionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleVersionServiceImpl implements ScheduleVersionService {

    private final ScheduleVersionRepository repository;
    private final GeneratedScheduleRepository generatedScheduleRepository;
    private final ScheduleVersionMapper mapper;

    @Override
    public ScheduleVersionResponse create(ScheduleVersionRequest request) {
        ScheduleVersion entity = mapper.toEntity(request);

        entity.setGeneratedSchedule(
                generatedScheduleRepository.findById(
                                request.getGeneratedScheduleId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Generated schedule not found: "
                                        + request.getGeneratedScheduleId())));

        if (request.getParentVersionId() != null) {
            entity.setParentVersion(
                    repository.findById(request.getParentVersionId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Parent schedule version not found: "
                                            + request.getParentVersionId())));
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleVersionResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleVersionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ScheduleVersionResponse update(
            Long id, ScheduleVersionRequest request) {
        findById(id);
        throw new UnsupportedOperationException(
                "Schedule versions are immutable. Create a new version instead.");
    }

    @Override
    public void delete(Long id) {
        findById(id);
        throw new UnsupportedOperationException(
                "Schedule versions cannot be deleted through this service.");
    }

    private ScheduleVersion findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Schedule version not found: " + id));
    }
}
