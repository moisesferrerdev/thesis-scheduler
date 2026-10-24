
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.SubjectOfferingRequest;
import com.thesis.thesis_scheduler.dto.SubjectOfferingResponse;
import com.thesis.thesis_scheduler.entity.SubjectOffering;
import com.thesis.thesis_scheduler.mapper.SubjectOfferingMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.CurriculumRepository;
import com.thesis.thesis_scheduler.repository.SubjectOfferingRepository;
import com.thesis.thesis_scheduler.repository.SubjectRepository;
import com.thesis.thesis_scheduler.service.SubjectOfferingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectOfferingServiceImpl implements SubjectOfferingService {

    private final SubjectOfferingRepository subjectOfferingRepository;
    private final AcademicTermRepository academicTermRepository;
    private final CurriculumRepository curriculumRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectOfferingMapper subjectOfferingMapper;

    @Override
    public SubjectOfferingResponse create(SubjectOfferingRequest request) {
        SubjectOffering entity = subjectOfferingMapper.toEntity(request);
        setRelationships(entity, request);

        SubjectOffering saved = subjectOfferingRepository.save(entity);
        return subjectOfferingMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectOfferingResponse getById(Long id) {
        return subjectOfferingMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectOfferingResponse> getAll() {
        return subjectOfferingRepository.findAll()
                .stream()
                .map(subjectOfferingMapper::toResponse)
                .toList();
    }

    @Override
    public SubjectOfferingResponse update(
            Long id, SubjectOfferingRequest request) {

        SubjectOffering existing = findById(id);
        subjectOfferingMapper.updateEntity(request, existing);
        setRelationships(existing, request);

        SubjectOffering updated = subjectOfferingRepository.save(existing);
        return subjectOfferingMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        subjectOfferingRepository.delete(findById(id));
    }

    private void setRelationships(
            SubjectOffering entity,
            SubjectOfferingRequest request) {

        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));

        entity.setCurriculum(
                curriculumRepository.findById(request.getCurriculumId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Curriculum not found: "
                                        + request.getCurriculumId())));

        entity.setSubject(
                subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Subject not found: "
                                        + request.getSubjectId())));
    }

    private SubjectOffering findById(Long id) {
        return subjectOfferingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject offering not found: " + id));
    }
}
