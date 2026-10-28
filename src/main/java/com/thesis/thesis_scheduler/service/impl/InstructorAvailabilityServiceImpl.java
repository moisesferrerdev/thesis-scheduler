
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.InstructorAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.InstructorAvailabilityResponse;
import com.thesis.thesis_scheduler.entity.InstructorAvailability;
import com.thesis.thesis_scheduler.mapper.InstructorAvailabilityMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.InstructorAvailabilityRepository;
import com.thesis.thesis_scheduler.repository.InstructorRepository;
import com.thesis.thesis_scheduler.service.InstructorAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorAvailabilityServiceImpl
        implements InstructorAvailabilityService {

    private final InstructorAvailabilityRepository repository;
    private final InstructorRepository instructorRepository;
    private final AcademicTermRepository academicTermRepository;
    private final InstructorAvailabilityMapper mapper;

    @Override
    public InstructorAvailabilityResponse create(
            InstructorAvailabilityRequest request) {

        validateAvailabilityType(request.getAvailabilityType());

        InstructorAvailability entity = mapper.toEntity(request);
        setRelationships(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorAvailabilityResponse getById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorAvailabilityResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public InstructorAvailabilityResponse update(
            Long id, InstructorAvailabilityRequest request) {

        validateAvailabilityType(request.getAvailabilityType());

        InstructorAvailability existing = findById(id);
        mapper.updateEntity(request, existing);
        setRelationships(existing, request);

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void setRelationships(
            InstructorAvailability entity,
            InstructorAvailabilityRequest request) {

        entity.setInstructor(
                instructorRepository.findById(request.getInstructorId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Instructor not found: "
                                        + request.getInstructorId())));

        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));
    }

    private InstructorAvailability findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Instructor availability not found: " + id));
    }

    private void validateAvailabilityType(String availabilityType) {
        if (availabilityType == null
                || (!"AVAILABLE".equalsIgnoreCase(availabilityType)
                && !"UNAVAILABLE".equalsIgnoreCase(availabilityType))) {
            throw new IllegalArgumentException(
                    "Availability type must be AVAILABLE or UNAVAILABLE.");
        }
    }
}
