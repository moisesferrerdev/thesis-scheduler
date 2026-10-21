package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.SectionSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionSubjectRepository
        extends JpaRepository<SectionSubject, Long> {

    List<SectionSubject> findBySection_SectionId(Long sectionId);

    List<SectionSubject> findByOffering_OfferingId(Long offeringId);

    Optional<SectionSubject> findBySection_SectionIdAndOffering_OfferingId(
            Long sectionId,
            Long offeringId
    );
}