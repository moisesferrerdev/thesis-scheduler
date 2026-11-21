package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleEntryResponse;
import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;
import com.thesis.thesis_scheduler.entity.GeneratedSchedule;
import com.thesis.thesis_scheduler.mapper.GeneratedScheduleEntryMapper;
import com.thesis.thesis_scheduler.mapper.GeneratedScheduleMapper;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleEntryRepository;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleRepository;
import com.thesis.thesis_scheduler.repository.OptimizationRunRepository;
import com.thesis.thesis_scheduler.service.GeneratedScheduleService;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeneratedScheduleServiceImpl
        implements GeneratedScheduleService {

    private final GeneratedScheduleRepository repository;

    private final GeneratedScheduleEntryRepository entryRepository;

    private final OptimizationRunRepository optimizationRunRepository;

    private final GeneratedScheduleMapper mapper;

    private final GeneratedScheduleEntryMapper entryMapper;

    @Override
    public GeneratedScheduleResponse getById(Long id) {

        GeneratedSchedule generatedSchedule = findById(id);

        return toResponseWithEntries(generatedSchedule);
    }

    @Override
    public List<GeneratedScheduleResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::toResponseWithEntries)
                .toList();
    }

    @Override
    public List<GeneratedScheduleResponse> getByOptimizationRunId(
            Long optimizationRunId) {

        if (optimizationRunId == null) {
            throw new IllegalArgumentException(
                    "Optimization run ID is required."
            );
        }

        if (!optimizationRunRepository.existsById(optimizationRunId)) {
            throw new EntityNotFoundException(
                    "Optimization run not found: " + optimizationRunId
            );
        }

        return repository
                .findByOptimizationRun_OptimizationRunId(optimizationRunId)
                .stream()
                .map(this::toResponseWithEntries)
                .toList();
    }

    private GeneratedScheduleResponse toResponseWithEntries(
            GeneratedSchedule generatedSchedule) {

        GeneratedScheduleResponse response =
                mapper.toResponse(generatedSchedule);

        List<GeneratedScheduleEntryResponse> entries =
                entryRepository
                        .findByGeneratedSchedule_GeneratedScheduleId(
                                generatedSchedule.getGeneratedScheduleId()
                        )
                        .stream()
                        .map(entryMapper::toResponse)
                        .toList();

        response.setEntries(entries);

        return response;
    }

    private GeneratedSchedule findById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Generated schedule ID must be positive."
            );
        }

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Generated schedule not found: " + id
                ));
    }
}