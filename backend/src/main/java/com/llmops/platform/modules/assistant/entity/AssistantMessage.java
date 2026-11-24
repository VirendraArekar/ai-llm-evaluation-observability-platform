package com.llmops.platform.modules.assistant.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "assistant_messages")
public class AssistantMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AssistantConversation conversation;

    @Column(name = "sender_role", length = 50, nullable = false)
    private String senderRole; // USER, ASSISTANT, TOOL

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "tool_calls_json", columnDefinition = "TEXT")
    private String toolCallsJson;

    @Column(name = "tool_results_json", columnDefinition = "TEXT")
    private String toolResultsJson;

    public AssistantMessage() {}

    public AssistantConversation getConversation() { return conversation; }
    public void setConversation(AssistantConversation conversation) { this.conversation = conversation; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getToolCallsJson() { return toolCallsJson; }
    public void setToolCallsJson(String toolCallsJson) { this.toolCallsJson = toolCallsJson; }
    public String getToolResultsJson() { return toolResultsJson; }
    public void setToolResultsJson(String toolResultsJson) { this.toolResultsJson = toolResultsJson; }
}
