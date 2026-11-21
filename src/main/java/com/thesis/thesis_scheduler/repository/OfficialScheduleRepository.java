package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.OfficialSchedule;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OfficialScheduleRepository
        extends JpaRepository<OfficialSchedule, Long> {

    Optional<OfficialSchedule> findByTerm_TermId(Long termId);

    boolean existsByTerm_TermId(Long termId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT os
            FROM OfficialSchedule os
            WHERE os.officialScheduleId = :id
            """)
    Optional<OfficialSchedule> findByIdForUpdate(
            @Param("id") Long id
    );
}