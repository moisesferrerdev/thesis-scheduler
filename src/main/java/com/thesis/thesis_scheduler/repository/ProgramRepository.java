package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProgramRepository
        extends JpaRepository<Program, Long> {

    Optional<Program> findByProgramCode(String programCode);

    List<Program> findByStatus(String status);
}