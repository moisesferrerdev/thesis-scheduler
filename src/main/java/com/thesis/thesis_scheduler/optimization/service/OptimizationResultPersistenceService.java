
package com.thesis.thesis_scheduler.optimization.service;

import com.thesis.thesis_scheduler.entity.GeneratedSchedule;
import com.thesis.thesis_scheduler.entity.GeneratedScheduleEntry;
import com.thesis.thesis_scheduler.entity.OptimizationRun;
import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import com.thesis.thesis_scheduler.entity.Timeslot;

import com.thesis.thesis_scheduler.optimization.ga.GeneticAlgorithmOptimizer;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;

import com.thesis.thesis_scheduler.repository.GeneratedScheduleEntryRepository;
import com.thesis.thesis_scheduler.repository.GeneratedScheduleRepository;
import com.thesis.thesis_scheduler.repository.OptimizationRunRepository;
import com.thesis.thesis_scheduler.repository.RoomRepository;
import com.thesis.thesis_scheduler.repository.TeachingAssignmentRepository;
import com.thesis.thesis_scheduler.repository.TimeslotRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptimizationResultPersistenceService {

    private final OptimizationRunRepository optimizationRunRepository;
    private final GeneratedScheduleRepository generatedScheduleRepository;
    private final GeneratedScheduleEntryRepository generatedScheduleEntryRepository;

    private final TeachingAssignmentRepository teachingAssignmentRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;

    /**
     * Persists every candidate from the final evaluated population.
     *
     * Feasible and infeasible candidates are both retained.
     * Only candidates with complete objective metrics receive
     * an overall fitness score.
     *
     * Candidate persistence does not create ScheduleVersion
     * or ScheduleEntry records.
     */
    @Transactional
    public List<GeneratedSchedule> saveBestResult(
            Long optimizationRunId,
            GeneticAlgorithmOptimizer.OptimizationResult result) {

        if (optimizationRunId == null || result == null) {
            throw new IllegalArgumentException(
                    "Optimization run ID and result are required."
            );
        }

        if (result.finalPopulation() == null
                || result.finalPopulation().isEmpty()) {
            throw new IllegalStateException(
                    "The optimizer returned an empty final population."
            );
        }

        OptimizationRun optimizationRun = optimizationRunRepository
                .findById(optimizationRunId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Optimization run not found: " + optimizationRunId
                ));

        List<GeneratedSchedule> savedCandidates = new ArrayList<>();

        int candidateNumber = 1;

        for (var evaluatedCandidate : result.finalPopulation()) {

            if (evaluatedCandidate == null
                    || evaluatedCandidate.chromosome() == null
                    || evaluatedCandidate.evaluation() == null) {

                throw new IllegalStateException(
                        "Final population contains an incomplete evaluated candidate."
                );
            }

            ScheduleChromosome chromosome =
                    evaluatedCandidate.chromosome();

            var evaluation = evaluatedCandidate.evaluation();

            validateChromosome(chromosome);

            /*
             * 1. Create the candidate record.
             */
            GeneratedSchedule generatedSchedule =
                    new GeneratedSchedule();

            generatedSchedule.setOptimizationRun(optimizationRun);
            generatedSchedule.setCandidateNumber(candidateNumber);

            generatedSchedule.setHardConstraintViolations(
                    evaluation.hardViolations().size()
            );

            generatedSchedule.setFeasible(evaluation.feasible());

            /*
             * Fitness is available only when all objective
             * metrics were successfully calculated.
             */
            if (evaluation.metricsComplete()
                    && evaluation.fitness() != null) {

                generatedSchedule.setFitnessScore(
                        BigDecimal.valueOf(evaluation.fitness())
                );

            } else {
                generatedSchedule.setFitnessScore(null);
            }

            /*
             * Preserve the calculated soft-constraint penalty
             * even when another metric, such as RP, is unavailable.
             */
            Double softPenalty = evaluation.metricValues().sp();

            generatedSchedule.setSoftConstraintPenalty(
                    softPenalty == null
                            ? null
                            : BigDecimal.valueOf(softPenalty)
            );

            GeneratedSchedule savedSchedule =
                    generatedScheduleRepository.save(generatedSchedule);

            /*
             * 2. Convert this candidate's genes into snapshot entries.
             */
            List<GeneratedScheduleEntry> entries =
                    new ArrayList<>();

            for (ScheduleGene gene : chromosome.getGenes()) {

                validateGene(gene);

                TeachingAssignment assignment =
                        teachingAssignmentRepository
                                .findById(gene.getTeachingAssignmentId())
                                .orElseThrow(() -> new IllegalStateException(
                                        "Teaching assignment not found: "
                                                + gene.getTeachingAssignmentId()
                                ));

                Room room = roomRepository
                        .findById(gene.getRoomId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Room not found: " + gene.getRoomId()
                        ));

                Timeslot timeslot = timeslotRepository
                        .findById(gene.getTimeslotId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Timeslot not found: " + gene.getTimeslotId()
                        ));

                GeneratedScheduleEntry entry =
                        new GeneratedScheduleEntry();

                entry.setGeneratedSchedule(savedSchedule);
                entry.setAssignment(assignment);
                entry.setRoom(room);
                entry.setTimeslot(timeslot);
                entry.setSessionType(gene.getSessionType());

                entries.add(entry);
            }

            generatedScheduleEntryRepository.saveAll(entries);

            savedCandidates.add(savedSchedule);

            candidateNumber++;
        }

        return savedCandidates;
    }

    private void validateChromosome(
            ScheduleChromosome chromosome) {

        if (chromosome.getGenes() == null
                || chromosome.getGenes().isEmpty()) {

            throw new IllegalStateException(
                    "Candidate chromosome must contain schedule genes."
            );
        }
    }

    private void validateGene(ScheduleGene gene) {

        if (gene == null
                || gene.getTeachingAssignmentId() == null
                || gene.getRoomId() == null
                || gene.getTimeslotId() == null
                || gene.getSessionType() == null
                || gene.getSessionType().isBlank()) {

            throw new IllegalStateException(
                    "Candidate gene contains missing required schedule data."
            );
        }
    }
}
