package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.InstructorRequest;
import com.thesis.thesis_scheduler.dto.InstructorResponse;
import com.thesis.thesis_scheduler.entity.Instructor;
import com.thesis.thesis_scheduler.mapper.InstructorMapper;
import com.thesis.thesis_scheduler.repository.InstructorRepository;
import com.thesis.thesis_scheduler.service.InstructorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;

    @Override
    public InstructorResponse create(InstructorRequest request) {

        validateLoadHours(request);

        Instructor instructor = instructorMapper.toEntity(request);

        Long nextNumber = instructorRepository.getNextEmployeeNumber();

        instructor.setEmployeeNo(
                String.format(Locale.ROOT, "EMP-%04d", nextNumber)
        );

        return instructorMapper.toResponse(
                instructorRepository.save(instructor)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorResponse getById(Long id) {
        return instructorMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstructorResponse> getAll() {
        return instructorRepository.findAll()
                .stream()
                .map(instructorMapper::toResponse)
                .toList();
    }

    @Override
    public InstructorResponse update(Long id, InstructorRequest request) {

        validateLoadHours(request);

        Instructor existing = findById(id);

        instructorMapper.updateEntity(request, existing);

        return instructorMapper.toResponse(
                instructorRepository.save(existing)
        );
    }

    @Override
    public void delete(Long id) {
        instructorRepository.delete(findById(id));
    }

    private Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Instructor not found: " + id));
    }

    private void validateLoadHours(InstructorRequest request) {

        if (request.getTargetLoadHours() != null
                && request.getTargetLoadHours()
                .compareTo(request.getMaxLoadHours()) > 0) {

            throw new IllegalArgumentException(
                    "Target Load Hours cannot exceed Maximum Load Hours."
            );
        }
    }
}
