
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.RoomAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.RoomAvailabilityResponse;
import com.thesis.thesis_scheduler.service.RoomAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-availabilities")
@RequiredArgsConstructor
public class RoomAvailabilityController {

    private final RoomAvailabilityService service;

    @PostMapping
    public ResponseEntity<RoomAvailabilityResponse> create(
            @Valid @RequestBody RoomAvailabilityRequest request) {

        RoomAvailabilityResponse response = service.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomAvailabilityResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomAvailabilityResponse>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomAvailabilityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomAvailabilityRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
