
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.StudentSectionRequest;
import com.thesis.thesis_scheduler.dto.StudentSectionResponse;
import com.thesis.thesis_scheduler.service.StudentSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-sections")
@RequiredArgsConstructor
public class StudentSectionController {

    private final StudentSectionService studentSectionService;

    @PostMapping
    public ResponseEntity<StudentSectionResponse> create(
            @Valid @RequestBody StudentSectionRequest request) {

        StudentSectionResponse response =
                studentSectionService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentSectionResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                studentSectionService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<StudentSectionResponse>> getAll() {

        return ResponseEntity.ok(
                studentSectionService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentSectionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentSectionRequest request) {

        return ResponseEntity.ok(
                studentSectionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        studentSectionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
