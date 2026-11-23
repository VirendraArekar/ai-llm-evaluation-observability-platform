package com.llmops.platform.modules.provider.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.provider.dto.ProviderDto;
import com.llmops.platform.modules.provider.dto.ProviderDto.CreateProviderRequest;
import com.llmops.platform.modules.provider.dto.ProviderDto.ModelDto;
import com.llmops.platform.modules.provider.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<List<ProviderDto>>> getProviders(@RequestParam(required = false) UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(providerService.getProvidersByProject(projectId)));
    }

    @PostMapping("/providers")
    public ResponseEntity<ApiResponse<ProviderDto>> createProvider(
            @RequestParam UUID projectId,
            @RequestBody CreateProviderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Provider created successfully", providerService.createProvider(projectId, request)));
    }

    @PostMapping("/providers/{id}/test")
    public ResponseEntity<ApiResponse<Boolean>> testProvider(@PathVariable UUID id) {
        boolean ok = providerService.testProvider(id);
        return ResponseEntity.ok(ApiResponse.ok("Provider health check: " + (ok ? "HEALTHY" : "UNREACHABLE"), ok));
    }

    @GetMapping("/models")
    public ResponseEntity<ApiResponse<List<ModelDto>>> getModels(@RequestParam(required = false) UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(providerService.getModelsByProject(projectId)));
    }
}
