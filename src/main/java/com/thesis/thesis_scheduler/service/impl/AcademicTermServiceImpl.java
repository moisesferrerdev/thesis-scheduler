
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.AcademicTermRequest;
import com.thesis.thesis_scheduler.dto.AcademicTermResponse;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import com.thesis.thesis_scheduler.mapper.AcademicTermMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.service.AcademicTermService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicTermServiceImpl implements AcademicTermService {

    private final AcademicTermRepository academicTermRepository;
    private final AcademicTermMapper academicTermMapper;

    @Override
    public AcademicTermResponse create(AcademicTermRequest request) {
        validateDateRange(request);

        AcademicTerm entity = academicTermMapper.toEntity(request);
        AcademicTerm saved = academicTermRepository.save(entity);

        return academicTermMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicTermResponse getById(Long id) {
        return academicTermMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicTermResponse> getAll() {
        return academicTermRepository.findAll()
                .stream()
                .map(academicTermMapper::toResponse)
                .toList();
    }

    @Override
    public AcademicTermResponse update(
            Long id,
            AcademicTermRequest request) {

        validateDateRange(request);

        AcademicTerm existing = findById(id);
        academicTermMapper.updateEntity(request, existing);

        AcademicTerm updated = academicTermRepository.save(existing);

        return academicTermMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        academicTermRepository.delete(findById(id));
    }

    private AcademicTerm findById(Long id) {
        return academicTermRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Academic term not found with ID: " + id));
    }

    private void validateDateRange(AcademicTermRequest request) {
        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException(
                    "Start date must not be after end date.");
        }
    }
}
