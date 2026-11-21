package com.thesis.thesis_scheduler.service.impl;

import com.thesis.thesis_scheduler.dto.OptimizationGenerationResponse;
import com.thesis.thesis_scheduler.entity.OptimizationGeneration;
import com.thesis.thesis_scheduler.repository.OptimizationGenerationRepository;
import com.thesis.thesis_scheduler.repository.OptimizationRunRepository;
import com.thesis.thesis_scheduler.service.OptimizationGenerationService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OptimizationGenerationServiceImpl
        implements OptimizationGenerationService {

    private final OptimizationGenerationRepository generationRepository;
    private final OptimizationRunRepository optimizationRunRepository;

    @Override
    public List<OptimizationGenerationResponse> getByOptimizationRunId(
            Long optimizationRunId) {

        if (optimizationRunId == null || optimizationRunId <= 0) {
            throw new IllegalArgumentException(
                    "Optimization run ID must be positive."
            );
        }

        if (!optimizationRunRepository.existsById(optimizationRunId)) {
            throw new EntityNotFoundException(
                    "Optimization run not found: " + optimizationRunId
            );
        }

        return generationRepository
                .findByOptimizationRun_OptimizationRunIdOrderByGenerationNumberAsc(
                        optimizationRunId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OptimizationGenerationResponse toResponse(
            OptimizationGeneration generation) {

        return new OptimizationGenerationResponse(
                generation.getOptimizationGenerationId(),
                generation.getOptimizationRun().getOptimizationRunId(),
                generation.getGenerationNumber(),
                generation.getBestFitness(),
                generation.getAverageFitness(),
                generation.getFeasibleCandidateCount(),
                generation.getEvaluatedCandidateCount(),
                generation.getRecordedAt()
        );
    }
}