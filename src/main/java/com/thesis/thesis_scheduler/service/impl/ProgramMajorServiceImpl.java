
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.ProgramMajorRequest;
import com.thesis.thesis_scheduler.dto.ProgramMajorResponse;
import com.thesis.thesis_scheduler.entity.Program;
import com.thesis.thesis_scheduler.entity.ProgramMajor;
import com.thesis.thesis_scheduler.mapper.ProgramMajorMapper;
import com.thesis.thesis_scheduler.repository.ProgramMajorRepository;
import com.thesis.thesis_scheduler.repository.ProgramRepository;
import com.thesis.thesis_scheduler.service.ProgramMajorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramMajorServiceImpl implements ProgramMajorService {

    private final ProgramMajorRepository programMajorRepository;
    private final ProgramRepository programRepository;
    private final ProgramMajorMapper programMajorMapper;

    @Override
    public ProgramMajorResponse create(ProgramMajorRequest request) {

        validateDuplicateMajorCode(
                request.getProgramId(),
                request.getMajorCode()
        );

        ProgramMajor programMajor = programMajorMapper.toEntity(request);
        setProgramRelationship(programMajor, request);

        ProgramMajor saved = programMajorRepository.save(programMajor);

        return programMajorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramMajorResponse getById(Long id) {
        return programMajorMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramMajorResponse> getAll() {
        return programMajorRepository.findAll()
                .stream()
                .map(programMajorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramMajorResponse> getByProgramId(Long programId) {

        if (!programRepository.existsById(programId)) {
            throw new EntityNotFoundException(
                    "Program not found: " + programId
            );
        }

        return programMajorRepository
                .findByProgram_ProgramId(programId)
                .stream()
                .map(programMajorMapper::toResponse)
                .toList();
    }

    @Override
    public ProgramMajorResponse update(
            Long id,
            ProgramMajorRequest request
    ) {
        ProgramMajor existing = findById(id);

        validateDuplicateMajorCode(
                request.getProgramId(),
                request.getMajorCode(),
                id
        );

        programMajorMapper.updateEntity(request, existing);
        setProgramRelationship(existing, request);

        ProgramMajor updated = programMajorRepository.save(existing);

        return programMajorMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        programMajorRepository.delete(findById(id));
    }

    private void setProgramRelationship(
            ProgramMajor programMajor,
            ProgramMajorRequest request
    ) {
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Program not found: " + request.getProgramId()
                ));

        programMajor.setProgram(program);
    }

    private void validateDuplicateMajorCode(
            Long programId,
            String majorCode
    ) {
        if (programMajorRepository.existsByProgram_ProgramIdAndMajorCode(
                programId,
                majorCode
        )) {
            throw new IllegalArgumentException(
                    "Major code already exists for this Program: " + majorCode
            );
        }
    }

    private void validateDuplicateMajorCode(
            Long programId,
            String majorCode,
            Long programMajorId
    ) {
        if (programMajorRepository
                .existsByProgram_ProgramIdAndMajorCodeAndProgramMajorIdNot(
                        programId,
                        majorCode,
                        programMajorId
                )) {
            throw new IllegalArgumentException(
                    "Major code already exists for this Program: " + majorCode
            );
        }
    }

    private ProgramMajor findById(Long id) {
        return programMajorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Program Major not found: " + id
                ));
    }
}
