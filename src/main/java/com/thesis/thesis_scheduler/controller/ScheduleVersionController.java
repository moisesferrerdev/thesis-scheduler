
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.ScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionResponse;
import com.thesis.thesis_scheduler.service.ScheduleVersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-versions")
@RequiredArgsConstructor
public class ScheduleVersionController {

    private final ScheduleVersionService service;

    @PostMapping
    public ResponseEntity<ScheduleVersionResponse> create(
            @Valid @RequestBody ScheduleVersionRequest request) {

        ScheduleVersionResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleVersionResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleVersionResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}
