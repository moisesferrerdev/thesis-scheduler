package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;
import com.thesis.thesis_scheduler.service.GeneratedScheduleService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generated-schedules")
@RequiredArgsConstructor
public class GeneratedScheduleController {

    private final GeneratedScheduleService service;

    @GetMapping("/{id}")
    public ResponseEntity<GeneratedScheduleResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<GeneratedScheduleResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }
}