
package com.thesis.thesis_scheduler.optimization.ga;

import com.thesis.thesis_scheduler.optimization.model.ScheduleChromosome;
import com.thesis.thesis_scheduler.optimization.service.ObjectiveEvaluationService;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TournamentSelectionOperator {

    public EvaluatedCandidate selectParent(
            List<EvaluatedCandidate> population,
            int tournamentSize) {

        if (population == null || population.isEmpty()) {
            throw new IllegalArgumentException(
                    "Population must not be null or empty.");
        }

        if (tournamentSize <= 0) {
            throw new IllegalArgumentException(
                    "Tournament size must be greater than zero.");
        }

        List<EvaluatedCandidate> eligibleCandidates =
                population.stream()
                        .filter(candidate -> candidate != null)
                        .filter(candidate ->
                                candidate.evaluation().feasible())
                        .toList();

        if (eligibleCandidates.isEmpty()) {
            throw new IllegalStateException(
                    "No feasible candidates are available for selection.");
        }

        int actualTournamentSize =
                Math.min(tournamentSize, eligibleCandidates.size());

        List<EvaluatedCandidate> tournament =
                new ArrayList<>();

        List<EvaluatedCandidate> availableCandidates =
                new ArrayList<>(eligibleCandidates);

        ThreadLocalRandom random =
                ThreadLocalRandom.current();

        for (int i = 0; i < actualTournamentSize; i++) {

            int selectedIndex =
                    random.nextInt(availableCandidates.size());

            tournament.add(
                    availableCandidates.remove(selectedIndex)
            );
        }

        return tournament.stream()
                .max((first, second) ->
                        Double.compare(
                                first.evaluation().fitness(),
                                second.evaluation().fitness()
                        ))
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Unable to select a parent."));
    }

    public record EvaluatedCandidate(
            ScheduleChromosome chromosome,
            ObjectiveEvaluationService.EvaluationResult evaluation) {

        public EvaluatedCandidate {
            if (chromosome == null || evaluation == null) {
                throw new IllegalArgumentException(
                        "Chromosome and evaluation must not be null.");
            }
        }
    }
}
