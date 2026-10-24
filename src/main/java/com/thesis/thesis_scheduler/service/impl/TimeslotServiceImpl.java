
package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.TimeslotRequest;
import com.thesis.thesis_scheduler.dto.TimeslotResponse;
import com.thesis.thesis_scheduler.entity.Timeslot;
import com.thesis.thesis_scheduler.mapper.TimeslotMapper;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;
import com.thesis.thesis_scheduler.service.TimeslotService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeslotServiceImpl implements TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final AcademicTermRepository academicTermRepository;
    private final TimeslotMapper timeslotMapper;

    @Override
    public TimeslotResponse create(TimeslotRequest request) {
        Timeslot entity = timeslotMapper.toEntity(request);
        setRelationships(entity, request);
        return timeslotMapper.toResponse(timeslotRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public TimeslotResponse getById(Long id) {
        return timeslotMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeslotResponse> getAll() {
        return timeslotRepository.findAll()
                .stream()
                .map(timeslotMapper::toResponse)
                .toList();
    }

    @Override
    public TimeslotResponse update(Long id, TimeslotRequest request) {
        Timeslot existing = findById(id);
        timeslotMapper.updateEntity(request, existing);
        setRelationships(existing, request);
        return timeslotMapper.toResponse(timeslotRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        timeslotRepository.delete(findById(id));
    }

    private void setRelationships(Timeslot entity, TimeslotRequest request) {
        entity.setTerm(
                academicTermRepository.findById(request.getTermId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Academic term not found: "
                                        + request.getTermId())));
    }

    private Timeslot findById(Long id) {
        return timeslotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Timeslot not found: " + id));
    }
}
