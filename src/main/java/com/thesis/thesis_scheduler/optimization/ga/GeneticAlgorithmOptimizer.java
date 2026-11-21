
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
import java.util.OptionalDouble;

@Component
@RequiredArgsConstructor
public class GeneticAlgorithmOptimizer {

    private static final int MAX_OFFSPRING_ATTEMPTS_MULTIPLIER = 20;

    private final InitialPopulationGenerator initialPopulationGenerator;
    private final TournamentSelectionOperator tournamentSelectionOperator;
    private final ScheduleCrossoverOperator scheduleCrossoverOperator;
    private final ScheduleMutationOperator scheduleMutationOperator;
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

        // 1. Generate the initial population.
        List<ScheduleChromosome> initialPopulation =
                initialPopulationGenerator.generatePopulation(
                        termId,
                        populationSize
                );

        if (initialPopulation == null || initialPopulation.isEmpty()) {
            throw new IllegalStateException(
                    "Initial population generator returned no candidates."
            );
        }

        // 2. Load scheduling resources.
        InitialPopulationGenerator.SchedulingResources resources =
                initialPopulationGenerator.loadResources(termId);

        List<Room> availableRooms = resources.rooms();
        List<Timeslot> availableTimeslots = resources.timeslots();

        // 3. Evaluate every candidate in the initial population.
        List<TournamentSelectionOperator.EvaluatedCandidate>
                finalEvaluatedPopulation =
                evaluatePopulation(
                        initialPopulation,
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

        // Generation history starts with the evaluated initial population.
        List<GenerationSnapshot> generationHistory = new ArrayList<>();

        generationHistory.add(
                createGenerationSnapshot(
                        0,
                        finalEvaluatedPopulation
                )
        );

        /*
         * The working population contains feasible candidates only.
         * Infeasible candidates remain in finalEvaluatedPopulation
         * for research and persistence.
         */
        List<TournamentSelectionOperator.EvaluatedCandidate>
                evaluatedPopulation =
                getFeasibleCandidates(finalEvaluatedPopulation);

        TournamentSelectionOperator.EvaluatedCandidate bestCandidate =
                getBestCandidateOrNull(evaluatedPopulation);

        int completedGenerations = 0;

        /*
         * If the initial population has no feasible candidate,
         * the optimizer cannot safely perform parent selection.
         *
         * Return the evaluated initial population so that it can
         * still be persisted and inspected for research.
         */
        if (evaluatedPopulation.isEmpty()) {
            return new OptimizationResult(
                    null,
                    null,
                    finalEvaluatedPopulation,
                    completedGenerations,
                    finalEvaluatedPopulation.size(),
                    generationHistory
            );
        }

        // 4. Evolution process.
        for (int generation = 1;
             generation <= maxGenerations;
             generation++) {

            if (bestCandidate.evaluation().fitness() >= targetFitness) {
                break;
            }

            /*
             * This list represents every candidate evaluated
             * for the current generation.
             *
             * It includes the elite candidate and all evaluated
             * offspring, including infeasible offspring.
             */
            List<TournamentSelectionOperator.EvaluatedCandidate>
                    nextEvaluatedPopulation =
                    new ArrayList<>();

            /*
             * This list is used by the GA for the next generation.
             * It contains feasible candidates only.
             */
            List<TournamentSelectionOperator.EvaluatedCandidate>
                    nextFeasiblePopulation =
                    new ArrayList<>();

            // 5. Elitism: retain the best feasible candidate.
            nextEvaluatedPopulation.add(bestCandidate);
            nextFeasiblePopulation.add(bestCandidate);

            int offspringAttempts = 0;

            int maximumOffspringAttempts =
                    populationSize * MAX_OFFSPRING_ATTEMPTS_MULTIPLIER;

            /*
             * Generate and evaluate offspring.
             *
             * We stop when:
             * - The evaluated generation reaches populationSize, or
             * - The maximum number of attempts is reached.
             */
            while (nextEvaluatedPopulation.size() < populationSize
                    && offspringAttempts < maximumOffspringAttempts) {

                offspringAttempts++;

                // 6. Select feasible parents only.
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

                // 7. Crossover.
                if (Math.random() < crossoverRate) {
                    offspring = scheduleCrossoverOperator.crossover(
                            parent1,
                            parent2
                    );
                } else {
                    offspring = copyChromosome(parent1);
                }

                // 8. Mutation.
                offspring = scheduleMutationOperator.mutate(
                        offspring,
                        availableTimeslots,
                        availableRooms,
                        mutationRate
                );

                // 9. Evaluate every offspring.
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

                TournamentSelectionOperator.EvaluatedCandidate
                        evaluatedCandidate =
                        new TournamentSelectionOperator.EvaluatedCandidate(
                                offspring,
                                evaluation
                        );

                // Retain every evaluated offspring.
                nextEvaluatedPopulation.add(evaluatedCandidate);

                // Only eligible candidates enter the working population.
                if (isEligibleForEvolution(evaluatedCandidate)) {
                    nextFeasiblePopulation.add(evaluatedCandidate);
                }
            }

            /*
             * The elite candidate guarantees that the next feasible
             * population contains at least one feasible candidate.
             *
             * Refill the working population by reusing feasible
             * candidates if not enough feasible offspring were found.
             */
            List<TournamentSelectionOperator.EvaluatedCandidate>
                    nextWorkingPopulation =
                    refillWorkingPopulation(
                            nextFeasiblePopulation,
                            populationSize
                    );

            // 10. Replace the working population.
            evaluatedPopulation = nextWorkingPopulation;

            // 11. Update the best feasible candidate.
            TournamentSelectionOperator.EvaluatedCandidate generationBest =
                    getBestCandidateOrNull(evaluatedPopulation);

            if (generationBest != null
                    && generationBest.evaluation().fitness()
                    > bestCandidate.evaluation().fitness()) {

                bestCandidate = generationBest;
            }

            /*
             * Preserve the complete evaluated generation.
             * This becomes the final population if the algorithm
             * terminates after this generation.
             */
            finalEvaluatedPopulation =
                    List.copyOf(nextEvaluatedPopulation);

            completedGenerations = generation;

            // Record the actual evaluated candidates for this generation.
            generationHistory.add(
                    createGenerationSnapshot(
                            generation,
                            finalEvaluatedPopulation
                    )
            );

            /*
             * If the attempt limit was reached, the generation may
             * contain fewer candidates than the configured population.
             * Retain what was actually evaluated.
             */
            if (offspringAttempts >= maximumOffspringAttempts
                    && nextEvaluatedPopulation.size() < populationSize) {
                break;
            }
        }

        // 12. Return the best feasible candidate and complete final population.
        return new OptimizationResult(
                bestCandidate.chromosome(),
                bestCandidate.evaluation(),
                finalEvaluatedPopulation,
                completedGenerations,
                finalEvaluatedPopulation.size(),
                generationHistory
        );
    }

    private GenerationSnapshot createGenerationSnapshot(
            int generationNumber,
            List<TournamentSelectionOperator.EvaluatedCandidate> candidates) {

        long feasibleCandidateCount = candidates.stream()
                .filter(candidate ->
                        candidate != null
                                && candidate.evaluation() != null
                                && candidate.evaluation().feasible()
                )
                .count();

        /*
         * Fitness statistics are calculated only for candidates
         * eligible for evolution:
         * - Feasible
         * - Complete metrics
         * - Valid finite fitness
         */
        List<Double> eligibleFitnessValues = candidates.stream()
                .filter(this::isEligibleForEvolution)
                .map(candidate -> candidate.evaluation().fitness())
                .toList();

        Double bestFitness = eligibleFitnessValues.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .isPresent()
                ? eligibleFitnessValues.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .getAsDouble()
                : null;

        OptionalDouble averageResult = eligibleFitnessValues.stream()
                .mapToDouble(Double::doubleValue)
                .average();

        Double averageFitness = averageResult.isPresent()
                ? averageResult.getAsDouble()
                : null;

        return new GenerationSnapshot(
                generationNumber,
                bestFitness,
                averageFitness,
                (int) feasibleCandidateCount,
                candidates.size()
        );
    }

    private List<TournamentSelectionOperator.EvaluatedCandidate>
    evaluatePopulation(
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

        List<TournamentSelectionOperator.EvaluatedCandidate>
                evaluatedCandidates =
                new ArrayList<>();

        for (ScheduleChromosome chromosome : population) {

            if (chromosome == null
                    || chromosome.getGenes() == null
                    || chromosome.getGenes().isEmpty()) {
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

            evaluatedCandidates.add(
                    new TournamentSelectionOperator.EvaluatedCandidate(
                            chromosome,
                            evaluation
                    )
            );
        }

        if (evaluatedCandidates.isEmpty()) {
            throw new IllegalStateException(
                    "No structurally valid candidates could be evaluated."
            );
        }

        return evaluatedCandidates;
    }

    private List<TournamentSelectionOperator.EvaluatedCandidate>
    getFeasibleCandidates(
            List<TournamentSelectionOperator.EvaluatedCandidate> candidates) {

        return candidates.stream()
                .filter(this::isEligibleForEvolution)
                .toList();
    }

    private List<TournamentSelectionOperator.EvaluatedCandidate>
    refillWorkingPopulation(
            List<TournamentSelectionOperator.EvaluatedCandidate> feasibleCandidates,
            int requiredSize
    ) {

        if (feasibleCandidates == null || feasibleCandidates.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot create a working population without feasible candidates."
            );
        }

        List<TournamentSelectionOperator.EvaluatedCandidate> result =
                new ArrayList<>(feasibleCandidates);

        int index = 0;

        while (result.size() < requiredSize) {
            result.add(
                    feasibleCandidates.get(index % feasibleCandidates.size())
            );

            index++;
        }

        if (result.size() > requiredSize) {
            return new ArrayList<>(
                    result.subList(0, requiredSize)
            );
        }

        return result;
    }

    private TournamentSelectionOperator.EvaluatedCandidate
    getBestCandidateOrNull(
            List<TournamentSelectionOperator.EvaluatedCandidate> candidates) {

        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        return candidates.stream()
                .filter(this::isEligibleForEvolution)
                .max(Comparator.comparingDouble(
                        candidate -> candidate.evaluation().fitness()
                ))
                .orElse(null);
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

    public record GenerationSnapshot(
            int generationNumber,
            Double bestFitness,
            Double averageFitness,
            int feasibleCandidateCount,
            int evaluatedCandidateCount
    ) {
    }

    public record OptimizationResult(
            ScheduleChromosome bestChromosome,
            EvaluationResult evaluation,
            List<TournamentSelectionOperator.EvaluatedCandidate> finalPopulation,
            int completedGenerations,
            int finalPopulationSize,
            List<GenerationSnapshot> generationHistory
    ) {

        public OptimizationResult {
            if (finalPopulation == null || finalPopulation.isEmpty()) {
                throw new IllegalArgumentException(
                        "Final evaluated population must not be empty."
                );
            }

            finalPopulation = List.copyOf(finalPopulation);

            if (finalPopulationSize != finalPopulation.size()) {
                finalPopulationSize = finalPopulation.size();
            }

            if ((bestChromosome == null) != (evaluation == null)) {
                throw new IllegalArgumentException(
                        "Best chromosome and its evaluation must either both be present or both be null."
                );
            }

            if (evaluation != null && !evaluation.feasible()) {
                throw new IllegalArgumentException(
                        "Best candidate must be feasible."
                );
            }

            if (generationHistory == null || generationHistory.isEmpty()) {
                throw new IllegalArgumentException(
                        "Generation history must not be empty."
                );
            }

            generationHistory = List.copyOf(generationHistory);
        }
    }

    private boolean isEligibleForEvolution(
            TournamentSelectionOperator.EvaluatedCandidate candidate) {

        if (candidate == null || candidate.evaluation() == null) {
            return false;
        }

        EvaluationResult evaluation = candidate.evaluation();

        return evaluation.feasible()
                && evaluation.metricsComplete()
                && evaluation.fitness() != null
                && Double.isFinite(evaluation.fitness());
    }
}
