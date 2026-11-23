import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Model } from '../types';
import { formatCurrency } from '../utils/formatters';
import {
  Box,
  Cpu,
  Layers,
  Coins,
  Search,
  CheckCircle2,
  Clock,
  Sparkles,
  Zap,
  Eye,
  Wrench,
} from 'lucide-react';

const DEFAULT_MODELS: Model[] = [
  {
    id: '0ca8f132-f5a6-4681-93ad-1b5bacf56c80',
    providerId: '79e9bc27-2fc6-4cdc-b731-e9bc5989e865',
    modelIdentifier: 'gpt-4o',
    modelId: 'gpt-4o',
    displayName: 'OpenAI GPT-4o',
    name: 'OpenAI GPT-4o',
    contextWindow: 128000,
    maxOutputTokens: 4096,
    inputCostPerMillion: 5.0,
    outputCostPerMillion: 15.0,
    costPer1kInput: 0.005,
    costPer1kOutput: 0.015,
    supportsStreaming: true,
    supportsTools: true,
    supportsVision: true,
    active: true,
  },
  {
    id: 'e8a77ea6-4522-4aab-8e06-232c1339f4b5',
    providerId: '79e9bc27-2fc6-4cdc-b731-e9bc5989e865',
    modelIdentifier: 'gpt-4o-mini',
    modelId: 'gpt-4o-mini',
    displayName: 'OpenAI GPT-4o Mini',
    name: 'OpenAI GPT-4o Mini',
    contextWindow: 128000,
    maxOutputTokens: 4096,
    inputCostPerMillion: 0.15,
    outputCostPerMillion: 0.60,
    costPer1kInput: 0.00015,
    costPer1kOutput: 0.0006,
    supportsStreaming: true,
    supportsTools: true,
    supportsVision: true,
    active: true,
  },
  {
    id: '93e1a118-6298-4ef7-b181-27b0a02db54d',
    providerId: 'c1bd1464-f7d7-402a-97b4-c320cc9cc974',
    modelIdentifier: 'gemini-1.5-pro',
    modelId: 'gemini-1.5-pro',
    displayName: 'Google Gemini 1.5 Pro',
    name: 'Google Gemini 1.5 Pro',
    contextWindow: 1000000,
    maxOutputTokens: 8192,
    inputCostPerMillion: 3.5,
    outputCostPerMillion: 10.5,
    costPer1kInput: 0.0035,
    costPer1kOutput: 0.0105,
    supportsStreaming: true,
    supportsTools: true,
    supportsVision: true,
    active: true,
  },
  {
    id: '7e34237a-8ca0-468a-ad8c-77d7b8725262',
    providerId: 'c1bd1464-f7d7-402a-97b4-c320cc9cc974',
    modelIdentifier: 'gemini-1.5-flash',
    modelId: 'gemini-1.5-flash',
    displayName: 'Google Gemini 1.5 Flash',
    name: 'Google Gemini 1.5 Flash',
    contextWindow: 1000000,
    maxOutputTokens: 8192,
    inputCostPerMillion: 0.075,
    outputCostPerMillion: 0.30,
    costPer1kInput: 0.000075,
    costPer1kOutput: 0.0003,
    supportsStreaming: true,
    supportsTools: true,
    supportsVision: true,
    active: true,
  },
  {
    id: '0c719c2e-7555-44b1-a02c-823a88d60ad1',
    providerId: 'e1376355-4434-4bab-aa86-be9915b82e85',
    modelIdentifier: 'claude-3-5-sonnet',
    modelId: 'claude-3-5-sonnet',
    displayName: 'Anthropic Claude 3.5 Sonnet',
    name: 'Anthropic Claude 3.5 Sonnet',
    contextWindow: 200000,
    maxOutputTokens: 8192,
    inputCostPerMillion: 3.0,
    outputCostPerMillion: 15.0,
    costPer1kInput: 0.003,
    costPer1kOutput: 0.015,
    supportsStreaming: true,
    supportsTools: true,
    supportsVision: true,
    active: true,
  },
  {
    id: '962a9ead-29a6-4a07-b92f-89766624c45a',
    providerId: '064d3652-8870-4980-918e-efdd795f5de9',
    modelIdentifier: 'llama3.1',
    modelId: 'llama3.1',
    displayName: 'Meta Llama 3.1 8B (Local)',
    name: 'Meta Llama 3.1 8B (Local)',
    contextWindow: 128000,
    maxOutputTokens: 4096,
    inputCostPerMillion: 0.0,
    outputCostPerMillion: 0.0,
    costPer1kInput: 0.0,
    costPer1kOutput: 0.0,
    supportsStreaming: true,
    supportsTools: true,
    supportsVision: false,
    active: true,
  },
];

export const ModelsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [models, setModels] = useState<Model[]>(DEFAULT_MODELS);
  const [loading, setLoading] = useState<boolean>(false);
  const [search, setSearch] = useState<string>('');

  useEffect(() => {
    loadModels();
  }, [currentProject]);

  const loadModels = async () => {
    setLoading(true);
    try {
      const projId = currentProject?.id || 'b4ff1487-1c49-48b3-a781-f91740987a72';
      const data = await api.getModels(projId);
      if (data && data.length > 0) {
        setModels(data);
      } else {
        setModels(DEFAULT_MODELS);
      }
    } catch (err) {
      console.warn('Using default catalog models:', err);
      setModels(DEFAULT_MODELS);
    } finally {
      setLoading(false);
    }
  };

  const filteredModels = models.filter((m) => {
    const nameStr = (m.displayName || m.name || '').toLowerCase();
    const idStr = (m.modelIdentifier || m.modelId || '').toLowerCase();
    const query = search.toLowerCase();
    return nameStr.includes(query) || idStr.includes(query);
  });

  const getProviderBadge = (modelId: string) => {
    const id = modelId.toLowerCase();
    if (id.includes('gpt')) {
      return <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-50 dark:bg-emerald-500/15 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-500/20">OpenAI</span>;
    }
    if (id.includes('claude')) {
      return <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-amber-50 dark:bg-amber-500/15 text-amber-700 dark:text-amber-300 border border-amber-200 dark:border-amber-500/20">Anthropic</span>;
    }
    if (id.includes('gemini')) {
      return <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-indigo-50 dark:bg-indigo-500/15 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/20">Google</span>;
    }
    return <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-purple-50 dark:bg-purple-500/15 text-purple-700 dark:text-purple-300 border border-purple-200 dark:border-purple-500/20">Ollama</span>;
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
            <Box className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
            Model Registry & Pricing Catalog
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Registered LLMs across OpenAI, Anthropic, Gemini, and Ollama with pricing tiers, context windows, and fallback routes.
          </p>
        </div>

        <div className="relative w-full sm:w-64">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search models..."
            className="w-full bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-800 rounded-lg pl-9 pr-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
          />
        </div>
      </div>

      {/* Models Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden shadow-sm">
        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left border-collapse min-w-[750px]">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/40 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                <th className="py-3 px-4">Provider</th>
                <th className="py-3 px-4">Model Name</th>
                <th className="py-3 px-4">Model Identifier</th>
                <th className="py-3 px-4">Context Window</th>
                <th className="py-3 px-4">Input Pricing</th>
                <th className="py-3 px-4">Output Pricing</th>
                <th className="py-3 px-4">Capabilities</th>
                <th className="py-3 px-4">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 dark:divide-slate-800/60 text-xs">
              {loading && filteredModels.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-12 text-center text-slate-400 dark:text-slate-500">
                    Loading model registry...
                  </td>
                </tr>
              ) : filteredModels.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-12 text-center text-slate-400 dark:text-slate-500">
                    No models found matching "{search}".
                  </td>
                </tr>
              ) : (
                filteredModels.map((m) => {
                  const identifier = m.modelIdentifier || m.modelId || '';
                  const displayName = m.displayName || m.name || identifier;
                  const inCost = m.costPer1kInput ?? (m.inputCostPerMillion ? m.inputCostPerMillion / 1000 : 0.005);
                  const outCost = m.costPer1kOutput ?? (m.outputCostPerMillion ? m.outputCostPerMillion / 1000 : 0.015);
                  return (
                    <tr key={m.id || identifier} className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                      <td className="py-3.5 px-4">{getProviderBadge(identifier)}</td>
                      <td className="py-3.5 px-4 font-semibold text-slate-900 dark:text-slate-200">{displayName}</td>
                      <td className="py-3.5 px-4 font-mono text-indigo-600 dark:text-indigo-400 font-medium">{identifier}</td>
                      <td className="py-3.5 px-4 font-mono text-slate-600 dark:text-slate-300">
                        {m.contextWindow ? `${(m.contextWindow / 1000).toLocaleString()}k tokens` : '128k'}
                      </td>
                      <td className="py-3.5 px-4 font-mono text-emerald-600 dark:text-emerald-400 font-medium">
                        {formatCurrency(inCost)} / 1k
                      </td>
                      <td className="py-3.5 px-4 font-mono text-emerald-600 dark:text-emerald-400 font-medium">
                        {formatCurrency(outCost)} / 1k
                      </td>
                      <td className="py-3.5 px-4">
                        <div className="flex items-center gap-1.5 text-slate-500 dark:text-slate-400">
                          {m.supportsTools && <span title="Tool Calling / Function Execution" className="p-1 rounded bg-slate-100 dark:bg-slate-800 text-indigo-500"><Wrench className="w-3 h-3" /></span>}
                          {m.supportsVision && <span title="Multimodal Vision" className="p-1 rounded bg-slate-100 dark:bg-slate-800 text-cyan-500"><Eye className="w-3 h-3" /></span>}
                          {m.supportsStreaming && <span title="Token Streaming" className="p-1 rounded bg-slate-100 dark:bg-slate-800 text-amber-500"><Zap className="w-3 h-3" /></span>}
                        </div>
                      </td>
                      <td className="py-3.5 px-4">
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 dark:bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/20">
                          <CheckCircle2 className="w-3 h-3" /> ACTIVE
                        </span>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
