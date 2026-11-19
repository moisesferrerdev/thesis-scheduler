package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.RoomClassificationRequest;
import com.thesis.thesis_scheduler.dto.RoomClassificationResponse;
import com.thesis.thesis_scheduler.service.RoomClassificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-classifications")
@RequiredArgsConstructor
public class RoomClassificationController {

    private final RoomClassificationService service;

    @PostMapping
    public ResponseEntity<RoomClassificationResponse> create(
            @Valid @RequestBody RoomClassificationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomClassificationResponse>> getAll(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        return ResponseEntity.ok(service.getAll(activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomClassificationResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomClassificationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomClassificationRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {

        service.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}