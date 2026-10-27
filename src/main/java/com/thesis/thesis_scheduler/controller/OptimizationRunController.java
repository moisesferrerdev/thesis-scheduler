
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.OptimizationRunRequest;
import com.thesis.thesis_scheduler.dto.OptimizationRunResponse;
import com.thesis.thesis_scheduler.service.OptimizationRunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimization-runs")
@RequiredArgsConstructor
public class OptimizationRunController {

    private final OptimizationRunService service;

    @PostMapping
    public ResponseEntity<OptimizationRunResponse> create(
            @Valid @RequestBody OptimizationRunRequest request) {

        OptimizationRunResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptimizationRunResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OptimizationRunResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptimizationRunResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OptimizationRunRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
