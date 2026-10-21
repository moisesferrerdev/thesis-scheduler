package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.SubjectOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectOfferingRepository
        extends JpaRepository<SubjectOffering, Long> {

    List<SubjectOffering> findByTerm_TermId(Long termId);

    List<SubjectOffering> findBySubject_SubjectId(Long subjectId);

    Optional<SubjectOffering> findByTerm_TermIdAndOfferingCode(
            Long termId,
            String offeringCode
    );
}