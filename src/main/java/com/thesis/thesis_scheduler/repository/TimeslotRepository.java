package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeslotRepository
        extends JpaRepository<Timeslot, Long> {

    List<Timeslot> findByTerm_TermId(Long termId);

    List<Timeslot> findByTerm_TermIdAndDayOfWeek(
            Long termId,
            Short dayOfWeek
    );
}