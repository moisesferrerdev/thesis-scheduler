package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("""
    SELECT COALESCE(SUM(ta.assignedLoadHours), 0)
    FROM TeachingAssignment ta
    WHERE ta.instructor.instructorId = :instructorId
      AND (:excludedAssignmentId IS NULL
           OR ta.assignmentId <> :excludedAssignmentId)
    """)
    BigDecimal sumAssignedLoadHoursByInstructor(
            @Param("instructorId") Long instructorId,
            @Param("excludedAssignmentId") Long excludedAssignmentId
    );


    @Query("""
    SELECT ta
    FROM TeachingAssignment ta
    JOIN ta.sectionSubject ss
    JOIN ss.section sec
    WHERE sec.academicTerm.termId = :termId
      AND UPPER(ta.assignmentStatus) = 'ACTIVE'
""")
    List<TeachingAssignment> findActiveAssignmentsByTermId(
            @Param("termId") Long termId
    );

}