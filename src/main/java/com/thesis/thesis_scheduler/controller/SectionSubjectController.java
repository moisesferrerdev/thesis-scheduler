
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.SectionSubjectRequest;
import com.thesis.thesis_scheduler.dto.SectionSubjectResponse;
import com.thesis.thesis_scheduler.service.SectionSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/section-subjects")
@RequiredArgsConstructor
public class SectionSubjectController {

    private final SectionSubjectService sectionSubjectService;

    @PostMapping
    public ResponseEntity<SectionSubjectResponse> create(
            @Valid @RequestBody SectionSubjectRequest request) {

        SectionSubjectResponse response =
                sectionSubjectService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionSubjectResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                sectionSubjectService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SectionSubjectResponse>> getAll() {

        return ResponseEntity.ok(
                sectionSubjectService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionSubjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SectionSubjectRequest request) {

        return ResponseEntity.ok(
                sectionSubjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        sectionSubjectService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
