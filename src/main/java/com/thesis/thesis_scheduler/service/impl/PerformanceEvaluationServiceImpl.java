
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.PerformanceEvaluationRequest;
import com.thesis.thesis_scheduler.dto.PerformanceEvaluationResponse;
import com.thesis.thesis_scheduler.entity.PerformanceEvaluation;
import com.thesis.thesis_scheduler.mapper.PerformanceEvaluationMapper;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleRepository;
import com.thesis.thesis_scheduler.repository.PerformanceEvaluationRepository;
import com.thesis.thesis_scheduler.repository.ScheduleVersionRepository;
import com.thesis.thesis_scheduler.service.PerformanceEvaluationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceEvaluationServiceImpl
        implements PerformanceEvaluationService {

    private final PerformanceEvaluationRepository repository;
    private final GeneratedScheduleRepository generatedScheduleRepository;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final PerformanceEvaluationMapper mapper;

    @Override
    public PerformanceEvaluationResponse create(
            PerformanceEvaluationRequest request) {

        validateReference(request);

        PerformanceEvaluation entity = mapper.toEntity(request);
        setRelationships(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceEvaluationResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceEvaluationResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PerformanceEvaluationResponse update(
            Long id, PerformanceEvaluationRequest request) {

        validateReference(request);

        PerformanceEvaluation existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            PerformanceEvaluation entity,
            PerformanceEvaluationRequest request) {

        if (request.getGeneratedScheduleId() != null) {
            entity.setGeneratedSchedule(
                    generatedScheduleRepository.findById(
                                    request.getGeneratedScheduleId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Generated schedule not found: "
                                            + request.getGeneratedScheduleId())));
        } else {
            entity.setGeneratedSchedule(null);
        }

        if (request.getScheduleVersionId() != null) {
            entity.setScheduleVersion(
                    scheduleVersionRepository.findById(
                                    request.getScheduleVersionId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Schedule version not found: "
                                            + request.getScheduleVersionId())));
        } else {
            entity.setScheduleVersion(null);
        }
    }

    private void validateReference(PerformanceEvaluationRequest request) {
        if (request.getGeneratedScheduleId() == null
                && request.getScheduleVersionId() == null) {
            throw new IllegalArgumentException(
                    "Either generatedScheduleId or scheduleVersionId is required.");
        }
    }

    private PerformanceEvaluation findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Performance evaluation not found: " + id));
    }
}
