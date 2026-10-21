package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.InstructorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorAvailabilityRepository
        extends JpaRepository<InstructorAvailability, Long> {

    List<InstructorAvailability>
    findByInstructor_InstructorIdAndTerm_TermId(
            Long instructorId,
            Long termId
    );

    List<InstructorAvailability>
    findByTerm_TermIdAndDayOfWeek(
            Long termId,
            Short dayOfWeek
    );
}