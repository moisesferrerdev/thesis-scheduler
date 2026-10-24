
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.InstructorQualificationRequest;
import com.thesis.thesis_scheduler.dto.InstructorQualificationResponse;
import com.thesis.thesis_scheduler.entity.InstructorQualification;
import com.thesis.thesis_scheduler.mapper.InstructorQualificationMapper;
import com.thesis.thesis_scheduler.repository.InstructorQualificationRepository;
import com.thesis.thesis_scheduler.repository.InstructorRepository;
import com.thesis.thesis_scheduler.repository.SubjectRepository;
import com.thesis.thesis_scheduler.service.InstructorQualificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorQualificationServiceImpl
        implements InstructorQualificationService {

    private final InstructorQualificationRepository repository;
    private final InstructorRepository instructorRepository;
    private final SubjectRepository subjectRepository;
    private final InstructorQualificationMapper mapper;

    @Override
    public InstructorQualificationResponse create(
            InstructorQualificationRequest request) {

        InstructorQualification entity = mapper.toEntity(request);
        setRelationships(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorQualificationResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorQualificationResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public InstructorQualificationResponse update(
            Long id, InstructorQualificationRequest request) {

        InstructorQualification existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            InstructorQualification entity,
            InstructorQualificationRequest request) {

        entity.setInstructor(
                instructorRepository.findById(request.getInstructorId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Instructor not found: "
                                        + request.getInstructorId())));

        entity.setSubject(
                subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Subject not found: "
                                        + request.getSubjectId())));
    }

    private InstructorQualification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Instructor qualification not found: " + id));
    }
}
