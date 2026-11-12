package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository
        extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByEmployeeNo(String employeeNo);

    List<Instructor> findByEmploymentType(String employmentType);

    List<Instructor> findByStatus(String status);

    @Query(
            value = "SELECT nextval('instructor_employee_no_seq')",
            nativeQuery = true
    )
    Long getNextEmployeeNumber();
}
