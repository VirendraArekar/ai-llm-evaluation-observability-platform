package com.llmops.platform.modules.feedback.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.feedback.dto.FeedbackDto;
import com.llmops.platform.modules.feedback.dto.FeedbackDto.CreateFeedbackRequest;
import com.llmops.platform.modules.feedback.dto.FeedbackDto.FeedbackStatsDto;
import com.llmops.platform.modules.feedback.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<FeedbackDto>>> getFeedback(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(feedbackService.getFeedback(projectId, page, size)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackDto>> createFeedback(
            @RequestParam UUID projectId,
            @RequestBody CreateFeedbackRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Feedback submitted", feedbackService.createFeedback(projectId, request, principal)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<FeedbackStatsDto>> getStats(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(feedbackService.getStats(projectId)));
    }

    @PostMapping("/{id}/convert-to-dataset")
    public ResponseEntity<ApiResponse<Void>> convertToDataset(
            @PathVariable UUID id,
            @RequestParam UUID datasetId) {
        feedbackService.convertToDatasetItem(id, datasetId);
        return ResponseEntity.ok(ApiResponse.ok("Feedback converted to dataset item", null));
    }
}
