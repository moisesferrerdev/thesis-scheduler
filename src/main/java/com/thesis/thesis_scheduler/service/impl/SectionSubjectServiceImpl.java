
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.SectionSubjectRequest;
import com.thesis.thesis_scheduler.dto.SectionSubjectResponse;
import com.thesis.thesis_scheduler.entity.SectionSubject;
import com.thesis.thesis_scheduler.mapper.SectionSubjectMapper;
import com.thesis.thesis_scheduler.repository.SectionSubjectRepository;
import com.thesis.thesis_scheduler.repository.StudentSectionRepository;
import com.thesis.thesis_scheduler.repository.SubjectOfferingRepository;
import com.thesis.thesis_scheduler.service.SectionSubjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionSubjectServiceImpl implements SectionSubjectService {

    private final SectionSubjectRepository sectionSubjectRepository;
    private final StudentSectionRepository studentSectionRepository;
    private final SubjectOfferingRepository subjectOfferingRepository;
    private final SectionSubjectMapper sectionSubjectMapper;

    @Override
    public SectionSubjectResponse create(SectionSubjectRequest request) {
        SectionSubject entity = sectionSubjectMapper.toEntity(request);
        setRelationships(entity, request);

        SectionSubject saved = sectionSubjectRepository.save(entity);
        return sectionSubjectMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SectionSubjectResponse getById(Long id) {
        return sectionSubjectMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionSubjectResponse> getAll() {
        return sectionSubjectRepository.findAll()
                .stream()
                .map(sectionSubjectMapper::toResponse)
                .toList();
    }

    @Override
    public SectionSubjectResponse update(
            Long id, SectionSubjectRequest request) {

        SectionSubject existing = findById(id);
        sectionSubjectMapper.updateEntity(request, existing);
        setRelationships(existing, request);

        SectionSubject updated = sectionSubjectRepository.save(existing);
        return sectionSubjectMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        sectionSubjectRepository.delete(findById(id));
    }

    private void setRelationships(
            SectionSubject entity,
            SectionSubjectRequest request) {

        entity.setSection(
                studentSectionRepository.findById(request.getSectionId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Student section not found: "
                                        + request.getSectionId())));

        entity.setOffering(
                subjectOfferingRepository.findById(request.getOfferingId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Subject offering not found: "
                                        + request.getOfferingId())));
    }

    private SectionSubject findById(Long id) {
        return sectionSubjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Section subject not found: " + id));
    }
}
