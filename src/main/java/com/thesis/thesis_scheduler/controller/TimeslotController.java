
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.TimeslotRequest;
import com.thesis.thesis_scheduler.dto.TimeslotResponse;
import com.thesis.thesis_scheduler.service.TimeslotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
public class TimeslotController {

    private final TimeslotService timeslotService;

    @PostMapping
    public ResponseEntity<TimeslotResponse> create(
            @Valid @RequestBody TimeslotRequest request) {

        TimeslotResponse response = timeslotService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeslotResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(timeslotService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TimeslotResponse>> getAll() {

        return ResponseEntity.ok(timeslotService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeslotResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TimeslotRequest request) {

        return ResponseEntity.ok(
                timeslotService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        timeslotService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
