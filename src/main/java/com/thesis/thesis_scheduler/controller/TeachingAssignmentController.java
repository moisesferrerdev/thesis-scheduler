
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.TeachingAssignmentRequest;
import com.thesis.thesis_scheduler.dto.TeachingAssignmentResponse;
import com.thesis.thesis_scheduler.service.TeachingAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching-assignments")
@RequiredArgsConstructor
public class TeachingAssignmentController {

    private final TeachingAssignmentService service;

    @PostMapping
    public ResponseEntity<TeachingAssignmentResponse> create(
            @Valid @RequestBody TeachingAssignmentRequest request) {

        TeachingAssignmentResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeachingAssignmentResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeachingAssignmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TeachingAssignmentRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
