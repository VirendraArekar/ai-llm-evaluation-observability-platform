package com.llmops.platform.modules.dataset.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmops.platform.common.dto.PagedResponse;
import com.llmops.platform.common.exception.ResourceNotFoundException;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.repository.ProjectRepository;
import com.llmops.platform.modules.dataset.dto.DatasetDto;
import com.llmops.platform.modules.dataset.dto.DatasetDto.CreateDatasetItemRequest;
import com.llmops.platform.modules.dataset.dto.DatasetDto.CreateDatasetRequest;
import com.llmops.platform.modules.dataset.dto.DatasetDto.DatasetItemDto;
import com.llmops.platform.modules.dataset.entity.Dataset;
import com.llmops.platform.modules.dataset.entity.DatasetItem;
import com.llmops.platform.modules.dataset.entity.DatasetVersion;
import com.llmops.platform.modules.dataset.repository.DatasetItemRepository;
import com.llmops.platform.modules.dataset.repository.DatasetRepository;
import com.llmops.platform.modules.dataset.repository.DatasetVersionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final DatasetVersionRepository versionRepository;
    private final DatasetItemRepository itemRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DatasetService(DatasetRepository datasetRepository,
                          DatasetVersionRepository versionRepository,
                          DatasetItemRepository itemRepository,
                          ProjectRepository projectRepository) {
        this.datasetRepository = datasetRepository;
        this.versionRepository = versionRepository;
        this.itemRepository = itemRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<DatasetDto> getDatasets(UUID projectId) {
        List<Dataset> list = datasetRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        if (list.isEmpty()) {
            list = datasetRepository.findAll();
        }
        return list.stream()
                .map(this::mapToDatasetDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DatasetDto getDataset(UUID datasetId) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new ResourceNotFoundException("Dataset not found: " + datasetId));
        return mapToDatasetDto(dataset);
    }

    @Transactional
    public DatasetDto createDataset(UUID projectId, CreateDatasetRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Dataset dataset = new Dataset();
        dataset.setProject(project);
        dataset.setName(request.getName());
        dataset.setDescription(request.getDescription());
        dataset.setTags(request.getTags());
        dataset.setCurrentVersion(1);
        dataset = datasetRepository.save(dataset);

        DatasetVersion version = new DatasetVersion();
        version.setDataset(dataset);
        version.setVersionNumber(1);
        version.setItemsCount(request.getItems() != null ? request.getItems().size() : 0);
        version = versionRepository.save(version);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<DatasetItem> items = new ArrayList<>();
            for (CreateDatasetItemRequest iReq : request.getItems()) {
                DatasetItem item = new DatasetItem();
                item.setDatasetVersion(version);
                item.setInputVariables(iReq.getInputVariables());
                item.setExpectedOutput(iReq.getExpectedOutput());
                item.setExpectedContext(iReq.getExpectedContext());
                item.setMetadataJson(iReq.getMetadataJson());
                items.add(item);
            }
            itemRepository.saveAll(items);
        }

        return mapToDatasetDto(dataset);
    }

    @Transactional(readOnly = true)
    public PagedResponse<DatasetItemDto> getItems(UUID datasetId, int page, int size) {
        DatasetVersion version = versionRepository.findByDatasetIdOrderByVersionNumberDesc(datasetId).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No version found for dataset: " + datasetId));

        Page<DatasetItem> pageResult = itemRepository.findByDatasetVersionId(version.getId(), PageRequest.of(page, size));
        List<DatasetItemDto> dtos = pageResult.getContent().stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                dtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    @Transactional
    public DatasetDto uploadFile(UUID datasetId, String rawContent, String contentType) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new ResourceNotFoundException("Dataset not found: " + datasetId));

        List<CreateDatasetItemRequest> items = parseRawContent(rawContent, contentType);

        int nextVer = dataset.getCurrentVersion() + 1;
        dataset.setCurrentVersion(nextVer);
        datasetRepository.save(dataset);

        DatasetVersion version = new DatasetVersion();
        version.setDataset(dataset);
        version.setVersionNumber(nextVer);
        version.setItemsCount(items.size());
        version = versionRepository.save(version);

        List<DatasetItem> entities = new ArrayList<>();
        for (CreateDatasetItemRequest req : items) {
            DatasetItem item = new DatasetItem();
            item.setDatasetVersion(version);
            item.setInputVariables(req.getInputVariables());
            item.setExpectedOutput(req.getExpectedOutput());
            item.setExpectedContext(req.getExpectedContext());
            entities.add(item);
        }
        itemRepository.saveAll(entities);

        return mapToDatasetDto(dataset);
    }

    private List<CreateDatasetItemRequest> parseRawContent(String content, String contentType) {
        List<CreateDatasetItemRequest> list = new ArrayList<>();
        if (content == null || content.isBlank()) return list;

        try {
            if (contentType != null && contentType.contains("json") || content.trim().startsWith("[") || content.trim().startsWith("{")) {
                if (content.trim().startsWith("[")) {
                    List<Map<String, Object>> array = objectMapper.readValue(content, new TypeReference<>() {});
                    for (Map<String, Object> map : array) {
                        String expOut = map.containsKey("expected_output") ? String.valueOf(map.get("expected_output")) : null;
                        String expCtx = map.containsKey("expected_context") ? String.valueOf(map.get("expected_context")) : null;
                        map.remove("expected_output");
                        map.remove("expected_context");
                        list.add(new CreateDatasetItemRequest(objectMapper.writeValueAsString(map), expOut, expCtx));
                    }
                }
            } else {
                // CSV parsing
                String[] lines = content.split("\r?\n");
                if (lines.length > 1) {
                    String[] headers = lines[0].split(",");
                    for (int i = 1; i < lines.length; i++) {
                        String line = lines[i].trim();
                        if (line.isEmpty()) continue;
                        String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        Map<String, String> row = new HashMap<>();
                        String expectedOutput = null;
                        String expectedContext = null;

                        for (int j = 0; j < headers.length && j < values.length; j++) {
                            String h = headers[j].trim().replace("\"", "");
                            String v = values[j].trim().replace("\"", "");
                            if ("expected_output".equalsIgnoreCase(h) || "ground_truth".equalsIgnoreCase(h)) {
                                expectedOutput = v;
                            } else if ("expected_context".equalsIgnoreCase(h) || "context".equalsIgnoreCase(h)) {
                                expectedContext = v;
                            } else {
                                row.put(h, v);
                            }
                        }
                        list.add(new CreateDatasetItemRequest(objectMapper.writeValueAsString(row), expectedOutput, expectedContext));
                    }
                }
            }
        } catch (Exception e) {
            // Fallback raw row
            list.add(new CreateDatasetItemRequest(Map.of("query", content.substring(0, Math.min(content.length(), 100))).toString(), null, null));
        }
        return list;
    }

    private DatasetDto mapToDatasetDto(Dataset d) {
        DatasetDto dto = new DatasetDto();
        dto.setId(d.getId());
        dto.setProjectId(d.getProject().getId());
        dto.setName(d.getName());
        dto.setDescription(d.getDescription());
        dto.setTags(d.getTags());
        dto.setCurrentVersion(d.getCurrentVersion());
        dto.setCreatedAt(d.getCreatedAt());

        versionRepository.findByDatasetIdAndVersionNumber(d.getId(), d.getCurrentVersion()).ifPresent(v -> {
            dto.setTotalItems(v.getItemsCount());
        });
        return dto;
    }

    private DatasetItemDto mapToItemDto(DatasetItem i) {
        DatasetItemDto dto = new DatasetItemDto();
        dto.setId(i.getId());
        dto.setDatasetVersionId(i.getDatasetVersion().getId());
        dto.setInputVariables(i.getInputVariables());
        dto.setExpectedOutput(i.getExpectedOutput());
        dto.setExpectedContext(i.getExpectedContext());
        dto.setMetadataJson(i.getMetadataJson());
        dto.setCreatedAt(i.getCreatedAt());
        return dto;
    }
}
