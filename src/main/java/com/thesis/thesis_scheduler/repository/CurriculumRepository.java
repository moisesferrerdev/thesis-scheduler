package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumRepository
        extends JpaRepository<Curriculum, Long> {

    List<Curriculum> findByProgram_ProgramId(Long programId);

    List<Curriculum> findBySubject_SubjectId(Long subjectId);

    List<Curriculum> findByCurriculumYear(String curriculumYear);
}