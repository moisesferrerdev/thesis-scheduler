package com.thesis.thesis_scheduler.controller;

import com.thesis.thesis_scheduler.dto.SchedulingConfigRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConfigResponse;
import com.thesis.thesis_scheduler.service.SchedulingConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduling-config")
@RequiredArgsConstructor
public class SchedulingConfigController {

    private final SchedulingConfigService schedulingConfigService;

    @GetMapping
    public ResponseEntity<List<SchedulingConfigResponse>>
    getAllConfigurations() {

        return ResponseEntity.ok(
                schedulingConfigService.getAllConfigurations()
        );
    }

    @GetMapping("/term/{termId}")
    public ResponseEntity<SchedulingConfigResponse>
    getConfiguration(@PathVariable Long termId) {

        return ResponseEntity.ok(
                schedulingConfigService.getConfiguration(termId)
        );
    }

    @PutMapping
    public ResponseEntity<SchedulingConfigResponse> saveConfiguration(
            @Valid @RequestBody SchedulingConfigRequest request) {

        return ResponseEntity.ok(
                schedulingConfigService.saveConfiguration(request)
        );
    }
}