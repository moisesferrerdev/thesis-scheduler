
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleRequest;
import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;
import com.thesis.thesis_scheduler.entity.GeneratedSchedule;
import com.thesis.thesis_scheduler.mapper.GeneratedScheduleMapper;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleRepository;
import com.thesis.thesis_scheduler.repository.OptimizationRunRepository;
import com.thesis.thesis_scheduler.service.GeneratedScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GeneratedScheduleServiceImpl
        implements GeneratedScheduleService {

    private final GeneratedScheduleRepository repository;
    private final OptimizationRunRepository optimizationRunRepository;
    private final GeneratedScheduleMapper mapper;

    @Override
    public GeneratedScheduleResponse create(GeneratedScheduleRequest request) {
        GeneratedSchedule entity = mapper.toEntity(request);
        setRelationships(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public GeneratedScheduleResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratedScheduleResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public GeneratedScheduleResponse update(
            Long id, GeneratedScheduleRequest request) {
        GeneratedSchedule existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            GeneratedSchedule entity, GeneratedScheduleRequest request) {
        entity.setOptimizationRun(
                optimizationRunRepository.findById(
                                request.getOptimizationRunId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Optimization run not found: "
                                        + request.getOptimizationRunId())));
    }

    private GeneratedSchedule findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Generated schedule not found: " + id));
    }
}
