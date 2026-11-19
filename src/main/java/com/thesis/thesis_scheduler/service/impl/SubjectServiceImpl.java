package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.SubjectRequest;
import com.thesis.thesis_scheduler.dto.SubjectResponse;
import com.thesis.thesis_scheduler.entity.RoomClassification;
import com.thesis.thesis_scheduler.entity.Subject;
import com.thesis.thesis_scheduler.mapper.SubjectMapper;
import com.thesis.thesis_scheduler.repository.RoomClassificationRepository;
import com.thesis.thesis_scheduler.repository.SubjectRepository;
import com.thesis.thesis_scheduler.service.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository repository;
    private final RoomClassificationRepository classificationRepository;
    private final SubjectMapper mapper;

    @Override
    public SubjectResponse create(SubjectRequest request) {

        Subject entity = mapper.toEntity(request);

        setRequiredLaboratoryClassification(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getById(Long id) {

        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public SubjectResponse update(Long id, SubjectRequest request) {

        Subject entity = findById(id);

        mapper.updateEntity(request, entity);

        setRequiredLaboratoryClassification(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {

        repository.delete(findById(id));
    }

    private void setRequiredLaboratoryClassification(
            Subject subject,
            SubjectRequest request) {

        BigDecimal laboratoryHours = request.getLaboratoryHours();

        if (laboratoryHours == null) {
            throw new IllegalArgumentException(
                    "Laboratory hours are required."
            );
        }

        boolean requiresLaboratory =
                laboratoryHours.compareTo(BigDecimal.ZERO) > 0;

        Long classificationId =
                request.getRequiredLaboratoryClassificationId();

        if (!requiresLaboratory) {

            if (classificationId != null) {
                throw new IllegalArgumentException(
                        "A laboratory classification must not be assigned when laboratory hours are zero."
                );
            }

            subject.setRequiredLaboratoryClassification(null);
            return;
        }

        if (classificationId == null) {
            throw new IllegalArgumentException(
                    "A laboratory classification is required when laboratory hours are greater than zero."
            );
        }

        RoomClassification classification =
                classificationRepository.findById(classificationId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Room classification not found: "
                                                + classificationId
                                )
                        );

        if (!"ACTIVE".equalsIgnoreCase(classification.getStatus())) {
            throw new IllegalArgumentException(
                    "Inactive room classifications cannot be assigned to subjects."
            );
        }

        if (!"LABORATORY".equalsIgnoreCase(classification.getRoomType())) {
            throw new IllegalArgumentException(
                    "Subject laboratory requirement must use a LABORATORY classification."
            );
        }

        subject.setRequiredLaboratoryClassification(classification);
    }

    private Subject findById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Subject not found: " + id
                        )
                );
    }
}