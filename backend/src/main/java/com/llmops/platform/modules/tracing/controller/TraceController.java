package com.llmops.platform.modules.tracing.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.modules.auth.entity.ApiKey;
import com.llmops.platform.modules.tracing.dto.TraceDto;
import com.llmops.platform.modules.tracing.dto.TraceDto.TraceDetailDto;
import com.llmops.platform.modules.tracing.dto.TraceDto.TraceIngestRequest;
import com.llmops.platform.modules.tracing.dto.TraceDto.TraceStatsDto;
import com.llmops.platform.modules.tracing.service.TraceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/traces")
public class TraceController {

    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<TraceDto>> ingestTrace(
            @RequestBody TraceIngestRequest request,
            Authentication authentication) {
        UUID authenticatedProjectId = null;
        if (authentication != null && authentication.getPrincipal() instanceof ApiKey apiKey) {
            authenticatedProjectId = apiKey.getProject().getId();
        }
        TraceDto result = traceService.ingest(request, authenticatedProjectId);
        return ResponseEntity.ok(ApiResponse.ok("Trace ingested successfully", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TraceDto>>> getTraces(
            @RequestParam UUID projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(traceService.getTraces(projectId, status, query, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TraceDetailDto>> getTraceDetail(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(traceService.getTraceDetail(id)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TraceStatsDto>> getStats(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(traceService.getStats(projectId)));
    }
}
