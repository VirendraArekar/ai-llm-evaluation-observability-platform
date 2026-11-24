package com.llmops.platform.modules.analytics.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.analytics.dto.AnalyticsDto.CostAnalyticsDto;
import com.llmops.platform.modules.analytics.dto.AnalyticsDto.OverviewMetricsDto;
import com.llmops.platform.modules.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<OverviewMetricsDto>> getOverview(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getOverview(projectId)));
    }

    @GetMapping("/costs")
    public ResponseEntity<ApiResponse<CostAnalyticsDto>> getCostAnalytics(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getCostAnalytics(projectId)));
    }
}
