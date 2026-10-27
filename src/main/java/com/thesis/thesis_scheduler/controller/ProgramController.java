
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.ProgramRequest;
import com.thesis.thesis_scheduler.dto.ProgramResponse;
import com.thesis.thesis_scheduler.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<ProgramResponse> create(
            @Valid @RequestBody ProgramRequest request) {

        ProgramResponse response = programService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                programService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProgramResponse>> getAll() {

        return ResponseEntity.ok(
                programService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgramRequest request) {

        return ResponseEntity.ok(
                programService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        programService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
