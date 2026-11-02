
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.entity.Room;
import com.thesis.thesis_scheduler.entity.Timeslot;

import com.thesis.thesis_scheduler.optimization.constraint.RoomCapacityConstraint;
import com.thesis.thesis_scheduler.optimization.fitness.FullTimeFacultyPrioritizationEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredRoomAssignmentEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.PreferredTeachingScheduleEvaluator;
import com.thesis.thesis_scheduler.optimization.fitness.FitnessCalculator.ObjectiveWeights;
import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.model.ScheduleGene;
import com.thesis.thesis_scheduler.optimization.service.ObjectiveEvaluationService;
import com.thesis.thesis_scheduler.optimization.service.ObjectiveEvaluationService.EvaluationResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeneticAlgorithmOptimizer {

    private final InitialPopulationGenerator initialPopulationGenerator;
    private final TournamentSelectionOperator tournamentSelectionOperator;
    private final ScheduleCrossoverOperator scheduleCrossoverOperator;
    private final ScheduleMutationOperator scheduleMutationOperator;
    private final CompleteScheduleValidator completeScheduleValidator;
    private final ObjectiveEvaluationService objectiveEvaluationService;

    public OptimizationResult optimize(
            Long termId,
            int populationSize,
            int maxGenerations,
            int tournamentSize,
            double crossoverRate,
            double mutationRate,
            double targetFitness,
            RoomCapacityConstraint.CapacityLookup capacityLookup,
            Map<Long, PreferredTeachingScheduleEvaluator.InstructorTeachingPreference> instructorPreferences,
            Map<Long, PreferredRoomAssignmentEvaluator.PreferredRoomPreference> roomPreferences,
            int sc3PenaltyPerGapHour,
            int sc4PenaltyPerGapHour,
            int sc5PenaltyPerImbalanceHour,
            Map<Long, FullTimeFacultyPrioritizationEvaluator.AssignmentPriorityAssessment> mc2Assessments,
            ObjectiveWeights weights
    ) {

        validateParameters(
                populationSize,
                maxGenerations,
                tournamentSize,
                crossoverRate,
                mutationRate,
                targetFitness
        );

        // Generate the initial population.
        List<ScheduleChromosome> population =
                initialPopulationGenerator.generatePopulation(
                        termId,
                        populationSize
                );

        // Load scheduling resources.
        InitialPopulationGenerator.SchedulingResources resources =
                initialPopulationGenerator.loadResources(termId);

        List<Room> availableRooms = resources.rooms();
        List<Timeslot> availableTimeslots = resources.timeslots();

        // Evaluate the initial population.
        List<TournamentSelectionOperator.EvaluatedCandidate> evaluatedPopulation =
                evaluatePopulation(
                        population,
                        termId,
                        capacityLookup,
                        instructorPreferences,
                        roomPreferences,
                        sc3PenaltyPerGapHour,
                        sc4PenaltyPerGapHour,
                        sc5PenaltyPerImbalanceHour,
                        mc2Assessments,
                        weights
                );

        TournamentSelectionOperator.EvaluatedCandidate bestCandidate =
                getBestCandidate(evaluatedPopulation);

        int completedGenerations = 0;

        // Evolution process.
        for (int generation = 1;
             generation <= maxGenerations;
             generation++) {

            if (bestCandidate.evaluation().fitness() >= targetFitness) {
                break;
            }

            List<TournamentSelectionOperator.EvaluatedCandidate> nextGeneration =
                    new ArrayList<>();

            // Elitism: preserve the best candidate.
            nextGeneration.add(bestCandidate);

            while (nextGeneration.size() < populationSize) {

                // Select parents using tournament selection.
                ScheduleChromosome parent1 =
                        tournamentSelectionOperator.selectParent(
                                evaluatedPopulation,
                                tournamentSize
                        ).chromosome();

                ScheduleChromosome parent2 =
                        tournamentSelectionOperator.selectParent(
                                evaluatedPopulation,
                                tournamentSize
                        ).chromosome();

                ScheduleChromosome offspring;

                // Crossover.
                if (Math.random() < crossoverRate) {
                    offspring = scheduleCrossoverOperator.crossover(
                            parent1,
                            parent2
                    );
                } else {
                    offspring = copyChromosome(parent1);
                }

                // Mutation.
                offspring = scheduleMutationOperator.mutate(
                        offspring,
                        availableTimeslots,
                        availableRooms,
                        mutationRate
                );

                // Validate offspring.
                CompleteScheduleValidator.ValidationResult validationResult =
                        completeScheduleValidator.validate(
                                offspring,
                                termId
                        );

                if (!validationResult.feasible()) {
                    continue;
                }

                // Evaluate offspring.
                EvaluationResult evaluation =
                        objectiveEvaluationService.evaluate(
                                offspring,
                                termId,
                                capacityLookup,
                                instructorPreferences,
                                roomPreferences,
                                sc3PenaltyPerGapHour,
                                sc4PenaltyPerGapHour,
                                sc5PenaltyPerImbalanceHour,
                                mc2Assessments,
                                weights
                        );

                if (evaluation.feasible()) {
                    nextGeneration.add(
                            new TournamentSelectionOperator.EvaluatedCandidate(
                                    offspring,
                                    evaluation
                            )
                    );
                }
            }

            // Maintain the required population size.
            while (nextGeneration.size() < populationSize) {

                nextGeneration.addAll(evaluatedPopulation);

                if (nextGeneration.size() > populationSize) {
                    nextGeneration = new ArrayList<>(
                            nextGeneration.subList(0, populationSize)
                    );
                }
            }

            evaluatedPopulation = nextGeneration;

            // Find the best candidate in this generation.
            TournamentSelectionOperator.EvaluatedCandidate generationBest =
                    getBestCandidate(evaluatedPopulation);

            if (generationBest.evaluation().fitness()
                    > bestCandidate.evaluation().fitness()) {

                bestCandidate = generationBest;
            }

            completedGenerations = generation;
        }

        return new OptimizationResult(
                bestCandidate.chromosome(),
                bestCandidate.evaluation(),
                completedGenerations,
                evaluatedPopulation.size()
        );
    }

    private List<TournamentSelectionOperator.EvaluatedCandidate> evaluatePopulation(
            List<ScheduleChromosome> population,
            Long termId,
            RoomCapacityConstraint.CapacityLookup capacityLookup,
            Map<Long, PreferredTeachingScheduleEvaluator.InstructorTeachingPreference> instructorPreferences,
            Map<Long, PreferredRoomAssignmentEvaluator.PreferredRoomPreference> roomPreferences,
            int sc3PenaltyPerGapHour,
            int sc4PenaltyPerGapHour,
            int sc5PenaltyPerImbalanceHour,
            Map<Long, FullTimeFacultyPrioritizationEvaluator.AssignmentPriorityAssessment> mc2Assessments,
            ObjectiveWeights weights
    ) {

        List<TournamentSelectionOperator.EvaluatedCandidate> evaluatedCandidates =
                new ArrayList<>();

        for (ScheduleChromosome chromosome : population) {

            CompleteScheduleValidator.ValidationResult validationResult =
                    completeScheduleValidator.validate(
                            chromosome,
                            termId
                    );

            if (!validationResult.feasible()) {
                continue;
            }

            EvaluationResult evaluation =
                    objectiveEvaluationService.evaluate(
                            chromosome,
                            termId,
                            capacityLookup,
                            instructorPreferences,
                            roomPreferences,
                            sc3PenaltyPerGapHour,
                            sc4PenaltyPerGapHour,
                            sc5PenaltyPerImbalanceHour,
                            mc2Assessments,
                            weights
                    );

            if (evaluation.feasible()) {
                evaluatedCandidates.add(
                        new TournamentSelectionOperator.EvaluatedCandidate(
                                chromosome,
                                evaluation
                        )
                );
            }
        }

        if (evaluatedCandidates.isEmpty()) {
            throw new IllegalStateException(
                    "No feasible candidates were found in the initial population."
            );
        }

        return evaluatedCandidates;
    }

    private TournamentSelectionOperator.EvaluatedCandidate getBestCandidate(
            List<TournamentSelectionOperator.EvaluatedCandidate> candidates
    ) {

        return candidates.stream()
                .max(Comparator.comparingDouble(
                        candidate -> candidate.evaluation().fitness()
                ))
                .orElseThrow(() -> new IllegalStateException(
                        "No evaluated candidates are available."
                ));
    }

    private ScheduleChromosome copyChromosome(
            ScheduleChromosome original
    ) {

        List<ScheduleGene> copiedGenes = new ArrayList<>();

        for (ScheduleGene gene : original.getGenes()) {

            ScheduleGene copy = new ScheduleGene(
                    gene.getTeachingAssignmentId(),
                    gene.getInstructorId(),
                    gene.getSectionId(),
                    gene.getRoomId(),
                    gene.getTimeslotId()
            );

            copy.setSessionType(gene.getSessionType());
            copy.setSessionGroupId(gene.getSessionGroupId());

            copiedGenes.add(copy);
        }

        return new ScheduleChromosome(copiedGenes);
    }

    private void validateParameters(
            int populationSize,
            int maxGenerations,
            int tournamentSize,
            double crossoverRate,
            double mutationRate,
            double targetFitness
    ) {

        if (populationSize < 5) {
            throw new IllegalArgumentException(
                    "Population size must be at least 5."
            );
        }

        if (maxGenerations < 1) {
            throw new IllegalArgumentException(
                    "Maximum generations must be at least 1."
            );
        }

        if (tournamentSize < 1 || tournamentSize > populationSize) {
            throw new IllegalArgumentException(
                    "Tournament size must be between 1 and population size."
            );
        }

        if (crossoverRate < 0 || crossoverRate > 1) {
            throw new IllegalArgumentException(
                    "Crossover rate must be between 0 and 1."
            );
        }

        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException(
                    "Mutation rate must be between 0 and 1."
            );
        }

        if (targetFitness < 0 || targetFitness > 1) {
            throw new IllegalArgumentException(
                    "Target fitness must be between 0 and 1."
            );
        }
    }

    public record OptimizationResult(
            ScheduleChromosome bestChromosome,
            EvaluationResult evaluation,
            int completedGenerations,
            int finalPopulationSize
    ) {
    }
}
