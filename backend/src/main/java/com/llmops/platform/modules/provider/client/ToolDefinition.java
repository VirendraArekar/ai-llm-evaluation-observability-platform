package com.llmops.platform.modules.provider.client;

import java.util.Map;

public class ToolDefinition {
    private String type = "function";
    private FunctionDefinition function;

    public ToolDefinition() {}

    public ToolDefinition(String name, String description, Map<String, Object> parameters) {
        this.function = new FunctionDefinition(name, description, parameters);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public FunctionDefinition getFunction() { return function; }
    public void setFunction(FunctionDefinition function) { this.function = function; }

    public static class FunctionDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;

        public FunctionDefinition() {}
        public FunctionDefinition(String name, String description, Map<String, Object> parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
}
