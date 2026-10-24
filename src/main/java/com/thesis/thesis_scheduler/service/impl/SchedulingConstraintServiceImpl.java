
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.SchedulingConstraintRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConstraintResponse;
import com.thesis.thesis_scheduler.entity.SchedulingConstraint;
import com.thesis.thesis_scheduler.mapper.SchedulingConstraintMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.SchedulingConstraintRepository;
import com.thesis.thesis_scheduler.service.SchedulingConstraintService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulingConstraintServiceImpl
        implements SchedulingConstraintService {

    private final SchedulingConstraintRepository repository;
    private final AcademicTermRepository academicTermRepository;
    private final SchedulingConstraintMapper mapper;

    @Override
    public SchedulingConstraintResponse create(
            SchedulingConstraintRequest request) {
        SchedulingConstraint entity = mapper.toEntity(request);
        setRelationships(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SchedulingConstraintResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchedulingConstraintResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public SchedulingConstraintResponse update(
            Long id, SchedulingConstraintRequest request) {
        SchedulingConstraint existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            SchedulingConstraint entity,
            SchedulingConstraintRequest request) {
        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));
    }

    private SchedulingConstraint findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Scheduling constraint not found: " + id));
    }
}
