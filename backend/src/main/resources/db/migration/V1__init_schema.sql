-- ================================================================
-- AetherLLM Platform - V1 Schema Migration
-- ================================================================

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    role VARCHAR(50) NOT NULL DEFAULT 'DEVELOPER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    plan VARCHAR(50) NOT NULL DEFAULT 'ENTERPRISE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS organization_members (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL DEFAULT 'DEVELOPER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_org_user UNIQUE (organization_id, user_id)
);

CREATE TABLE IF NOT EXISTS workspaces (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_org_workspace_slug UNIQUE (organization_id, slug)
);

CREATE TABLE IF NOT EXISTS workspace_members (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL DEFAULT 'DEVELOPER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_workspace_user UNIQUE (workspace_id, user_id)
);

CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_workspace_project_slug UNIQUE (workspace_id, slug)
);

CREATE TABLE IF NOT EXISTS api_keys (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    key_prefix VARCHAR(32) NOT NULL,
    key_hash VARCHAR(255) NOT NULL UNIQUE,
    scopes VARCHAR(500) NOT NULL DEFAULT 'traces:write,evaluations:read',
    last_used_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    organization_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(255),
    details_json TEXT,
    ip_address VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS llm_providers (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    provider_type VARCHAR(50) NOT NULL, -- OPENAI, GEMINI, CLAUDE, OLLAMA, CUSTOM_OPENAI
    name VARCHAR(255) NOT NULL,
    base_url VARCHAR(500),
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    config_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS provider_credentials (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES llm_providers(id) ON DELETE CASCADE,
    encrypted_api_key TEXT NOT NULL,
    key_masked VARCHAR(50) NOT NULL,
    iv_salt VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS models (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES llm_providers(id) ON DELETE CASCADE,
    model_identifier VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    context_window INT NOT NULL DEFAULT 128000,
    max_output_tokens INT NOT NULL DEFAULT 4096,
    input_cost_per_million NUMERIC(10, 4) NOT NULL DEFAULT 0.0,
    output_cost_per_million NUMERIC(10, 4) NOT NULL DEFAULT 0.0,
    supports_streaming BOOLEAN NOT NULL DEFAULT TRUE,
    supports_tools BOOLEAN NOT NULL DEFAULT TRUE,
    supports_vision BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prompts (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    description TEXT,
    tags VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_project_prompt_slug UNIQUE (project_id, slug)
);

CREATE TABLE IF NOT EXISTS prompt_versions (
    id UUID PRIMARY KEY,
    prompt_id UUID NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    version_number INT NOT NULL,
    template TEXT NOT NULL,
    system_message TEXT,
    variables_schema TEXT,
    model_id UUID REFERENCES models(id) ON DELETE SET NULL,
    temperature NUMERIC(3, 2) DEFAULT 0.7,
    max_tokens INT DEFAULT 2048,
    change_summary VARCHAR(500),
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prompt_version UNIQUE (prompt_id, version_number)
);

CREATE TABLE IF NOT EXISTS traces (
    id VARCHAR(64) PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    session_id VARCHAR(100),
    user_id VARCHAR(100),
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS', -- SUCCESS, ERROR, RUNNING
    latency_ms BIGINT NOT NULL DEFAULT 0,
    total_tokens INT NOT NULL DEFAULT 0,
    input_tokens INT NOT NULL DEFAULT 0,
    output_tokens INT NOT NULL DEFAULT 0,
    estimated_cost NUMERIC(12, 6) NOT NULL DEFAULT 0.000000,
    tags VARCHAR(500),
    metadata_json TEXT,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS spans (
    id VARCHAR(64) PRIMARY KEY,
    trace_id VARCHAR(64) NOT NULL REFERENCES traces(id) ON DELETE CASCADE,
    parent_span_id VARCHAR(64),
    name VARCHAR(255) NOT NULL,
    span_type VARCHAR(50) NOT NULL, -- LLM, TOOL, RETRIEVAL, AGENT, CHAIN
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS',
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    latency_ms BIGINT NOT NULL DEFAULT 0,
    input_json TEXT,
    output_json TEXT,
    model_name VARCHAR(100),
    provider_name VARCHAR(100),
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    cost NUMERIC(12, 6) DEFAULT 0.000000,
    error_message TEXT,
    stack_trace TEXT,
    attributes_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS datasets (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tags VARCHAR(500),
    current_version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dataset_versions (
    id UUID PRIMARY KEY,
    dataset_id UUID NOT NULL REFERENCES datasets(id) ON DELETE CASCADE,
    version_number INT NOT NULL,
    items_count INT NOT NULL DEFAULT 0,
    metadata_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_dataset_version UNIQUE (dataset_id, version_number)
);

CREATE TABLE IF NOT EXISTS dataset_items (
    id UUID PRIMARY KEY,
    dataset_version_id UUID NOT NULL REFERENCES dataset_versions(id) ON DELETE CASCADE,
    input_variables TEXT NOT NULL,
    expected_output TEXT,
    expected_context TEXT,
    metadata_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS evaluation_metrics (
    id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    scoring_type VARCHAR(50) NOT NULL, -- NUMERIC, BOOLEAN, CATEGORICAL
    min_score NUMERIC(5, 2) DEFAULT 0.0,
    max_score NUMERIC(5, 2) DEFAULT 1.0,
    passing_threshold NUMERIC(5, 2) DEFAULT 0.7,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS evaluation_rubrics (
    id UUID PRIMARY KEY,
    metric_id UUID NOT NULL REFERENCES evaluation_metrics(id) ON DELETE CASCADE,
    version INT NOT NULL DEFAULT 1,
    prompt_template TEXT NOT NULL,
    criteria_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS evaluations (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    trace_id VARCHAR(64) REFERENCES traces(id) ON DELETE CASCADE,
    span_id VARCHAR(64) REFERENCES spans(id) ON DELETE CASCADE,
    experiment_run_id UUID,
    evaluation_type VARCHAR(50) NOT NULL, -- LLM_AS_A_JUDGE, DETERMINISTIC, HUMAN
    metric_name VARCHAR(100) NOT NULL,
    score NUMERIC(5, 4) NOT NULL,
    pass_fail BOOLEAN NOT NULL,
    explanation TEXT,
    evidence TEXT,
    category VARCHAR(100),
    judge_model VARCHAR(100),
    rubric_version INT DEFAULT 1,
    execution_time_ms BIGINT DEFAULT 0,
    metadata_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS experiments (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    dataset_id UUID NOT NULL REFERENCES datasets(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, RUNNING, COMPLETED, FAILED
    total_runs INT NOT NULL DEFAULT 0,
    completed_runs INT NOT NULL DEFAULT 0,
    failed_runs INT NOT NULL DEFAULT 0,
    average_score NUMERIC(5, 4) DEFAULT 0.0,
    average_latency BIGINT DEFAULT 0,
    total_cost NUMERIC(12, 6) DEFAULT 0.000000,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS experiment_runs (
    id UUID PRIMARY KEY,
    experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
    dataset_item_id UUID NOT NULL REFERENCES dataset_items(id) ON DELETE CASCADE,
    prompt_version_id UUID REFERENCES prompt_versions(id) ON DELETE SET NULL,
    model_id UUID REFERENCES models(id) ON DELETE SET NULL,
    trace_id VARCHAR(64) REFERENCES traces(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    input_text TEXT NOT NULL,
    generated_output TEXT,
    latency_ms BIGINT DEFAULT 0,
    tokens INT DEFAULT 0,
    cost NUMERIC(12, 6) DEFAULT 0.0,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS experiment_results (
    id UUID PRIMARY KEY,
    experiment_run_id UUID NOT NULL REFERENCES experiment_runs(id) ON DELETE CASCADE,
    metric_name VARCHAR(100) NOT NULL,
    score NUMERIC(5, 4) NOT NULL,
    pass_fail BOOLEAN NOT NULL,
    details_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS model_comparisons (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    input_prompt TEXT NOT NULL,
    baseline_model VARCHAR(100) NOT NULL,
    comparison_models_json TEXT NOT NULL,
    results_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS feedback (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    trace_id VARCHAR(64) REFERENCES traces(id) ON DELETE CASCADE,
    span_id VARCHAR(64) REFERENCES spans(id) ON DELETE CASCADE,
    rating VARCHAR(50) NOT NULL, -- THUMBS_UP, THUMBS_DOWN, 1_STAR, 5_STARS
    user_comment TEXT,
    tags VARCHAR(500),
    metadata_json TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    alert_type VARCHAR(100) NOT NULL, -- LATENCY_THRESHOLD, ERROR_RATE, BUDGET_EXCEEDED, EVAL_SCORE_DROP
    threshold_value NUMERIC(10, 2) NOT NULL,
    time_window_minutes INT NOT NULL DEFAULT 15,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    severity VARCHAR(50) NOT NULL DEFAULT 'WARNING', -- INFO, WARNING, CRITICAL
    last_triggered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    link_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recommendations (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    category VARCHAR(100) NOT NULL, -- COST_SAVING, LATENCY_OPTIMIZATION, PROMPT_IMPROVEMENT, MODEL_SWITCH
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    evidence_json TEXT,
    expected_impact VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN', -- OPEN, APPLIED, DISMISSED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assistant_conversations (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assistant_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES assistant_conversations(id) ON DELETE CASCADE,
    sender_role VARCHAR(50) NOT NULL, -- USER, ASSISTANT, TOOL
    content TEXT,
    tool_calls_json TEXT,
    tool_results_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CREATE INDEXES FOR FAST QUERYING
CREATE INDEX IF NOT EXISTS idx_traces_project ON traces(project_id, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_traces_status ON traces(project_id, status);
CREATE INDEX IF NOT EXISTS idx_spans_trace ON spans(trace_id);
CREATE INDEX IF NOT EXISTS idx_evaluations_project ON evaluations(project_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_evaluations_trace ON evaluations(trace_id);
CREATE INDEX IF NOT EXISTS idx_experiment_runs_exp ON experiment_runs(experiment_id);
