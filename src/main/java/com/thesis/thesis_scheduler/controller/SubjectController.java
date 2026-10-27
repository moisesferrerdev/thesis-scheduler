
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.SubjectRequest;
import com.thesis.thesis_scheduler.dto.SubjectResponse;
import com.thesis.thesis_scheduler.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> create(
            @Valid @RequestBody SubjectRequest request) {

        SubjectResponse response = subjectService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subjectService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAll() {

        return ResponseEntity.ok(
                subjectService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {

        return ResponseEntity.ok(
                subjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        subjectService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
