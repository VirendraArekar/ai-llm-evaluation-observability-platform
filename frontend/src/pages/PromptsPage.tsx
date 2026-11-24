import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Prompt, PromptVersion } from '../types';
import { formatRelativeTime } from '../utils/formatters';
import {
  Code2,
  Plus,
  Sparkles,
  History,
  RotateCcw,
  CheckCircle2,
  Copy,
  Layers,
  ArrowRight,
  Send,
  Eye,
} from 'lucide-react';

export const PromptsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [prompts, setPrompts] = useState<Prompt[]>([]);
  const [selectedPromptId, setSelectedPromptId] = useState<string | null>(null);
  const [versions, setVersions] = useState<PromptVersion[]>([]);
  const [selectedVersion, setSelectedVersion] = useState<PromptVersion | null>(null);
  const [newTemplate, setNewTemplate] = useState('');
  const [commitMessage, setCommitMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [showOptimizerModal, setShowOptimizerModal] = useState(false);
  const [optimizationGoal, setOptimizationGoal] = useState('Reduce token usage and prevent hallucination');
  const [optimizing, setOptimizing] = useState(false);
  const [optimizedResult, setOptimizedResult] = useState<any>(null);

  // New prompt modal state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newPromptName, setNewPromptName] = useState('');
  const [newPromptSlug, setNewPromptSlug] = useState('');
  const [newPromptDesc, setNewPromptDesc] = useState('');

  useEffect(() => {
    if (currentProject) {
      loadPrompts();
    }
  }, [currentProject]);

  useEffect(() => {
    if (selectedPromptId) {
      loadVersions(selectedPromptId);
    }
  }, [selectedPromptId]);

  const loadPrompts = async () => {
    if (!currentProject) return;
    try {
      const data = await api.getPrompts(currentProject.id);
      if (data && data.length > 0) {
        setPrompts(data);
        if (!selectedPromptId) {
          setSelectedPromptId(data[0].id);
        }
      } else {
        const fallbackPrompts: Prompt[] = [
          {
            id: 'p-seed-1',
            projectId: 'b4ff1487-1c49-48b3-a781-f91740987a72',
            name: 'Customer Support Inquiry Resolver',
            slug: 'customer-support-inquiry-resolver',
            description: 'Answers customer questions using knowledge base and context',
            latestVersion: 2,
            createdAt: '2025-05-18T10:00:00Z',
            updatedAt: '2025-05-18T11:30:00Z',
          },
          {
            id: 'p-seed-2',
            projectId: 'b4ff1487-1c49-48b3-a781-f91740987a72',
            name: 'Financial Report Summarizer',
            slug: 'financial-report-summarizer',
            description: 'Extracts key earnings and EBITDA metrics from 10-K filings',
            latestVersion: 1,
            createdAt: '2025-05-17T09:00:00Z',
            updatedAt: '2025-05-17T09:00:00Z',
          },
        ];
        setPrompts(fallbackPrompts);
        if (!selectedPromptId) {
          setSelectedPromptId(fallbackPrompts[0].id);
        }
      }
    } catch (err) {
      console.error('Failed to load prompts', err);
      const fallbackPrompts: Prompt[] = [
        {
          id: 'p-seed-1',
          projectId: 'b4ff1487-1c49-48b3-a781-f91740987a72',
          name: 'Customer Support Inquiry Resolver',
          slug: 'customer-support-inquiry-resolver',
          description: 'Answers customer questions using knowledge base and context',
          latestVersion: 2,
          createdAt: '2025-05-18T10:00:00Z',
          updatedAt: '2025-05-18T11:30:00Z',
        },
        {
          id: 'p-seed-2',
          projectId: 'b4ff1487-1c49-48b3-a781-f91740987a72',
          name: 'Financial Report Summarizer',
          slug: 'financial-report-summarizer',
          description: 'Extracts key earnings and EBITDA metrics from 10-K filings',
          latestVersion: 1,
          createdAt: '2025-05-17T09:00:00Z',
          updatedAt: '2025-05-17T09:00:00Z',
        },
      ];
      setPrompts(fallbackPrompts);
      if (!selectedPromptId) {
        setSelectedPromptId(fallbackPrompts[0].id);
      }
    }
  };

  const loadVersions = async (promptId: string) => {
    try {
      const vData = await api.getVersions(promptId);
      if (vData && vData.length > 0) {
        setVersions(vData);
        const published = vData.find((v) => v.isPublished) || vData[0];
        setSelectedVersion(published);
        setNewTemplate(published.template);
      } else {
        const fallbackVersion: PromptVersion = {
          id: 'v-seed-1',
          promptId,
          versionNumber: 1,
          template: 'You are a senior support engineer for Acme Cloud. Using ONLY the provided context, answer the user question:\nContext: {{context}}\nUser Question: {{question}}\nRules:\n- Be polite, concise, and professional\n- If context lacks the answer, state "I do not have enough information to answer."',
          commitMessage: 'Production baseline template',
          isPublished: true,
          createdAt: '2025-05-18T10:00:00Z',
        };
        setVersions([fallbackVersion]);
        setSelectedVersion(fallbackVersion);
        setNewTemplate(fallbackVersion.template);
      }
    } catch (err) {
      console.error('Failed to load prompt versions', err);
      const fallbackVersion: PromptVersion = {
        id: 'v-seed-1',
        promptId,
        versionNumber: 1,
        template: 'You are a senior support engineer for Acme Cloud. Using ONLY the provided context, answer the user question:\nContext: {{context}}\nUser Question: {{question}}\nRules:\n- Be polite, concise, and professional\n- If context lacks the answer, state "I do not have enough information to answer."',
        commitMessage: 'Production baseline template',
        isPublished: true,
        createdAt: '2025-05-18T10:00:00Z',
      };
      setVersions([fallbackVersion]);
      setSelectedVersion(fallbackVersion);
      setNewTemplate(fallbackVersion.template);
    }
  };

  const handleCreatePrompt = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentProject || !newPromptName) return;
    try {
      const created = await api.createPrompt(currentProject.id, {
        name: newPromptName,
        slug: newPromptSlug || newPromptName.toLowerCase().replace(/\s+/g, '-'),
        description: newPromptDesc,
        template: 'You are an AI assistant. Answer the user prompt: {{input}}',
      });
      setPrompts((prev) => [created, ...prev]);
      setSelectedPromptId(created.id);
      setShowCreateModal(false);
      setNewPromptName('');
      setNewPromptSlug('');
      setNewPromptDesc('');
    } catch (err) {
      console.error('Failed to create prompt', err);
    }
  };

  const handleSaveVersion = async () => {
    if (!selectedPromptId || !newTemplate) return;
    setLoading(true);
    try {
      const newV = await api.createVersion(selectedPromptId, {
        template: newTemplate,
        commitMessage: commitMessage || 'Updated prompt template',
      });
      setVersions((prev) => [newV, ...prev]);
      setSelectedVersion(newV);
      setCommitMessage('');
    } catch (err) {
      console.error('Failed to save version', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePublish = async (versionNumber: number) => {
    if (!selectedPromptId) return;
    try {
      await api.publishVersion(selectedPromptId, versionNumber);
      loadVersions(selectedPromptId);
    } catch (err) {
      console.error('Failed to publish version', err);
    }
  };

  const handleRollback = async (versionNumber: number) => {
    if (!selectedPromptId) return;
    try {
      await api.rollbackVersion(selectedPromptId, versionNumber);
      loadVersions(selectedPromptId);
    } catch (err) {
      console.error('Failed to rollback version', err);
    }
  };

  const handleRunOptimizer = async () => {
    if (!selectedPromptId || !newTemplate) return;
    setOptimizing(true);
    setOptimizedResult(null);
    try {
      const res = await api.optimizePrompt({
        promptId: selectedPromptId,
        currentTemplate: newTemplate,
        optimizationGoal,
      });
      setOptimizedResult(res);
    } catch (err) {
      console.error('Optimization failed', err);
    } finally {
      setOptimizing(false);
    }
  };

  const extractVariables = (text: string) => {
    const matches = text.match(/{{\s*([a-zA-Z0-9_]+)\s*}}/g) || [];
    return Array.from(new Set(matches.map((m) => m.replace(/[{}]/g, '').trim())));
  };

  const currentVariables = extractVariables(newTemplate);

  return (
    <div className="flex flex-col lg:flex-row h-[calc(100vh-4rem)] bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 overflow-hidden">
      {/* Sidebar: Prompts List */}
      <div className="w-full lg:w-72 bg-white dark:bg-slate-900 border-b lg:border-b-0 lg:border-r border-slate-200 dark:border-slate-800 flex flex-col shrink-0 overflow-y-auto">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Code2 className="w-5 h-5 text-indigo-500 dark:text-indigo-400" />
            <h2 className="font-semibold text-sm text-slate-900 dark:text-slate-200">Prompt Studio</h2>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="p-1.5 rounded-lg bg-indigo-50 dark:bg-indigo-600/20 text-indigo-600 dark:text-indigo-400 hover:bg-indigo-100 dark:hover:bg-indigo-600/30 transition-colors"
            title="Create Prompt"
          >
            <Plus className="w-4 h-4" />
          </button>
        </div>

        <div className="p-3 flex lg:flex-col gap-2 overflow-x-auto lg:overflow-y-auto custom-scrollbar max-h-48 lg:max-h-none">
          {prompts.map((p) => (
            <button
              key={p.id}
              onClick={() => setSelectedPromptId(p.id)}
              className={`min-w-[200px] lg:min-w-0 w-full text-left p-3 rounded-lg text-xs font-medium transition-colors shrink-0 ${
                selectedPromptId === p.id
                  ? 'bg-indigo-50 dark:bg-indigo-600/20 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800/60 hover:text-slate-900 dark:hover:text-slate-200'
              }`}
            >
              <div className="font-semibold text-slate-900 dark:text-slate-200">{p.name}</div>
              <div className="text-[11px] font-mono text-indigo-600 dark:text-indigo-400 mt-0.5">{p.slug}</div>
              <div className="text-[10px] text-slate-500 line-clamp-1 mt-1">{p.description}</div>
            </button>
          ))}
          {prompts.length === 0 && (
            <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-8">No prompts configured.</p>
          )}
        </div>
      </div>

      {/* Center: Template Editor & Actions */}
      <div className="flex-1 flex flex-col border-b lg:border-b-0 lg:border-r border-slate-200 dark:border-slate-800 overflow-y-auto custom-scrollbar">
        {selectedPromptId ? (
          <>
            {/* Action Bar */}
            <div className="p-4 border-b border-slate-200 dark:border-slate-800 bg-white/70 dark:bg-slate-900/40 backdrop-blur flex flex-wrap items-center justify-between gap-3">
              <div className="flex items-center gap-3">
                <span className="text-sm font-bold text-slate-900 dark:text-slate-200">
                  {prompts.find((p) => p.id === selectedPromptId)?.name}
                </span>
                {selectedVersion && (
                  <span className="px-2 py-0.5 rounded text-[11px] font-mono bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700">
                    v{selectedVersion.versionNumber} {selectedVersion.isPublished && '• Published'}
                  </span>
                )}
              </div>

              <div className="flex flex-wrap items-center gap-2">
                <button
                  onClick={() => setShowOptimizerModal(true)}
                  className="px-3.5 py-1.5 rounded-lg bg-indigo-50 dark:bg-indigo-600/20 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30 hover:bg-indigo-100 dark:hover:bg-indigo-600/30 text-xs font-semibold flex items-center gap-1.5 transition-colors"
                >
                  <Sparkles className="w-3.5 h-3.5 text-indigo-500 dark:text-indigo-400" />
                  AI Prompt Optimizer
                </button>
                <button
                  onClick={handleSaveVersion}
                  disabled={loading}
                  className="px-4 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-40 text-white text-xs font-semibold flex items-center gap-1.5 transition-colors shadow-sm"
                >
                  <Send className="w-3.5 h-3.5" />
                  Save New Version
                </button>
              </div>
            </div>

            {/* Template Editing Area */}
            <div className="p-4 sm:p-6 space-y-6">
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-xs font-semibold text-slate-600 dark:text-slate-400 uppercase tracking-wider">
                    Prompt Template
                  </label>
                  <span className="text-xs text-slate-500">
                    Use <code className="text-indigo-600 dark:text-indigo-400">{'{{variable}}'}</code> for dynamic parameters
                  </span>
                </div>
                <textarea
                  rows={12}
                  value={newTemplate}
                  onChange={(e) => setNewTemplate(e.target.value)}
                  className="w-full bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-800 rounded-lg p-4 font-mono text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500 transition-colors"
                />
              </div>

              {/* Detected Variables Pill Bar */}
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 uppercase tracking-wider mb-2">
                  Detected Template Variables
                </label>
                <div className="flex flex-wrap gap-2">
                  {currentVariables.length > 0 ? (
                    currentVariables.map((v) => (
                      <span
                        key={v}
                        className="px-2.5 py-1 rounded-md bg-indigo-50 dark:bg-indigo-600/10 text-indigo-700 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-500/20 text-xs font-mono font-medium"
                      >
                        {`{{${v}}}`}
                      </span>
                    ))
                  ) : (
                    <span className="text-xs text-slate-400 dark:text-slate-500">No dynamic variables detected.</span>
                  )}
                </div>
              </div>

              {/* Commit Message */}
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 uppercase tracking-wider mb-2">
                  Version Commit Note
                </label>
                <input
                  type="text"
                  value={commitMessage}
                  onChange={(e) => setCommitMessage(e.target.value)}
                  placeholder="e.g., Added few-shot examples to reduce hallucination..."
                  className="w-full bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-800 rounded-lg px-4 py-2.5 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>
            </div>
          </>
        ) : (
          <div className="p-12 text-center text-slate-400 dark:text-slate-500">Select a prompt to begin editing.</div>
        )}
      </div>

      {/* Right Sidebar: Version History & Rollbacks */}
      <div className="w-full lg:w-80 bg-white dark:bg-slate-900 flex flex-col shrink-0 p-4 sm:p-5 space-y-4">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-2">
          <History className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> Version History
        </h3>

        <div className="space-y-2.5 max-h-64 lg:max-h-none overflow-y-auto custom-scrollbar">
          {versions.map((v) => (
            <div
              key={v.id}
              onClick={() => {
                setSelectedVersion(v);
                setNewTemplate(v.template);
              }}
              className={`p-3 rounded-lg border text-xs cursor-pointer transition-all ${
                selectedVersion?.id === v.id
                  ? 'bg-slate-100 dark:bg-slate-800 border-indigo-300 dark:border-indigo-500/40 text-slate-900 dark:text-slate-200'
                  : 'bg-slate-50 dark:bg-slate-950/60 border-slate-200 dark:border-slate-800/80 text-slate-600 dark:text-slate-400 hover:border-slate-300 dark:hover:border-slate-700'
              }`}
            >
              <div className="flex items-center justify-between mb-1">
                <span className="font-mono font-bold text-slate-900 dark:text-slate-200">v{v.versionNumber}</span>
                {v.isPublished && (
                  <span className="px-1.5 py-0.5 rounded text-[10px] bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400 font-semibold">
                    ACTIVE
                  </span>
                )}
              </div>
              <p className="text-[11px] text-slate-600 dark:text-slate-300 line-clamp-1 mb-2">{v.commitMessage || 'No note'}</p>
              <div className="flex items-center justify-between pt-2 border-t border-slate-200 dark:border-slate-800 text-[10px] text-slate-500">
                <span>{formatRelativeTime(v.createdAt)}</span>
                <div className="flex items-center gap-2">
                  {!v.isPublished && (
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        handlePublish(v.versionNumber);
                      }}
                      className="text-indigo-600 dark:text-indigo-400 hover:text-indigo-500 font-medium"
                    >
                      Publish
                    </button>
                  )}
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleRollback(v.versionNumber);
                    }}
                    className="text-amber-600 dark:text-amber-400 hover:text-amber-500 font-medium flex items-center gap-0.5"
                  >
                    <RotateCcw className="w-3 h-3" /> Rollback
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* AI Prompt Optimizer Modal */}
      {showOptimizerModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-2xl w-full p-5 sm:p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-indigo-500 dark:text-indigo-400" />
                <h3 className="font-bold text-slate-900 dark:text-slate-100">AI Prompt Optimizer</h3>
              </div>
              <button
                onClick={() => setShowOptimizerModal(false)}
                className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 text-xs"
              >
                Close
              </button>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 uppercase tracking-wider mb-1.5">
                Optimization Objective
              </label>
              <input
                type="text"
                value={optimizationGoal}
                onChange={(e) => setOptimizationGoal(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>

            <button
              onClick={handleRunOptimizer}
              disabled={optimizing}
              className="w-full py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-40 text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-sm"
            >
              <Sparkles className={`w-4 h-4 ${optimizing ? 'animate-spin' : ''}`} />
              {optimizing ? 'Synthesizing Optimized Alternatives...' : 'Generate 3 Optimized Variants'}
            </button>

            {/* Results */}
            {optimizedResult && (
              <div className="space-y-3 max-h-72 overflow-y-auto custom-scrollbar pt-2">
                <p className="text-xs text-slate-500 dark:text-slate-400">{optimizedResult.summary}</p>
                {optimizedResult.variants?.map((v: any, idx: number) => (
                  <div key={idx} className="p-3 bg-slate-50 dark:bg-slate-950 rounded-lg border border-slate-200 dark:border-slate-800 space-y-2">
                    <div className="flex justify-between items-center text-xs font-semibold text-indigo-600 dark:text-indigo-300">
                      <span>Variant {idx + 1}: {v.name}</span>
                      <span className="text-[11px] text-emerald-600 dark:text-emerald-400">{v.expectedTokenReduction}</span>
                    </div>
                    <pre className="text-[11px] font-mono text-slate-700 dark:text-slate-300 whitespace-pre-wrap">{v.template}</pre>
                    <button
                      onClick={() => {
                        setNewTemplate(v.template);
                        setShowOptimizerModal(false);
                      }}
                      className="px-3 py-1 rounded bg-indigo-600/10 text-indigo-600 dark:bg-indigo-600/30 dark:text-indigo-300 hover:bg-indigo-600/20 text-xs font-semibold"
                    >
                      Apply This Variant
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Create Prompt Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <form
            onSubmit={handleCreatePrompt}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-5 sm:p-6 space-y-4 shadow-2xl"
          >
            <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm">Create New Prompt Template</h3>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Prompt Name</label>
              <input
                type="text"
                required
                value={newPromptName}
                onChange={(e) => setNewPromptName(e.target.value)}
                placeholder="e.g., Customer Support Classifier"
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Slug</label>
              <input
                type="text"
                value={newPromptSlug}
                onChange={(e) => setNewPromptSlug(e.target.value)}
                placeholder="e.g., support-classifier"
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 font-mono focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Description</label>
              <textarea
                rows={2}
                value={newPromptDesc}
                onChange={(e) => setNewPromptDesc(e.target.value)}
                placeholder="Purpose of this prompt..."
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowCreateModal(false)}
                className="px-3 py-1.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold shadow-sm"
              >
                Create Prompt
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
