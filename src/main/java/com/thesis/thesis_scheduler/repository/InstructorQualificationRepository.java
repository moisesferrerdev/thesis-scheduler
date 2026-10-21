package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.InstructorQualification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstructorQualificationRepository
        extends JpaRepository<InstructorQualification, Long> {

    List<InstructorQualification> findByInstructor_InstructorId(
            Long instructorId
    );

    List<InstructorQualification> findBySubject_SubjectId(
            Long subjectId
    );

    List<InstructorQualification> findByInstructor_InstructorIdAndApproved(
            Long instructorId,
            Boolean approved
    );

    Optional<InstructorQualification>
    findByInstructor_InstructorIdAndSubject_SubjectId(
            Long instructorId,
            Long subjectId
    );
}