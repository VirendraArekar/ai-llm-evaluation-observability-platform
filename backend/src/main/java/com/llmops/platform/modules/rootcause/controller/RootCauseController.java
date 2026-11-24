package com.llmops.platform.modules.rootcause.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.modules.rootcause.dto.RootCauseDto.*;
import com.llmops.platform.modules.rootcause.service.RootCauseAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intelligence")
public class RootCauseController {

    private final RootCauseAnalysisService rootCauseService;

    public RootCauseController(RootCauseAnalysisService rootCauseService) {
        this.rootCauseService = rootCauseService;
    }

    @PostMapping("/root-cause")
    public ResponseEntity<ApiResponse<RootCauseResultDto>> analyzeRootCause(@RequestBody RootCauseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Root cause analysis completed", rootCauseService.analyze(request)));
    }
}
