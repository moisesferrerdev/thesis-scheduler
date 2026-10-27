
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.PerformanceEvaluationRequest;
import com.thesis.thesis_scheduler.dto.PerformanceEvaluationResponse;
import com.thesis.thesis_scheduler.service.PerformanceEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance-evaluations")
@RequiredArgsConstructor
public class PerformanceEvaluationController {

    private final PerformanceEvaluationService service;

    @PostMapping
    public ResponseEntity<PerformanceEvaluationResponse> create(
            @Valid @RequestBody PerformanceEvaluationRequest request) {

        PerformanceEvaluationResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceEvaluationResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PerformanceEvaluationResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceEvaluationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceEvaluationRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
