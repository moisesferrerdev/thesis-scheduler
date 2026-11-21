package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.OfficialScheduleRequest;
import com.thesis.thesis_scheduler.dto.OfficialScheduleResponse;
import com.thesis.thesis_scheduler.service.OfficialScheduleService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/official-schedules")
@RequiredArgsConstructor
public class OfficialScheduleController {

    private final OfficialScheduleService service;

    @PostMapping
    public ResponseEntity<OfficialScheduleResponse> createFromCandidate(
            @Valid @RequestBody OfficialScheduleRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.createFromCandidate(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfficialScheduleResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/term/{termId}")
    public ResponseEntity<OfficialScheduleResponse> getByTermId(
            @PathVariable Long termId) {

        return ResponseEntity.ok(service.getByTermId(termId));
    }

    @GetMapping
    public ResponseEntity<List<OfficialScheduleResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}