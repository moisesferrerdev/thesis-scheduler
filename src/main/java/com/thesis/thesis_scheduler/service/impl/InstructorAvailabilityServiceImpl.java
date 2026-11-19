
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.InstructorAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.InstructorAvailabilityResponse;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import com.thesis.thesis_scheduler.entity.Instructor;
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

import java.time.Duration;
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

        validateRequest(request, null);

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

        InstructorAvailability existing = findById(id);

        validateRequest(request, id);

        mapper.updateEntity(request, existing);
        setRelationships(existing, request);

        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public void delete(Long id) {
        InstructorAvailability existing = findById(id);

        validateActiveInstructor(existing.getInstructor());

        repository.delete(existing);
    }

    private void validateRequest(
            InstructorAvailabilityRequest request,
            Long excludedAvailabilityId) {

        validateAvailabilityType(request.getAvailabilityType());
        validateDayOfWeek(request.getDayOfWeek());
        validateTimeRange(
                request.getStartTime(),
                request.getEndTime()
        );

        Instructor instructor = instructorRepository
                .findById(request.getInstructorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Instructor not found: " + request.getInstructorId()));

        validateActiveInstructor(instructor);

        academicTermRepository.findById(request.getTermId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Academic term not found: " + request.getTermId()));

        validateNoOverlap(request, excludedAvailabilityId);
    }

    private void validateAvailabilityType(String availabilityType) {
        if (availabilityType == null
                || (!"AVAILABLE".equalsIgnoreCase(availabilityType)
                && !"UNAVAILABLE".equalsIgnoreCase(availabilityType))) {

            throw new IllegalArgumentException(
                    "Availability type must be AVAILABLE or UNAVAILABLE."
            );
        }
    }

    private void validateDayOfWeek(Short dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException(
                    "Day of week must be between 1 (Monday) and 7 (Sunday)."
            );
        }
    }

    private void validateTimeRange(
            java.time.LocalTime startTime,
            java.time.LocalTime endTime) {

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Start time and end time are required."
            );
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Start time must be earlier than end time."
            );
        }

        long durationMinutes = Duration.between(
                startTime,
                endTime
        ).toMinutes();

        if (durationMinutes < 60) {
            throw new IllegalArgumentException(
                    "Availability period must be at least 60 minutes."
            );
        }
    }

    private void validateNoOverlap(
            InstructorAvailabilityRequest request,
            Long excludedAvailabilityId) {

        List<InstructorAvailability> existingPeriods =
                repository.findByInstructor_InstructorIdAndTerm_TermId(
                        request.getInstructorId(),
                        request.getTermId()
                );

        boolean hasOverlap = existingPeriods.stream()
                .filter(period ->
                        period.getDayOfWeek().equals(request.getDayOfWeek()))
                .filter(period ->
                        excludedAvailabilityId == null
                                || !period.getAvailabilityId()
                                .equals(excludedAvailabilityId))
                .anyMatch(period ->
                        request.getStartTime().isBefore(period.getEndTime())
                                && request.getEndTime()
                                .isAfter(period.getStartTime()));

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "Availability period overlaps with an existing period."
            );
        }
    }

    private void validateActiveInstructor(Instructor instructor) {
        if (instructor.getStatus() == null
                || !"ACTIVE".equalsIgnoreCase(instructor.getStatus())) {

            throw new IllegalArgumentException(
                    "Availability cannot be modified for an inactive instructor."
            );
        }
    }

    private void setRelationships(
            InstructorAvailability entity,
            InstructorAvailabilityRequest request) {

        entity.setInstructor(
                instructorRepository.findById(request.getInstructorId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Instructor not found: "
                                        + request.getInstructorId()))
        );

        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId()))
        );
    }

    private InstructorAvailability findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Instructor availability not found: " + id));
    }
}
