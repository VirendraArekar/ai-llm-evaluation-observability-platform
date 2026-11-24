package com.llmops.platform.modules.dataset.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.modules.dataset.dto.DatasetDto;
import com.llmops.platform.modules.dataset.dto.DatasetDto.CreateDatasetRequest;
import com.llmops.platform.modules.dataset.dto.DatasetDto.DatasetItemDto;
import com.llmops.platform.modules.dataset.service.DatasetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DatasetDto>>> getDatasets(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(datasetService.getDatasets(projectId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DatasetDto>> getDataset(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(datasetService.getDataset(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DatasetDto>> createDataset(
            @RequestParam UUID projectId,
            @RequestBody CreateDatasetRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Dataset created", datasetService.createDataset(projectId, request)));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<ApiResponse<PagedResponse<DatasetItemDto>>> getItems(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.ok(datasetService.getItems(id, page, size)));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<DatasetDto>> uploadFile(
            @PathVariable UUID id,
            @RequestBody String content,
            @RequestHeader(value = "Content-Type", required = false) String contentType) {
        return ResponseEntity.ok(ApiResponse.ok("File uploaded and dataset version created", datasetService.uploadFile(id, content, contentType)));
    }
}
