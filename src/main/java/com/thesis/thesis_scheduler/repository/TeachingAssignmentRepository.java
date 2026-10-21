package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachingAssignmentRepository
        extends JpaRepository<TeachingAssignment, Long> {

    List<TeachingAssignment> findByInstructor_InstructorId(
            Long instructorId
    );

    List<TeachingAssignment> findBySectionSubject_SectionSubjectId(
            Long sectionSubjectId
    );

    List<TeachingAssignment> findByAssignmentStatus(
            String assignmentStatus
    );
}