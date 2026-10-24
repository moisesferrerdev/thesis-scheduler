
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.ProgramRequest;
import com.thesis.thesis_scheduler.dto.ProgramResponse;
import com.thesis.thesis_scheduler.entity.Program;
import com.thesis.thesis_scheduler.mapper.ProgramMapper;
import com.thesis.thesis_scheduler.repository.ProgramRepository;
import com.thesis.thesis_scheduler.service.ProgramService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository repository;
    private final ProgramMapper mapper;

    @Override
    public ProgramResponse create(ProgramRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ProgramResponse update(Long id, ProgramRequest request) {
        Program entity = findById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private Program findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Program not found: " + id));
    }
}
