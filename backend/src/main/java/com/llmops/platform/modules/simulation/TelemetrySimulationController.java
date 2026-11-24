package com.llmops.platform.modules.simulation;

import com.llmops.platform.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetrySimulationController {

    private final TelemetrySimulationService simulationService;

    public TelemetrySimulationController(TelemetrySimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<ApiResponse<Object>> simulateLiveTelemetry(@RequestParam(required = false) UUID projectId) {
        Object trace = simulationService.generateLiveTelemetry(projectId);
        return ResponseEntity.ok(ApiResponse.ok("Live telemetry trace successfully generated and ingested", trace));
    }

    @PostMapping("/seed-all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> forceSeedAll() {
        Map<String, Object> result = simulationService.forceSeedAll();
        return ResponseEntity.ok(ApiResponse.ok("Platform demonstration data verification complete", result));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> checkHealth() {
        return ResponseEntity.ok(ApiResponse.ok("Telemetry engine online"));
    }
}
