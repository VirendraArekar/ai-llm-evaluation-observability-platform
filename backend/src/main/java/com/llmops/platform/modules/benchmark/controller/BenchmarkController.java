package com.llmops.platform.modules.benchmark.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.benchmark.dto.BenchmarkDto.*;
import com.llmops.platform.modules.benchmark.service.BenchmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/benchmarks")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BenchmarkComparisonResponse>>> getComparisons(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(benchmarkService.getComparisons(projectId)));
    }

    @PostMapping("/compare")
    public ResponseEntity<ApiResponse<BenchmarkComparisonResponse>> runBenchmark(
            @RequestParam UUID projectId,
            @RequestBody RunBenchmarkRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Benchmark completed", benchmarkService.runBenchmark(projectId, request)));
    }
}
