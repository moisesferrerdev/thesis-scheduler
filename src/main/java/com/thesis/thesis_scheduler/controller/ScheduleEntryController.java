
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.ScheduleEntryRequest;
import com.thesis.thesis_scheduler.dto.ScheduleEntryResponse;
import com.thesis.thesis_scheduler.service.ScheduleEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-entries")
@RequiredArgsConstructor
public class ScheduleEntryController {

    private final ScheduleEntryService service;

    @PostMapping
    public ResponseEntity<ScheduleEntryResponse> create(
            @Valid @RequestBody ScheduleEntryRequest request) {

        ScheduleEntryResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleEntryResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleEntryResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}
