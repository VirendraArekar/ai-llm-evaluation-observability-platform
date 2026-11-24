package com.llmops.platform.modules.assistant.controller;

import com.llmops.platform.common.dto.ApiResponse;
import com.llmops.platform.config.security.UserPrincipal;
import com.llmops.platform.modules.assistant.dto.AssistantDto.*;
import com.llmops.platform.modules.assistant.service.AssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationDto>>> getConversations(@RequestParam UUID projectId) {
        return ResponseEntity.ok(ApiResponse.ok(assistantService.getConversations(projectId)));
    }

    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ConversationDto>> createConversation(
            @RequestParam UUID projectId,
            @RequestBody(required = false) CreateConversationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String title = request != null ? request.getTitle() : "Observability Exploration";
        UUID userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(ApiResponse.ok(assistantService.createConversation(projectId, title, userId)));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessages(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(assistantService.getMessages(id)));
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(
            @PathVariable UUID id,
            @RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(assistantService.chat(id, request)));
    }
}
