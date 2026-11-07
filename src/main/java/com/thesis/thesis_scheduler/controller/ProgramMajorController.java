
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.ProgramMajorRequest;
import com.thesis.thesis_scheduler.dto.ProgramMajorResponse;
import com.thesis.thesis_scheduler.service.ProgramMajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/program-majors")
@RequiredArgsConstructor
public class ProgramMajorController {

    private final ProgramMajorService programMajorService;

    @PostMapping
    public ResponseEntity<ProgramMajorResponse> create(
            @Valid @RequestBody ProgramMajorRequest request) {

        ProgramMajorResponse response = programMajorService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramMajorResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                programMajorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProgramMajorResponse>> getAll() {

        return ResponseEntity.ok(
                programMajorService.getAll());
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<List<ProgramMajorResponse>> getByProgramId(
            @PathVariable Long programId) {

        return ResponseEntity.ok(
                programMajorService.getByProgramId(programId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramMajorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgramMajorRequest request) {

        return ResponseEntity.ok(
                programMajorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        programMajorService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
