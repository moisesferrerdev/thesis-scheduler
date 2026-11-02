 package com.thesis.thesis_scheduler.optimization.ga;

import com.fasterxml.jackson.databind.JsonNode;
import com.thesis.thesis_scheduler.entity.SchedulingConstraint;
import com.thesis.thesis_scheduler.repository.SchedulingConstraintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SchedulingRuleProvider {

    private final SchedulingConstraintRepository
            schedulingConstraintRepository;

    public List<SchedulingConstraint> loadActiveRules(Long termId) {

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Term ID must be a positive value."
            );
        }

        List<SchedulingConstraint> rules =
                schedulingConstraintRepository
                        .findByTerm_TermIdAndActive(termId, true);

        return List.copyOf(rules);
    }

    public List<SchedulingConstraint> loadActiveRulesByCategory(
            Long termId,
            String category) {

        if (termId == null || termId <= 0) {
            throw new IllegalArgumentException(
                    "Term ID must be a positive value."
            );
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Constraint category must not be empty."
            );
        }

        return List.copyOf(
                schedulingConstraintRepository
                        .findByTerm_TermIdAndConstraintCategoryAndActive(
                                termId,
                                category,
                                true
                        )
        );
    }

    public SchedulingConstraint findRule(
            List<SchedulingConstraint> rules,
            String constraintCode) {

        if (rules == null || constraintCode == null
                || constraintCode.isBlank()) {
            return null;
        }

        return rules.stream()
                .filter(rule -> rule.getConstraintCode() != null)
                .filter(rule -> rule.getConstraintCode()
                        .equalsIgnoreCase(constraintCode))
                .findFirst()
                .orElse(null);
    }

    public JsonNode getConfiguration(
            SchedulingConstraint constraint) {

        if (constraint == null) {
            return null;
        }

        return constraint.getConfiguration();
    }

    public boolean isHardConstraint(
            SchedulingConstraint constraint) {

        return constraint != null
                && constraint.getConstraintType() != null
                && "HARD".equalsIgnoreCase(
                constraint.getConstraintType()
        );
    }

    public boolean isSoftConstraint(
            SchedulingConstraint constraint) {

        return constraint != null
                && constraint.getConstraintType() != null
                && "SOFT".equalsIgnoreCase(
                constraint.getConstraintType()
        );
    }

    public String normalizeCategory(String category) {

        if (category == null || category.isBlank()) {
            return null;
        }

        return category.trim().toUpperCase(Locale.ROOT);
    }
}