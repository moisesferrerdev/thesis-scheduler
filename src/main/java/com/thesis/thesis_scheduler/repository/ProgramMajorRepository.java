
package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.ProgramMajor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramMajorRepository
        extends JpaRepository<ProgramMajor, Long> {

    List<ProgramMajor> findByProgram_ProgramId(Long programId);

    List<ProgramMajor> findByProgram_ProgramIdAndStatus(
            Long programId,
            String status
    );

    Optional<ProgramMajor> findByProgram_ProgramIdAndMajorCode(
            Long programId,
            String majorCode
    );

    boolean existsByProgram_ProgramIdAndMajorCode(
            Long programId,
            String majorCode
    );

    boolean existsByProgram_ProgramIdAndMajorCodeAndProgramMajorIdNot(
            Long programId,
            String majorCode,
            Long programMajorId
    );
}
