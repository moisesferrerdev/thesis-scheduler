
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.InstructorQualificationRequest;
import com.thesis.thesis_scheduler.dto.InstructorQualificationResponse;
import com.thesis.thesis_scheduler.service.InstructorQualificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor-qualifications")
@RequiredArgsConstructor
public class InstructorQualificationController {

    private final InstructorQualificationService
            instructorQualificationService;

    @PostMapping
    public ResponseEntity<InstructorQualificationResponse> create(
            @Valid @RequestBody InstructorQualificationRequest request) {

        InstructorQualificationResponse response =
                instructorQualificationService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorQualificationResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                instructorQualificationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<InstructorQualificationResponse>> getAll() {

        return ResponseEntity.ok(
                instructorQualificationService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorQualificationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InstructorQualificationRequest request) {

        return ResponseEntity.ok(
                instructorQualificationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        instructorQualificationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
