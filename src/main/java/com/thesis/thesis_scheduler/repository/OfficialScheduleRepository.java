package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.OfficialSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficialScheduleRepository
        extends JpaRepository<OfficialSchedule, Long> {

    Optional<OfficialSchedule> findByTerm_TermId(Long termId);

    boolean existsByTerm_TermId(Long termId);
}