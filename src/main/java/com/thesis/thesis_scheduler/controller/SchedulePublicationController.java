package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.SchedulePublicationEventResponse;
import com.thesis.thesis_scheduler.dto.SchedulePublicationRequest;
import com.thesis.thesis_scheduler.dto.ScheduleWithdrawalRequest;
import com.thesis.thesis_scheduler.service.SchedulePublicationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/official-schedules/{officialScheduleId}")
@RequiredArgsConstructor
public class SchedulePublicationController {

    private final SchedulePublicationService service;

    @PostMapping("/publish")
    public ResponseEntity<SchedulePublicationEventResponse> publish(
            @PathVariable Long officialScheduleId,
            @Valid @RequestBody SchedulePublicationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.publish(officialScheduleId, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<SchedulePublicationEventResponse> withdraw(
            @PathVariable Long officialScheduleId,
            @Valid @RequestBody ScheduleWithdrawalRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.withdraw(officialScheduleId, request));
    }

    @GetMapping("/publication-history")
    public ResponseEntity<List<SchedulePublicationEventResponse>>
    getPublicationHistory(
            @PathVariable Long officialScheduleId) {

        return ResponseEntity.ok(
                service.getPublicationHistory(officialScheduleId)
        );
    }
}