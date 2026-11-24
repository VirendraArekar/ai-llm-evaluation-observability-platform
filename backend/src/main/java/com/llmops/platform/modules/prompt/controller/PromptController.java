package com.llmops.platform.modules.prompt.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.prompt.dto.PromptDto;
import com.llmops.platform.modules.prompt.dto.PromptDto.*;
import com.llmops.platform.modules.prompt.service.PromptOptimizerService;
import com.llmops.platform.modules.prompt.service.PromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prompts")
public class PromptController {

    private final PromptService promptService;
    private final PromptOptimizerService optimizerService;

    public PromptController(PromptService promptService, PromptOptimizerService optimizerService) {
        this.promptService = promptService;
        this.optimizerService = optimizerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromptDto>>> getPrompts(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getPromptsByProject(projectId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromptDto>> getPrompt(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getPrompt(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromptDto>> createPrompt(
            @RequestParam UUID projectId,
            @RequestBody CreatePromptRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Prompt created", promptService.createPrompt(projectId, request, principal)));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<PromptVersionDto>>> getVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(promptService.getVersions(id)));
    }

    @PostMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<PromptVersionDto>> createVersion(
            @PathVariable UUID id,
            @RequestBody CreateVersionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Version created", promptService.createVersion(id, request, principal)));
    }

    @PostMapping("/{id}/versions/{versionNumber}/publish")
    public ResponseEntity<ApiResponse<Void>> publishVersion(@PathVariable UUID id, @PathVariable int versionNumber) {
        promptService.publishVersion(id, versionNumber);
        return ResponseEntity.ok(ApiResponse.ok("Version " + versionNumber + " published", null));
    }

    @PostMapping("/{id}/versions/{versionNumber}/rollback")
    public ResponseEntity<ApiResponse<PromptVersionDto>> rollback(
            @PathVariable UUID id,
            @PathVariable int versionNumber,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Rolled back to version " + versionNumber, promptService.rollbackToVersion(id, versionNumber, principal)));
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<ApiResponse<PromptExecutionResponse>> testPrompt(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer version,
            @RequestBody PromptExecutionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Prompt executed", promptService.executePrompt(id, version, request)));
    }

    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<PromptOptimizationResult>> optimizePrompt(@RequestBody OptimizePromptRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Prompt analyzed and optimized", optimizerService.optimize(request)));
    }
}
