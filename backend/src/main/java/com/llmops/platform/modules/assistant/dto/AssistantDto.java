package com.llmops.platform.modules.assistant.dto;

import java.time.Instant;
import java.util.UUID;

public class AssistantDto {

    public static class ConversationDto {
        private UUID id;
        private UUID projectId;
        private String title;
        private Instant createdAt;
        private Instant updatedAt;

        public ConversationDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getProjectId() { return projectId; }
        public void setProjectId(UUID projectId) { this.projectId = projectId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class CreateConversationRequest {
        private String title;

        public CreateConversationRequest() {}
        public CreateConversationRequest(String title) { this.title = title; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public static class ChatMessageRequest {
        private String content;

        public ChatMessageRequest() {}
        public ChatMessageRequest(String content) { this.content = content; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class MessageDto {
        private UUID id;
        private UUID conversationId;
        private String senderRole;
        private String content;
        private String toolCallsJson;
        private String toolResultsJson;
        private Instant createdAt;

        public MessageDto() {}

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getConversationId() { return conversationId; }
        public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
        public String getSenderRole() { return senderRole; }
        public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getToolCallsJson() { return toolCallsJson; }
        public void setToolCallsJson(String toolCallsJson) { this.toolCallsJson = toolCallsJson; }
        public String getToolResultsJson() { return toolResultsJson; }
        public void setToolResultsJson(String toolResultsJson) { this.toolResultsJson = toolResultsJson; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }
}
