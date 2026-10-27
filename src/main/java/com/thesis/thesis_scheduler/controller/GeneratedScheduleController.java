
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleRequest;
import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;
import com.thesis.thesis_scheduler.service.GeneratedScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generated-schedules")
@RequiredArgsConstructor
public class GeneratedScheduleController {

    private final GeneratedScheduleService service;

    @PostMapping
    public ResponseEntity<GeneratedScheduleResponse> create(
            @Valid @RequestBody GeneratedScheduleRequest request) {

        GeneratedScheduleResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneratedScheduleResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<GeneratedScheduleResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneratedScheduleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GeneratedScheduleRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
