package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.SchedulingConstraint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulingConstraintRepository
        extends JpaRepository<SchedulingConstraint, Long> {

    List<SchedulingConstraint>
    findByTerm_TermIdAndActive(
            Long termId,
            Boolean active
    );

    List<SchedulingConstraint>
    findByTerm_TermIdAndConstraintCategoryAndActive(
            Long termId,
            String constraintCategory,
            Boolean active
    );
}