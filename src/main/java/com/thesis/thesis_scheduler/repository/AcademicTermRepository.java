package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicTermRepository
        extends JpaRepository<AcademicTerm, Long> {

    List<AcademicTerm> findByStatus(String status);
}