export interface User {
  id: string;
  email: string;
  fullName: string;
  avatarUrl?: string;
  role: 'SUPER_ADMIN' | 'ORG_ADMIN' | 'DEVELOPER' | 'EVALUATOR' | 'VIEWER';
  active: boolean;
  createdAt: string;
}

export interface Organization {
  id: string;
  name: string;
  slug: string;
  plan: string;
  createdAt: string;
}

export interface Workspace {
  id: string;
  organizationId: string;
  name: string;
  slug: string;
  description?: string;
  createdAt: string;
}

export interface Project {
  id: string;
  workspaceId: string;
  name: string;
  slug: string;
  description?: string;
  createdAt: string;
  totalTraces?: number;
  totalEvaluations?: number;
}

export interface ApiKey {
  id: string;
  projectId: string;
  name: string;
  keyPrefix: string;
  scopes?: string;
  lastUsedAt?: string;
  expiresAt?: string;
  active?: boolean;
  createdAt: string;
  rawKey?: string;
}

export interface LlmProvider {
  id: string;
  projectId: string;
  providerType?: 'OPENAI' | 'GEMINI' | 'CLAUDE' | 'OLLAMA' | 'CUSTOM_OPENAI';
  type?: string;
  name: string;
  baseUrl?: string;
  enabled?: boolean;
  isActive?: boolean;
  defaultProvider?: boolean;
  maskedApiKey?: string;
  createdAt?: string;
  models?: Model[];
}

export interface Model {
  id: string;
  providerId: string;
  modelIdentifier?: string;
  modelId?: string;
  displayName?: string;
  name?: string;
  contextWindow?: number;
  maxOutputTokens?: number;
  inputCostPerMillion?: number;
  outputCostPerMillion?: number;
  costPer1kInput?: number;
  costPer1kOutput?: number;
  supportsStreaming?: boolean;
  supportsTools?: boolean;
  supportsVision?: boolean;
  active?: boolean;
}

export interface PromptVersion {
  id: string;
  promptId: string;
  versionNumber: number;
  template: string;
  systemMessage?: string;
  variablesSchema?: string;
  modelId?: string;
  modelIdentifier?: string;
  temperature?: number;
  maxTokens?: number;
  changeSummary?: string;
  commitMessage?: string;
  published?: boolean;
  isPublished?: boolean;
  createdByName?: string;
  createdAt: string;
}

export interface Prompt {
  id: string;
  projectId: string;
  name: string;
  slug: string;
  description?: string;
  tags?: string;
  active?: boolean;
  createdAt: string;
  updatedAt?: string;
  latestVersion?: number;
  publishedVersion?: PromptVersion;
}

export interface Span {
  id: string;
  spanId?: string;
  traceId: string;
  parentSpanId?: string;
  name: string;
  spanType: 'LLM' | 'TOOL' | 'RETRIEVAL' | 'AGENT' | 'CHAIN';
  status: 'SUCCESS' | 'ERROR';
  startTime: string;
  endTime?: string;
  latencyMs: number;
  durationMs?: number;
  inputJson?: string;
  outputJson?: string;
  input?: string;
  output?: string;
  attributes?: any;
  modelName?: string;
  providerName?: string;
  inputTokens?: number;
  outputTokens?: number;
  totalTokens?: number;
  cost?: number;
  errorMessage?: string;
  stackTrace?: string;
}

export interface SpanNode {
  span: Span;
  relativeStartPercent: number;
  relativeDurationPercent: number;
  children: SpanNode[];
}

export interface Trace {
  id: string;
  traceId?: string;
  projectId: string;
  sessionId?: string;
  userId?: string;
  name?: string;
  rootSpanName?: string;
  status: 'SUCCESS' | 'ERROR' | 'RUNNING' | 'TIMEOUT';
  latencyMs: number;
  totalTokens?: number;
  inputTokens?: number;
  outputTokens?: number;
  estimatedCost?: number;
  totalCost?: number;
  tags?: string;
  startTime?: string;
  createdAt?: string;
  endTime?: string;
  spanCount?: number;
  errorMessage?: string;
}

export interface TraceDetail {
  trace: Trace;
  waterfallTree: SpanNode[];
  allSpans: Span[];
  spans?: Span[];
}

export interface EvaluationMetric {
  id: string;
  name: string;
  code?: string;
  category?: string;
  scoringType?: string;
  type?: string;
  description?: string;
  minScore?: number;
  maxScore?: number;
  passingThreshold?: number;
  system?: boolean;
}

export interface Evaluation {
  id: string;
  projectId: string;
  traceId?: string;
  spanId?: string;
  experimentRunId?: string;
  evaluationType?: string;
  metricName: string;
  score: number;
  passFail?: boolean;
  passed?: boolean;
  explanation?: string;
  reasoning?: string;
  evidence?: string;
  category?: string;
  judgeModel?: string;
  rubricVersion?: number;
  executionTimeMs?: number;
  createdAt: string;
}

export interface DatasetItem {
  id: string;
  datasetVersionId?: string;
  inputVariables?: string;
  input?: string;
  expectedOutput?: string;
  expectedContext?: string;
  metadataJson?: string;
  createdAt?: string;
}

export interface Dataset {
  id: string;
  projectId: string;
  name: string;
  description?: string;
  tags?: string[] | string;
  currentVersion?: number;
  totalItems?: number;
  itemCount?: number;
  createdAt?: string;
}

export interface ExperimentRunResult {
  metricName: string;
  score: number;
  passFail: boolean;
  detailsJson?: string;
}

export interface ExperimentRun {
  id: string;
  experimentId: string;
  datasetItemId?: string;
  status: string;
  inputText?: string;
  generatedOutput?: string;
  latencyMs?: number;
  avgLatencyMs?: number;
  tokens?: number;
  cost?: number;
  totalCost?: number;
  accuracyScore?: number;
  errorMessage?: string;
  results?: ExperimentRunResult[];
}

export interface Experiment {
  id: string;
  projectId: string;
  datasetId?: string;
  datasetName?: string;
  name: string;
  description?: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
  totalRuns?: number;
  completedRuns?: number;
  failedRuns?: number;
  averageScore?: number;
  accuracyScore?: number;
  averageLatency?: number;
  avgLatencyMs?: number;
  totalCost?: number;
  createdAt?: string;
}

export interface Recommendation {
  id: string;
  projectId: string;
  category: string;
  title: string;
  description: string;
  evidenceJson?: string;
  expectedImpact?: string;
  status: 'OPEN' | 'APPLIED' | 'DISMISSED';
  createdAt: string;
}

export interface Alert {
  id: string;
  projectId: string;
  name: string;
  alertType?: string;
  metric?: string;
  thresholdValue?: number;
  threshold?: number;
  condition?: string;
  timeWindowMinutes?: number;
  enabled: boolean;
  severity?: string;
  lastTriggeredAt?: string;
  createdAt?: string;
}

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: string;
  read: boolean;
  isRead?: boolean;
  linkUrl?: string;
  createdAt: string;
}

export interface Feedback {
  id: string;
  projectId: string;
  traceId?: string;
  spanId?: string;
  rating: string;
  comment?: string;
  userComment?: string;
  tags?: string;
  createdByName?: string;
  createdAt: string;
}

export interface AssistantMessage {
  id: string;
  conversationId: string;
  senderRole?: 'USER' | 'ASSISTANT' | 'TOOL';
  role?: 'USER' | 'ASSISTANT' | 'TOOL';
  content: string;
  toolCallsJson?: string;
  toolResultsJson?: string;
  toolCalls?: any[];
  createdAt: string;
}

export interface AssistantConversation {
  id: string;
  projectId: string;
  title: string;
  createdAt: string;
  updatedAt?: string;
}
