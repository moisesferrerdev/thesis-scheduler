
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.SchedulingConstraintRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConstraintResponse;
import com.thesis.thesis_scheduler.service.SchedulingConstraintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduling-constraints")
@RequiredArgsConstructor
public class SchedulingConstraintController {

    private final SchedulingConstraintService service;

    @PostMapping
    public ResponseEntity<SchedulingConstraintResponse> create(
            @Valid @RequestBody SchedulingConstraintRequest request) {

        SchedulingConstraintResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchedulingConstraintResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SchedulingConstraintResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchedulingConstraintResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SchedulingConstraintRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
