
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.CurriculumRequest;
import com.thesis.thesis_scheduler.dto.CurriculumResponse;
import com.thesis.thesis_scheduler.service.CurriculumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curricula")
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;

    @PostMapping
    public ResponseEntity<CurriculumResponse> create(
            @Valid @RequestBody CurriculumRequest request) {

        CurriculumResponse response =
                curriculumService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurriculumResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                curriculumService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CurriculumResponse>> getAll() {

        return ResponseEntity.ok(
                curriculumService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurriculumResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CurriculumRequest request) {

        return ResponseEntity.ok(
                curriculumService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        curriculumService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
