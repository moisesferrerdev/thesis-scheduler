package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.StudentSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSectionRepository
        extends JpaRepository<StudentSection, Long> {

    List<StudentSection> findByAcademicTerm_TermId(Long termId);

    List<StudentSection> findByProgram_ProgramId(Long programId);

    Optional<StudentSection> findByAcademicTerm_TermIdAndSectionCode(
            Long termId,
            String sectionCode
    );
}