
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.InstructorAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.InstructorAvailabilityResponse;
import com.thesis.thesis_scheduler.service.InstructorAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor-availabilities")
@RequiredArgsConstructor
public class InstructorAvailabilityController {

    private final InstructorAvailabilityService service;

    @PostMapping
    public ResponseEntity<InstructorAvailabilityResponse> create(
            @Valid @RequestBody InstructorAvailabilityRequest request) {

        InstructorAvailabilityResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorAvailabilityResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<InstructorAvailabilityResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorAvailabilityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InstructorAvailabilityRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
