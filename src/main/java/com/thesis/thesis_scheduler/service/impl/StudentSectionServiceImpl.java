
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.StudentSectionRequest;
import com.thesis.thesis_scheduler.dto.StudentSectionResponse;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import com.thesis.thesis_scheduler.entity.Program;
import com.thesis.thesis_scheduler.entity.ProgramMajor;
import com.thesis.thesis_scheduler.entity.StudentSection;
import com.thesis.thesis_scheduler.mapper.StudentSectionMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.ProgramMajorRepository;
import com.thesis.thesis_scheduler.repository.ProgramRepository;
import com.thesis.thesis_scheduler.repository.StudentSectionRepository;
import com.thesis.thesis_scheduler.service.StudentSectionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentSectionServiceImpl implements StudentSectionService {

    private final StudentSectionRepository repository;
    private final AcademicTermRepository termRepository;
    private final ProgramRepository programRepository;
    private final StudentSectionMapper mapper;
    private final ProgramMajorRepository programMajorRepository;

    @Override
    public StudentSectionResponse create(StudentSectionRequest request) {
        StudentSection entity = mapper.toEntity(request);
        setRelationships(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSectionResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSectionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StudentSectionResponse update(
            Long id, StudentSectionRequest request) {
        StudentSection entity = findById(id);
        mapper.updateEntity(request, entity);
        setRelationships(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            StudentSection studentSection,
            StudentSectionRequest request
    ) {
        // Resolve Academic Term
        AcademicTerm academicTerm = termRepository
                .findById(request.getTermId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Academic Term not found: " + request.getTermId()
                ));

        // Resolve Program
        Program program = programRepository
                .findById(request.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Program not found: " + request.getProgramId()
                ));

        // Set mandatory relationships
        studentSection.setAcademicTerm(academicTerm);
        studentSection.setProgram(program);

        // Resolve optional Program Major
        if (request.getProgramMajorId() != null) {

            ProgramMajor programMajor = programMajorRepository
                    .findById(request.getProgramMajorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Program Major not found: "
                                    + request.getProgramMajorId()
                    ));

            // Validate that the Program Major belongs to the selected Program
            if (!programMajor.getProgram().getProgramId()
                    .equals(program.getProgramId())) {

                throw new IllegalArgumentException(
                        "The selected Program Major does not belong to the selected Program."
                );
            }

            studentSection.setProgramMajor(programMajor);

        } else {
            studentSection.setProgramMajor(null);
        }
    }

    private StudentSection findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student section not found: " + id));
    }
}
