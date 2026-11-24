package com.llmops.platform.modules.recommendation.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.recommendation.dto.RecommendationDto;
import com.llmops.platform.modules.recommendation.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendationDto>>> getRecommendations(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(recommendationService.getRecommendations(projectId)));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<RecommendationDto>>> generateRecommendations(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok("Recommendations generated", recommendationService.generateRecommendations(projectId)));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<ApiResponse<RecommendationDto>> applyRecommendation(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Recommendation applied", recommendationService.applyRecommendation(id)));
    }
}
