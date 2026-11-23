package com.llmops.platform.modules.evaluation.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto;
import com.llmops.platform.modules.evaluation.dto.EvaluationDto.*;
import com.llmops.platform.modules.evaluation.service.EvaluationService;
import com.llmops.platform.modules.evaluation.service.HallucinationDetectorService;
import com.llmops.platform.modules.evaluation.service.LlmJudgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final LlmJudgeService judgeService;
    private final HallucinationDetectorService hallucinationDetector;

    public EvaluationController(EvaluationService evaluationService,
                                LlmJudgeService judgeService,
                                HallucinationDetectorService hallucinationDetector) {
        this.evaluationService = evaluationService;
        this.judgeService = judgeService;
        this.hallucinationDetector = hallucinationDetector;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EvaluationDto>>> getEvaluations(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.getEvaluations(projectId, page, size)));
    }

    @PostMapping("/run-judge")
    public ResponseEntity<ApiResponse<JudgeResultDto>> runJudge(@RequestBody RunJudgeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Evaluation completed", judgeService.evaluate(request)));
    }

    @PostMapping("/detect-hallucination")
    public ResponseEntity<ApiResponse<HallucinationResultDto>> detectHallucination(@RequestBody DetectHallucinationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Hallucination check completed", hallucinationDetector.detect(request)));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<List<MetricDto>>> getMetrics(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.getMetrics(projectId)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<EvaluationStatsDto>> getStats(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(evaluationService.getStats(projectId)));
    }
}
