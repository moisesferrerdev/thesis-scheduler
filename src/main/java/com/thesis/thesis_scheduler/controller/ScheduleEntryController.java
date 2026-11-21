package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.ScheduleEntryResponse;
import com.thesis.thesis_scheduler.service.ScheduleEntryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-entries")
@RequiredArgsConstructor
public class ScheduleEntryController {

    private final ScheduleEntryService service;

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleEntryResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleEntryResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/schedule-version/{scheduleVersionId}")
    public ResponseEntity<List<ScheduleEntryResponse>>
    getByScheduleVersionId(
            @PathVariable Long scheduleVersionId) {

        return ResponseEntity.ok(
                service.getByScheduleVersionId(scheduleVersionId)
        );
    }
}