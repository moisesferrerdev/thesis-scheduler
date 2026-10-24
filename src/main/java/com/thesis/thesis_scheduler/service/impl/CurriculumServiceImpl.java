
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.CurriculumRequest;
import com.thesis.thesis_scheduler.dto.CurriculumResponse;
import com.thesis.thesis_scheduler.entity.Curriculum;
import com.thesis.thesis_scheduler.mapper.CurriculumMapper;
import com.thesis.thesis_scheduler.repository.CurriculumRepository;
import com.thesis.thesis_scheduler.repository.ProgramRepository;
import com.thesis.thesis_scheduler.repository.SubjectRepository;
import com.thesis.thesis_scheduler.service.CurriculumService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;
    private final CurriculumMapper curriculumMapper;

    @Override
    public CurriculumResponse create(CurriculumRequest request) {
        Curriculum curriculum = curriculumMapper.toEntity(request);
        setRelationships(curriculum, request);

        Curriculum saved = curriculumRepository.save(curriculum);
        return curriculumMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumResponse getById(Long id) {
        return curriculumMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getAll() {
        return curriculumRepository.findAll()
                .stream()
                .map(curriculumMapper::toResponse)
                .toList();
    }

    @Override
    public CurriculumResponse update(Long id, CurriculumRequest request) {
        Curriculum existing = findById(id);

        curriculumMapper.updateEntity(request, existing);
        setRelationships(existing, request);

        Curriculum updated = curriculumRepository.save(existing);
        return curriculumMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        curriculumRepository.delete(findById(id));
    }

    private void setRelationships(
            Curriculum curriculum,
            CurriculumRequest request) {

        curriculum.setProgram(
                programRepository.findById(request.getProgramId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Program not found: "
                                        + request.getProgramId())));

        curriculum.setSubject(
                subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Subject not found: "
                                        + request.getSubjectId())));
    }

    private Curriculum findById(Long id) {
        return curriculumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curriculum not found: " + id));
    }
}
