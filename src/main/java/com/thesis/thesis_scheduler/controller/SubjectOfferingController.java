
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.SubjectOfferingRequest;
import com.thesis.thesis_scheduler.dto.SubjectOfferingResponse;
import com.thesis.thesis_scheduler.service.SubjectOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subject-offerings")
@RequiredArgsConstructor
public class SubjectOfferingController {

    private final SubjectOfferingService subjectOfferingService;

    @PostMapping
    public ResponseEntity<SubjectOfferingResponse> create(
            @Valid @RequestBody SubjectOfferingRequest request) {

        SubjectOfferingResponse response =
                subjectOfferingService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectOfferingResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subjectOfferingService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubjectOfferingResponse>> getAll() {

        return ResponseEntity.ok(
                subjectOfferingService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectOfferingResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectOfferingRequest request) {

        return ResponseEntity.ok(
                subjectOfferingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        subjectOfferingService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
