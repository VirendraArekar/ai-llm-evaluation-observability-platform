import { apiRequest } from './client';
import * as T from '../types';

export const api = {
  // Auth
  login: (data: any) => apiRequest<{ accessToken: string; user: T.User; defaultProjectId: string }>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  register: (data: any) => apiRequest<{ accessToken: string; user: T.User; defaultProjectId: string }>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  getMe: () => apiRequest<T.User>('/auth/me'),

  // Organizations & Workspaces
  getOrganizations: () => apiRequest<T.Organization[]>('/organizations'),
  getWorkspaces: (orgId: string) => apiRequest<T.Workspace[]>(`/organizations/${orgId}/workspaces`),
  getProjects: (workspaceId: string) => apiRequest<T.Project[]>(`/projects?workspaceId=${workspaceId}`),
  getProject: (id: string) => apiRequest<T.Project>(`/projects/${id}`),
  getApiKeys: (projectId: string) => apiRequest<T.ApiKey[]>(`/projects/${projectId}/api-keys`),
  createApiKey: (projectId: string, data: any) => apiRequest<T.ApiKey>(`/projects/${projectId}/api-keys`, { method: 'POST', body: JSON.stringify(data) }),
  revokeApiKey: (apiKeyId: string) => apiRequest<void>(`/projects/api-keys/${apiKeyId}`, { method: 'DELETE' }),

  // Providers & Models
  getProviders: (projectId: string) => apiRequest<T.LlmProvider[]>(`/providers?projectId=${projectId}`),
  createProvider: (projectId: string, data: any) => apiRequest<T.LlmProvider>(`/providers?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),
  testProvider: (id: string) => apiRequest<boolean>(`/providers/${id}/test`, { method: 'POST' }),
  getModels: (projectId: string) => apiRequest<T.Model[]>(`/models?projectId=${projectId}`),

  // Prompts
  getPrompts: (projectId: string) => apiRequest<T.Prompt[]>(`/prompts?projectId=${projectId}`),
  getPrompt: (id: string) => apiRequest<T.Prompt>(`/prompts/${id}`),
  createPrompt: (projectId: string, data: any) => apiRequest<T.Prompt>(`/prompts?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),
  getVersions: (promptId: string) => apiRequest<T.PromptVersion[]>(`/prompts/${promptId}/versions`),
  createVersion: (promptId: string, data: any) => apiRequest<T.PromptVersion>(`/prompts/${promptId}/versions`, { method: 'POST', body: JSON.stringify(data) }),
  publishVersion: (promptId: string, versionNumber: number) => apiRequest<void>(`/prompts/${promptId}/versions/${versionNumber}/publish`, { method: 'POST' }),
  rollbackVersion: (promptId: string, versionNumber: number) => apiRequest<T.PromptVersion>(`/prompts/${promptId}/versions/${versionNumber}/rollback`, { method: 'POST' }),
  testPrompt: (promptId: string, data: any, version?: number) => apiRequest<any>(`/prompts/${promptId}/test${version ? `?version=${version}` : ''}`, { method: 'POST', body: JSON.stringify(data) }),
  optimizePrompt: (data: any) => apiRequest<any>('/prompts/optimize', { method: 'POST', body: JSON.stringify(data) }),

  // Traces
  getTraces: (projectId: string, status?: string, query?: string, page = 0, size = 20) => {
    let url = `/traces?projectId=${projectId}&page=${page}&size=${size}`;
    if (status) url += `&status=${status}`;
    if (query) url += `&query=${encodeURIComponent(query)}`;
    return apiRequest<{ content: T.Trace[]; totalPages: number; totalElements: number }>(url);
  },
  getTraceDetail: (id: string) => apiRequest<T.TraceDetail>(`/traces/${id}`),
  getTraceStats: (projectId: string) => apiRequest<any>(`/traces/stats?projectId=${projectId}`),

  // Evaluations
  getEvaluations: (projectId: string, page = 0, size = 20) => apiRequest<{ content: T.Evaluation[]; totalPages: number; totalElements: number }>(`/evaluations?projectId=${projectId}&page=${page}&size=${size}`),
  runJudge: (data: any) => apiRequest<any>('/evaluations/run-judge', { method: 'POST', body: JSON.stringify(data) }),
  detectHallucination: (data: any) => apiRequest<any>('/evaluations/detect-hallucination', { method: 'POST', body: JSON.stringify(data) }),
  getMetrics: (projectId: string) => apiRequest<T.EvaluationMetric[]>(`/evaluations/metrics?projectId=${projectId}`),
  getEvaluationStats: (projectId: string) => apiRequest<any>(`/evaluations/stats?projectId=${projectId}`),

  // Datasets
  getDatasets: (projectId: string) => apiRequest<T.Dataset[]>(`/datasets?projectId=${projectId}`),
  getDataset: (id: string) => apiRequest<T.Dataset>(`/datasets/${id}`),
  createDataset: (projectId: string, data: any) => apiRequest<T.Dataset>(`/datasets?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),
  getDatasetItems: (datasetId: string, page = 0, size = 50) => apiRequest<{ content: T.DatasetItem[]; totalPages: number; totalElements: number }>(`/datasets/${datasetId}/items?page=${page}&size=${size}`),
  uploadDatasetFile: (datasetId: string, content: string, contentType: string) => apiRequest<T.Dataset>(`/datasets/${datasetId}/upload`, { method: 'POST', body: content, headers: { 'Content-Type': contentType } }),

  // Experiments
  getExperiments: (projectId: string) => apiRequest<T.Experiment[]>(`/experiments?projectId=${projectId}`),
  getExperiment: (id: string) => apiRequest<T.Experiment>(`/experiments/${id}`),
  getExperimentRuns: (experimentId: string) => apiRequest<T.ExperimentRun[]>(`/experiments/${experimentId}/runs`),
  createExperiment: (projectId: string, data: any) => apiRequest<T.Experiment>(`/experiments?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),
  compareExperiments: (baselineId: string, candidateId: string) => apiRequest<any>(`/experiments/compare?baselineId=${baselineId}&candidateId=${candidateId}`),

  // Benchmarks
  getBenchmarks: (projectId: string) => apiRequest<any[]>(`/benchmarks?projectId=${projectId}`),
  runBenchmark: (projectId: string, data: any) => apiRequest<any>(`/benchmarks/compare?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),

  // Analytics
  getOverviewMetrics: (projectId: string) => apiRequest<any>(`/analytics/overview?projectId=${projectId}`),
  getCostAnalytics: (projectId: string) => apiRequest<any>(`/analytics/costs?projectId=${projectId}`),

  // Monitoring & Alerts
  getAlerts: (projectId: string) => apiRequest<T.Alert[]>(`/monitoring/alerts?projectId=${projectId}`),
  createAlert: (projectId: string, data: any) => apiRequest<T.Alert>(`/monitoring/alerts?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),
  toggleAlert: (id: string) => apiRequest<void>(`/monitoring/alerts/${id}/toggle`, { method: 'PATCH' }),
  getNotifications: () => apiRequest<T.Notification[]>('/monitoring/notifications'),
  markNotificationRead: (id: string) => apiRequest<void>(`/monitoring/notifications/${id}/read`, { method: 'PATCH' }),

  // Playground
  runPlayground: (data: any) => apiRequest<any>('/playground/run', { method: 'POST', body: JSON.stringify(data) }),

  // AI Assistant
  getConversations: (projectId: string) => apiRequest<T.AssistantConversation[]>(`/assistant/conversations?projectId=${projectId}`),
  createConversation: (projectId: string, title?: string) => apiRequest<T.AssistantConversation>(`/assistant/conversations?projectId=${projectId}`, { method: 'POST', body: JSON.stringify({ title }) }),
  getMessages: (conversationId: string) => apiRequest<T.AssistantMessage[]>(`/assistant/conversations/${conversationId}/messages`),
  sendMessage: (conversationId: string, content: string) => apiRequest<T.AssistantMessage>(`/assistant/conversations/${conversationId}/messages`, { method: 'POST', body: JSON.stringify({ content }) }),

  // Root Cause Intelligence
  analyzeRootCause: (data: any) => apiRequest<any>('/intelligence/root-cause', { method: 'POST', body: JSON.stringify(data) }),

  // Recommendations
  getRecommendations: (projectId: string) => apiRequest<T.Recommendation[]>(`/recommendations?projectId=${projectId}`),
  generateRecommendations: (projectId: string) => apiRequest<T.Recommendation[]>(`/recommendations/generate?projectId=${projectId}`, { method: 'POST' }),
  applyRecommendation: (id: string) => apiRequest<T.Recommendation>(`/recommendations/${id}/apply`, { method: 'POST' }),

  // Feedback
  getFeedback: (projectId: string, page = 0, size = 20) => apiRequest<{ content: T.Feedback[]; totalPages: number; totalElements: number }>(`/feedback?projectId=${projectId}&page=${page}&size=${size}`),
  createFeedback: (projectId: string, data: any) => apiRequest<T.Feedback>(`/feedback?projectId=${projectId}`, { method: 'POST', body: JSON.stringify(data) }),
  getFeedbackStats: (projectId: string) => apiRequest<any>(`/feedback/stats?projectId=${projectId}`),
  convertFeedbackToDataset: (feedbackId: string, datasetId: string) => apiRequest<void>(`/feedback/${feedbackId}/convert-to-dataset?datasetId=${datasetId}`, { method: 'POST' }),

  // Telemetry Simulation & Seeding
  simulateTelemetry: (projectId?: string) => apiRequest<any>(`/telemetry/simulate${projectId ? `?projectId=${projectId}` : ''}`, { method: 'POST' }),
  forceSeedAll: () => apiRequest<any>('/telemetry/seed-all', { method: 'POST' }),
};
