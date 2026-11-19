
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.dto.OptimizationRunRequest;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import com.thesis.thesis_scheduler.entity.OptimizationRun;
import com.thesis.thesis_scheduler.optimization.ga.GeneticAlgorithmOptimizer;
import com.thesis.thesis_scheduler.optimization.service.OptimizationSettingsResolver.ResolvedOptimizationSettings;
import com.thesis.thesis_scheduler.repository.AcademicTermRepository;
import com.thesis.thesis_scheduler.repository.OptimizationRunRepository;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationExecutionService {

    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    private final AcademicTermRepository academicTermRepository;
    private final OptimizationRunRepository optimizationRunRepository;

    private final OptimizationDataProvider optimizationDataProvider;
    private final OptimizationSettingsResolver optimizationSettingsResolver;
    private final OptimizationWorkerService optimizationWorkerService;
    private final OptimizationResultPersistenceService persistenceService;

    @Transactional
    public OptimizationRun startOptimization(OptimizationRunRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Optimization request must not be null."
            );
        }

        // 1. Retrieve academic term.
        AcademicTerm term = academicTermRepository
                .findById(request.getTermId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Academic term not found: " + request.getTermId()
                ));

        // 2. Resolve optimization settings.
        ResolvedOptimizationSettings settings =
                optimizationSettingsResolver.resolve(
                        request.getAdvancedSettings()
                );

        // 3. Prepare optimization inputs.
        OptimizationDataProvider.OptimizationInput input =
                optimizationDataProvider.prepare(request.getTermId());

        // 4. Create optimization run.
        OptimizationRun optimizationRun = new OptimizationRun();

        optimizationRun.setTerm(term);
        optimizationRun.setRunName(request.getRunName());
        optimizationRun.setAlgorithmName(request.getAlgorithmName());
        optimizationRun.setPopulationSize(request.getPopulationSize());
        optimizationRun.setGenerationLimit(request.getGenerationLimit());
        optimizationRun.setMutationRate(request.getMutationRate());
        optimizationRun.setCrossoverRate(request.getCrossoverRate());
        optimizationRun.setRunStatus(STATUS_RUNNING);
        optimizationRun.setStartedAt(OffsetDateTime.now());

        OptimizationRun savedRun =
                optimizationRunRepository.save(optimizationRun);

        Long runId = savedRun.getOptimizationRunId();

        // 5. Launch optimization only after the transaction commits.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        executeAndPersist(
                                runId,
                                request,
                                settings,
                                input
                        );
                    }
                }
        );

        return savedRun;
    }


    private void executeAndPersist(
            Long runId,
            OptimizationRunRequest request,
            ResolvedOptimizationSettings settings,
            OptimizationDataProvider.OptimizationInput input) {

        try {

            CompletableFuture<GeneticAlgorithmOptimizer.OptimizationResult>
                    future = optimizationWorkerService.executeOptimization(
                    request.getTermId(),
                    request.getPopulationSize(),
                    request.getGenerationLimit(),
                    request.getCrossoverRate(),
                    request.getMutationRate(),
                    settings,
                    input.instructorPreferences(),
                    input.roomPreferences(),
                    input.mc2Assessments()
            );

            future.whenComplete((result, throwable) -> {

                if (throwable != null) {

                    log.error(
                            "Genetic Algorithm execution failed. Run ID: {}",
                            runId,
                            throwable
                    );

                    updateRunStatus(runId, STATUS_FAILED);
                    return;
                }

                if (result == null) {

                    log.error(
                            "Genetic Algorithm returned a null result. Run ID: {}",
                            runId
                    );

                    updateRunStatus(runId, STATUS_FAILED);
                    return;
                }

                try {

                    log.info(
                            "Genetic Algorithm completed. Persisting result. Run ID: {}",
                            runId
                    );

                    persistenceService.saveBestResult(runId, result);

                    updateRunStatus(runId, STATUS_COMPLETED);

                    log.info(
                            "Optimization completed successfully. Run ID: {}",
                            runId
                    );

                } catch (RuntimeException exception) {

                    log.error(
                            "Failed to persist optimization result. Run ID: {}",
                            runId,
                            exception
                    );

                    updateRunStatus(runId, STATUS_FAILED);
                }
            });

        } catch (RuntimeException exception) {

            log.error(
                    "Failed to start optimization execution. Run ID: {}",
                    runId,
                    exception
            );

            updateRunStatus(runId, STATUS_FAILED);
        }
    }


    private void updateRunStatus(Long runId, String status) {

        OptimizationRun run = optimizationRunRepository
                .findById(runId)
                .orElseThrow(() -> new IllegalStateException(
                        "Optimization run not found: " + runId
                ));

        run.setRunStatus(status);
        run.setCompletedAt(OffsetDateTime.now());

        optimizationRunRepository.save(run);
    }
}
