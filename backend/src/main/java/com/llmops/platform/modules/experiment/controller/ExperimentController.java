package com.llmops.platform.modules.experiment.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.experiment.dto.ExperimentDto;
import com.llmops.platform.modules.experiment.dto.ExperimentDto.CreateExperimentRequest;
import com.llmops.platform.modules.experiment.dto.ExperimentDto.ExperimentComparisonDto;
import com.llmops.platform.modules.experiment.dto.ExperimentDto.ExperimentRunDto;
import com.llmops.platform.modules.experiment.service.ExperimentExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experiments")
public class ExperimentController {

    private final ExperimentExecutionService experimentService;

    public ExperimentController(ExperimentExecutionService experimentService) {
        this.experimentService = experimentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperimentDto>>> getExperiments(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(experimentService.getExperiments(projectId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperimentDto>> getExperiment(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(experimentService.getExperiment(id)));
    }

    @GetMapping("/{id}/runs")
    public ResponseEntity<ApiResponse<List<ExperimentRunDto>>> getRuns(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(experimentService.getRuns(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExperimentDto>> createExperiment(
            @RequestParam UUID projectId,
            @RequestBody CreateExperimentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Experiment created and execution started", experimentService.createAndStartExperiment(projectId, request)));
    }

    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<ExperimentComparisonDto>> compareExperiments(
            @RequestParam UUID baselineId,
            @RequestParam UUID candidateId) {
        return ResponseEntity.ok(ApiResponse.ok(experimentService.compare(baselineId, candidateId)));
    }
}
