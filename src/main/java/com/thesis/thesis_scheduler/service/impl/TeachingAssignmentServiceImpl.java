
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.TeachingAssignmentRequest;
import com.thesis.thesis_scheduler.dto.TeachingAssignmentResponse;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.mapper.TeachingAssignmentMapper;
import com.thesis.thesis_scheduler.repository.InstructorRepository;
import com.thesis.thesis_scheduler.repository.SectionSubjectRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import com.thesis.thesis_scheduler.service.TeachingAssignmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeachingAssignmentServiceImpl
        implements TeachingAssignmentService {

    private final TeachingAssignmentRepository repository;
    private final InstructorRepository instructorRepository;
    private final SectionSubjectRepository sectionSubjectRepository;
    private final TeachingAssignmentMapper mapper;

    @Override
    public TeachingAssignmentResponse create(
            TeachingAssignmentRequest request) {

        TeachingAssignment entity = mapper.toEntity(request);
        setRelationships(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public TeachingAssignmentResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeachingAssignmentResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public TeachingAssignmentResponse update(
            Long id, TeachingAssignmentRequest request) {

        TeachingAssignment existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            TeachingAssignment entity,
            TeachingAssignmentRequest request) {

        entity.setSectionSubject(
                sectionSubjectRepository.findById(
                                request.getSectionSubjectId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Section subject not found: "
                                        + request.getSectionSubjectId())));

        entity.setInstructor(
                instructorRepository.findById(request.getInstructorId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Instructor not found: "
                                        + request.getInstructorId())));
    }

    private TeachingAssignment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Teaching assignment not found: " + id));
    }
}
