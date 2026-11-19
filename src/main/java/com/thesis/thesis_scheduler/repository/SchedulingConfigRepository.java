package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.SchedulingConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchedulingConfigRepository
        extends JpaRepository<SchedulingConfig, Long> {

    Optional<SchedulingConfig> findByTerm_TermId(Long termId);

    List<SchedulingConfig> findAllByOrderByTerm_StartDateDesc();
}