
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.OptimizationRunRequest;
import com.thesis.thesis_scheduler.dto.OptimizationRunResponse;
import com.thesis.thesis_scheduler.entity.OptimizationRun;
import com.thesis.thesis_scheduler.mapper.OptimizationRunMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.OptimizationRunRepository;
import com.thesis.thesis_scheduler.service.OptimizationRunService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OptimizationRunServiceImpl implements OptimizationRunService {

    private final OptimizationRunRepository repository;
    private final AcademicTermRepository academicTermRepository;
    private final OptimizationRunMapper mapper;

    @Override
    public OptimizationRunResponse create(OptimizationRunRequest request) {
        OptimizationRun entity = mapper.toEntity(request);

        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public OptimizationRunResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationRunResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public OptimizationRunResponse update(
            Long id, OptimizationRunRequest request) {
        OptimizationRun existing = findById(id);
        mapper.updateEntity(request, existing);

        existing.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private OptimizationRun findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Optimization run not found: " + id));
    }
}
