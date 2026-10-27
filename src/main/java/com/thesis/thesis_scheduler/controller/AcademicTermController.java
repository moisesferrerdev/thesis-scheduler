
package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.AcademicTermRequest;
import com.thesis.thesis_scheduler.dto.AcademicTermResponse;
import com.thesis.thesis_scheduler.service.AcademicTermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-terms")
@RequiredArgsConstructor
public class AcademicTermController {

    private final AcademicTermService academicTermService;

    @PostMapping
    public ResponseEntity<AcademicTermResponse> create(
            @Valid @RequestBody AcademicTermRequest request) {

        AcademicTermResponse response =
                academicTermService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicTermResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                academicTermService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AcademicTermResponse>> getAll() {

        return ResponseEntity.ok(
                academicTermService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicTermResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicTermRequest request) {

        return ResponseEntity.ok(
                academicTermService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        academicTermService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
