package com.thesis.thesis_scheduler.repository;

import com.thesis.thesis_scheduler.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SubjectRepository
        extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findByStatus(String status);
}