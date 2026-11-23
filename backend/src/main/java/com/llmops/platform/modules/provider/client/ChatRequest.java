package com.llmops.platform.modules.provider.client;

import java.util.List;

public class ChatRequest {
    private String model;
    private List<ChatMessage> messages;
    private Double temperature = 0.7;
    private Integer maxTokens = 2048;
    private boolean stream = false;
    private String responseFormat; // "text" or "json_object"
    private List<ToolDefinition> tools;

    public ChatRequest() {}

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public boolean isStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }
    public String getResponseFormat() { return responseFormat; }
    public void setResponseFormat(String responseFormat) { this.responseFormat = responseFormat; }
    public List<ToolDefinition> getTools() { return tools; }
    public void setTools(List<ToolDefinition> tools) { this.tools = tools; }
}
