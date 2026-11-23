import React, { useState } from 'react';
import { SpanNode, Span } from '../../types';
import { ChevronRight, ChevronDown, CheckCircle2, AlertCircle, Clock, Zap, Cpu, Database, Wrench } from 'lucide-react';
import { formatLatency } from '../../utils/formatters';

interface WaterfallVisualizerProps {
  rootNodes: SpanNode[];
  onSelectSpan?: (span: Span) => void;
}

export const WaterfallVisualizer: React.FC<WaterfallVisualizerProps> = ({ rootNodes, onSelectSpan }) => {
  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden shadow-xs">
      {/* Header Timeline Bar */}
      <div className="grid grid-cols-12 bg-slate-50 dark:bg-slate-900/90 border-b border-slate-200 dark:border-slate-800 px-4 py-2.5 text-[11px] font-semibold text-slate-500 dark:text-slate-400">
        <div className="col-span-6 sm:col-span-5">SPAN EXECUTION TREE</div>
        <div className="col-span-2 sm:col-span-1 text-center">TYPE</div>
        <div className="col-span-2 sm:col-span-1 text-right">LATENCY</div>
        <div className="hidden sm:block sm:col-span-5 text-right px-2">TIMELINE (0ms → 100%)</div>
        <div className="sm:hidden col-span-2 text-right">TIMELINE</div>
      </div>

      <div className="divide-y divide-slate-100 dark:divide-slate-800/40">
        {rootNodes && rootNodes.length > 0 ? (
          rootNodes.map((node) => (
            <SpanRow key={node.span.id} node={node} depth={0} onSelect={onSelectSpan} />
          ))
        ) : (
          <div className="p-8 text-center text-xs text-slate-400">No span execution nodes recorded.</div>
        )}
      </div>
    </div>
  );
};

const SpanRow: React.FC<{ node: SpanNode; depth: number; onSelect?: (span: Span) => void }> = ({
  node,
  depth,
  onSelect,
}) => {
  const [expanded, setExpanded] = useState(true);
  const { span, relativeStartPercent = 0, relativeDurationPercent = 100, children } = node;
  const hasChildren = children && children.length > 0;

  const getTypeBadge = (type?: string) => {
    switch (type) {
      case 'LLM':
        return <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-indigo-50 dark:bg-indigo-500/15 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30">LLM</span>;
      case 'RETRIEVAL':
        return <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-emerald-50 dark:bg-emerald-500/15 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-500/30">RAG</span>;
      case 'TOOL':
        return <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-amber-50 dark:bg-amber-500/15 text-amber-700 dark:text-amber-300 border border-amber-200 dark:border-amber-500/30">TOOL</span>;
      case 'GUARDRAIL':
        return <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-cyan-50 dark:bg-cyan-500/15 text-cyan-700 dark:text-cyan-300 border border-cyan-200 dark:border-cyan-500/30">GUARD</span>;
      default:
        return <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-purple-50 dark:bg-purple-500/15 text-purple-700 dark:text-purple-300 border border-purple-200 dark:border-purple-500/30">{type || 'SPAN'}</span>;
    }
  };

  const getBarColor = (type?: string, status?: string) => {
    if (status === 'ERROR') return 'bg-rose-500 shadow-rose-500/20';
    switch (type) {
      case 'LLM':
        return 'bg-indigo-500 shadow-indigo-500/20';
      case 'RETRIEVAL':
        return 'bg-emerald-500 shadow-emerald-500/20';
      case 'TOOL':
        return 'bg-amber-500 shadow-amber-500/20';
      case 'GUARDRAIL':
        return 'bg-cyan-500 shadow-cyan-500/20';
      default:
        return 'bg-purple-500 shadow-purple-500/20';
    }
  };

  return (
    <>
      <div
        onClick={() => onSelect && onSelect(span)}
        className="grid grid-cols-12 px-3 sm:px-4 py-2.5 hover:bg-slate-50 dark:hover:bg-slate-800/40 cursor-pointer items-center transition-colors text-xs"
      >
        {/* Name with indentation */}
        <div className="col-span-6 sm:col-span-5 flex items-center gap-1.5 truncate" style={{ paddingLeft: `${Math.min(depth * 14, 48)}px` }}>
          {hasChildren ? (
            <button
              onClick={(e) => {
                e.stopPropagation();
                setExpanded(!expanded);
              }}
              className="p-0.5 text-slate-400 hover:text-slate-900 dark:hover:text-white shrink-0"
            >
              {expanded ? <ChevronDown className="w-3.5 h-3.5" /> : <ChevronRight className="w-3.5 h-3.5" />}
            </button>
          ) : (
            <span className="w-4 shrink-0" />
          )}

          <span className="font-medium text-slate-800 dark:text-slate-200 truncate" title={span.name}>
            {span.name}
          </span>
        </div>

        {/* Span Type */}
        <div className="col-span-2 sm:col-span-1 text-center">
          {getTypeBadge(span.spanType)}
        </div>

        {/* Latency */}
        <div className="col-span-2 sm:col-span-1 text-right font-mono text-slate-600 dark:text-slate-400 text-[11px]">
          {formatLatency(span.latencyMs || span.durationMs || 10)}
        </div>

        {/* Waterfall Bar */}
        <div className="col-span-2 sm:col-span-5 px-1 sm:px-2 flex items-center">
          <div className="w-full bg-slate-100 dark:bg-slate-950/60 rounded-full h-2 relative overflow-hidden">
            <div
              className={`h-full rounded-full transition-all ${getBarColor(span.spanType, span.status)}`}
              style={{
                marginLeft: `${Math.max(0, Math.min(95, relativeStartPercent))}%`,
                width: `${Math.max(4, Math.min(100 - relativeStartPercent, relativeDurationPercent))}%`,
              }}
            />
          </div>
        </div>
      </div>

      {/* Children Spans */}
      {hasChildren && expanded && (
        <div className="space-y-0">
          {children.map((child) => (
            <SpanRow key={child.span.id} node={child} depth={depth + 1} onSelect={onSelect} />
          ))}
        </div>
      )}
    </>
  );
};
