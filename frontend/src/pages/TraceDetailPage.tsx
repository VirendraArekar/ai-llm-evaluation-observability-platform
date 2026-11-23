import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../api/services';
import { TraceDetail, Span } from '../types';
import { WaterfallVisualizer } from '../components/common/WaterfallVisualizer';
import {
  ArrowLeft,
  Activity,
  Clock,
  Coins,
  Layers,
  Sparkles,
  CheckCircle2,
  XCircle,
  Cpu,
  FileText,
} from 'lucide-react';
import { formatCurrency, formatLatency, formatNumber } from '../utils/formatters';

export const TraceDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [traceDetail, setTraceDetail] = useState<TraceDetail | null>(null);
  const [selectedSpanId, setSelectedSpanId] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [analyzingRootCause, setAnalyzingRootCause] = useState<boolean>(false);
  const [rootCauseResult, setRootCauseResult] = useState<any>(null);

  useEffect(() => {
    if (id) {
      loadTrace();
    }
  }, [id]);

  const loadTrace = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await api.getTraceDetail(id);
      setTraceDetail(data);
      const spansList = data.allSpans || data.spans || [];
      if (spansList.length > 0) {
        setSelectedSpanId(spansList[0].id);
      }
    } catch (err) {
      console.error('Failed to load trace detail', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRunRootCauseAnalysis = async () => {
    if (!traceDetail) return;
    setAnalyzingRootCause(true);
    setRootCauseResult(null);

    const spansList = traceDetail.allSpans || traceDetail.spans || [];
    const targetSpan = spansList.find((s) => s.id === selectedSpanId) || spansList[0];

    try {
      const res = await api.analyzeRootCause({
        traceId: traceDetail.trace.id || traceDetail.trace.traceId,
        spanId: targetSpan?.spanId || targetSpan?.id,
        errorMessage: traceDetail.trace.errorMessage || 'Elevated latency or unexpected model response formatting',
      });
      setRootCauseResult(res);
    } catch (err) {
      console.error('Root cause analysis failed', err);
    } finally {
      setAnalyzingRootCause(false);
    }
  };

  if (loading) {
    return (
      <div className="p-12 text-center text-slate-500">
        <Activity className="w-8 h-8 animate-spin text-indigo-500 mx-auto mb-3" />
        <p className="text-sm">Loading trace execution tree...</p>
      </div>
    );
  }

  if (!traceDetail) {
    return (
      <div className="p-8 text-center text-slate-500">
        <p>Trace not found.</p>
        <Link to="/traces" className="text-indigo-600 dark:text-indigo-400 text-xs mt-2 inline-block hover:underline">
          Return to Traces
        </Link>
      </div>
    );
  }

  const { trace } = traceDetail;
  const spansList = traceDetail.allSpans || traceDetail.spans || [];
  const activeSpan = spansList.find((s) => s.id === selectedSpanId) || spansList[0];

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Navigation & Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="space-y-1">
          <Link
            to="/traces"
            className="text-xs text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200 flex items-center gap-1.5 mb-1.5 transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            Back to All Traces
          </Link>
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-lg sm:text-xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2">
              Trace: <span className="font-mono text-indigo-600 dark:text-indigo-400">{trace.traceId || trace.id}</span>
            </h1>
            {trace.status === 'SUCCESS' ? (
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 dark:bg-emerald-500/10 text-emerald-700 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/20">
                <CheckCircle2 className="w-3.5 h-3.5" /> SUCCESS
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-50 dark:bg-rose-500/10 text-rose-700 dark:text-rose-400 border border-rose-200 dark:border-rose-500/20">
                <XCircle className="w-3.5 h-3.5" /> {trace.status}
              </span>
            )}
          </div>
        </div>

        {/* AI Root Cause CTA Button */}
        <button
          onClick={handleRunRootCauseAnalysis}
          disabled={analyzingRootCause}
          className="px-4 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-cyan-600 hover:from-indigo-500 hover:to-cyan-500 text-white text-xs font-semibold flex items-center gap-2 shadow-md shadow-indigo-500/20 transition-all self-start sm:self-auto cursor-pointer"
        >
          <Sparkles className={`w-4 h-4 ${analyzingRootCause ? 'animate-spin' : ''}`} />
          <span>{analyzingRootCause ? 'Analyzing with AI...' : 'Run AI Root Cause Analysis'}</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="p-4 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center gap-3 shadow-xs">
          <Clock className="w-5 h-5 text-indigo-500 dark:text-indigo-400" />
          <div>
            <div className="text-[10px] text-slate-500 dark:text-slate-400 uppercase font-semibold">Total Duration</div>
            <div className="text-sm sm:text-base font-bold font-mono text-slate-900 dark:text-slate-100">{formatLatency(trace.latencyMs)}</div>
          </div>
        </div>

        <div className="p-4 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center gap-3 shadow-xs">
          <Layers className="w-5 h-5 text-cyan-500 dark:text-cyan-400" />
          <div>
            <div className="text-[10px] text-slate-500 dark:text-slate-400 uppercase font-semibold">Total Spans</div>
            <div className="text-sm sm:text-base font-bold font-mono text-slate-900 dark:text-slate-100">{spansList.length} spans</div>
          </div>
        </div>

        <div className="p-4 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center gap-3 shadow-xs">
          <Cpu className="w-5 h-5 text-amber-500 dark:text-amber-400" />
          <div>
            <div className="text-[10px] text-slate-500 dark:text-slate-400 uppercase font-semibold">Total Tokens</div>
            <div className="text-sm sm:text-base font-bold font-mono text-slate-900 dark:text-slate-100">
              {formatNumber(trace.totalTokens)}
            </div>
          </div>
        </div>

        <div className="p-4 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center gap-3 shadow-xs">
          <Coins className="w-5 h-5 text-emerald-500 dark:text-emerald-400" />
          <div>
            <div className="text-[10px] text-slate-500 dark:text-slate-400 uppercase font-semibold">Estimated Cost</div>
            <div className="text-sm sm:text-base font-bold font-mono text-emerald-600 dark:text-emerald-400">
              {formatCurrency(trace.estimatedCost || trace.totalCost, 4)}
            </div>
          </div>
        </div>
      </div>

      {/* AI Root Cause Diagnostic Result Panel */}
      {rootCauseResult && (
        <div className="p-5 rounded-xl bg-gradient-to-br from-indigo-50 dark:from-indigo-950/40 via-white dark:via-slate-900 to-slate-50 dark:to-slate-900 border border-indigo-200 dark:border-indigo-500/30 shadow-md">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
              <h3 className="text-sm font-bold text-indigo-900 dark:text-indigo-200">AI Root Cause Diagnostic</h3>
            </div>
            <span
              className={`px-2.5 py-0.5 rounded text-[11px] font-bold ${
                rootCauseResult.severity === 'CRITICAL'
                  ? 'bg-rose-100 dark:bg-rose-500/20 text-rose-700 dark:text-rose-300 border border-rose-200 dark:border-rose-500/30'
                  : 'bg-amber-100 dark:bg-amber-500/20 text-amber-700 dark:text-amber-300 border border-amber-200 dark:border-amber-500/30'
              }`}
            >
              {rootCauseResult.severity || 'MEDIUM'} SEVERITY
            </span>
          </div>

          <p className="text-xs text-slate-800 dark:text-slate-200 leading-relaxed mb-4">
            {rootCauseResult.summary || rootCauseResult.primaryHypothesis}
          </p>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
            <div className="p-3 rounded-lg bg-slate-100 dark:bg-slate-950/70 border border-slate-200 dark:border-slate-800">
              <span className="font-semibold text-slate-700 dark:text-slate-300 uppercase tracking-wider text-[10px] block mb-1">
                Supporting Evidence & Trigger
              </span>
              <p className="text-slate-600 dark:text-slate-400 leading-relaxed font-mono text-[11px]">
                {rootCauseResult.supportingEvidence || 'Trace latency / error code captured in span tree.'}
              </p>
            </div>

            <div className="p-3 rounded-lg bg-slate-100 dark:bg-slate-950/70 border border-slate-200 dark:border-slate-800">
              <span className="font-semibold text-emerald-700 dark:text-emerald-400 uppercase tracking-wider text-[10px] block mb-1">
                Recommended Fix
              </span>
              <p className="text-slate-700 dark:text-slate-300 leading-relaxed">
                {rootCauseResult.suggestedFix || 'Review prompt length, check token budgets, or configure provider retry backoff.'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Waterfall Visualizer */}
      <div className="space-y-3">
        <h2 className="text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-2">
          <Activity className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> Execution Waterfall Timeline
        </h2>
        <WaterfallVisualizer
          rootNodes={traceDetail.waterfallTree || []}
          onSelectSpan={(span) => setSelectedSpanId(span.id)}
        />
      </div>

      {/* Span Details Inspector */}
      {activeSpan && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-4 sm:p-6 space-y-4 shadow-xs">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-4 gap-2">
            <div>
              <span className="text-[10px] font-semibold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider block">
                Selected Span Inspector
              </span>
              <h3 className="text-sm sm:text-base font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <span>{activeSpan.name}</span>
                <span className="text-xs px-2 py-0.5 rounded bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 font-mono">
                  {activeSpan.spanType}
                </span>
              </h3>
            </div>
            <div className="text-left sm:text-right text-xs space-y-0.5">
              <div className="text-slate-500 dark:text-slate-400">Duration: <span className="font-mono font-semibold text-slate-800 dark:text-slate-200">{formatLatency(activeSpan.durationMs || activeSpan.latencyMs)}</span></div>
              <div className="text-slate-500 dark:text-slate-400">Cost: <span className="font-mono font-semibold text-emerald-600 dark:text-emerald-400">{formatCurrency(activeSpan.cost, 4)}</span></div>
            </div>
          </div>

          {/* Input & Output */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                <FileText className="w-3.5 h-3.5 text-indigo-500" /> Span Input
              </label>
              <div className="bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg p-3 text-xs font-mono text-slate-800 dark:text-slate-300 max-h-60 overflow-y-auto whitespace-pre-wrap">
                {activeSpan.inputJson || activeSpan.input || '{"query": "input parameters"}'}
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                <FileText className="w-3.5 h-3.5 text-emerald-500" /> Span Output
              </label>
              <div className="bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg p-3 text-xs font-mono text-slate-800 dark:text-slate-300 max-h-60 overflow-y-auto whitespace-pre-wrap">
                {activeSpan.outputJson || activeSpan.output || '{"status": "completed successfully"}'}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
