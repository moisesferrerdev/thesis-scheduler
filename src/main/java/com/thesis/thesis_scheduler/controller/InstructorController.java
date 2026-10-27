
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.InstructorRequest;
import com.thesis.thesis_scheduler.dto.InstructorResponse;
import com.thesis.thesis_scheduler.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorResponse> create(
            @Valid @RequestBody InstructorRequest request) {

        InstructorResponse response =
                instructorService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                instructorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAll() {

        return ResponseEntity.ok(
                instructorService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InstructorRequest request) {

        return ResponseEntity.ok(
                instructorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        instructorService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
