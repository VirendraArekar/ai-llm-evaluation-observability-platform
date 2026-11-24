import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Evaluation, EvaluationMetric } from '../types';
import { formatPercentage, formatRelativeTime } from '../utils/formatters';
import {
  CheckCircle2,
  XCircle,
  AlertTriangle,
  Sparkles,
  Scale,
  ShieldAlert,
  Search,
  Filter,
  Layers,
  ChevronRight,
  Plus,
} from 'lucide-react';

export const EvaluationsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [evaluations, setEvaluations] = useState<Evaluation[]>([]);
  const [metrics, setMetrics] = useState<EvaluationMetric[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);

  // Judge Modal state
  const [showJudgeModal, setShowJudgeModal] = useState<boolean>(false);
  const [judgeInput, setJudgeInput] = useState<string>('What is the default port for PostgreSQL?');
  const [judgeOutput, setJudgeOutput] = useState<string>('The default port for PostgreSQL is 5432.');
  const [judgeContext, setJudgeContext] = useState<string>('PostgreSQL listens on TCP port 5432 by default.');
  const [judgeMetric, setJudgeMetric] = useState<string>('answer_correctness');
  const [runningJudge, setRunningJudge] = useState<boolean>(false);
  const [judgeResult, setJudgeResult] = useState<any>(null);

  // Hallucination Detector Modal state
  const [showHallucinationModal, setShowHallucinationModal] = useState<boolean>(false);
  const [hallucinationOutput, setHallucinationOutput] = useState<string>(
    'Apollo 11 landed on the moon on July 20, 1969. The crew consisted of Neil Armstrong, Buzz Aldrin, and Michael Collins. Armstrong played golf on the lunar surface during the mission.'
  );
  const [hallucinationContext, setHallucinationContext] = useState<string>(
    'Apollo 11 was the American spaceflight that first landed humans on the Moon. Commander Neil Armstrong and Lunar Module Pilot Buzz Aldrin landed the Apollo Lunar Module Eagle on July 20, 1969. Michael Collins flew the command module Columbia in lunar orbit. Golf was played on the moon during Apollo 14 by Alan Shepard, not Apollo 11.'
  );
  const [detectingHallucination, setDetectingHallucination] = useState<boolean>(false);
  const [hallucinationResult, setHallucinationResult] = useState<any>(null);

  useEffect(() => {
    if (currentProject) {
      loadEvaluations();
      loadMetrics();
    }

    const handleUpdate = () => {
      loadEvaluations();
    };
    window.addEventListener('aether:telemetry:updated', handleUpdate);
    return () => window.removeEventListener('aether:telemetry:updated', handleUpdate);
  }, [currentProject, page]);

  const loadEvaluations = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const data = await api.getEvaluations(currentProject.id, page, 20);
      if (data && data.content && data.content.length > 0) {
        setEvaluations(data.content);
        setTotalPages(data.totalPages || 1);
        setTotalElements(data.totalElements || 0);
      } else {
        const dummyEvals: any[] = [
          {
            id: 'ev-1',
            projectId: currentProject.id,
            traceId: 'tr-4a8b12c9',
            metricName: 'Answer Relevance & Faithfulness',
            score: 0.96,
            passFail: true,
            explanation: 'The generated answer strictly addresses the customer warranty inquiry using provided context without hallucinating external clauses.',
            evidence: 'Source Context: "Hardware warranty covers parts and labor for 24 months."',
            category: 'LLM_JUDGE',
            judgeModel: 'gpt-4o',
            createdAt: '2025-05-18T10:14:30Z',
          },
          {
            id: 'ev-2',
            projectId: currentProject.id,
            traceId: 'tr-7f2e99a1',
            metricName: 'Context Precision & Recall',
            score: 0.94,
            passFail: true,
            explanation: 'Relevant 10-K financial paragraphs were retrieved in the top 2 ranked chunks.',
            evidence: 'Retrieved Chunks [1, 2] contained exact operating income metrics.',
            category: 'RAG_TRIAD',
            judgeModel: 'claude-3-5-sonnet',
            createdAt: '2025-05-18T10:13:10Z',
          },
          {
            id: 'ev-3',
            projectId: currentProject.id,
            traceId: 'tr-9c1d33b4',
            metricName: 'Hallucination Risk Score',
            score: 0.98,
            passFail: true,
            explanation: '0 unsupported factual claims detected across 4 generated sentences.',
            evidence: 'Claims verified against knowledge base documentation.',
            category: 'HALLUCINATION',
            judgeModel: 'gemini-1.5-pro',
            createdAt: '2025-05-18T10:11:45Z',
          },
        ];
        setEvaluations(dummyEvals);
        setTotalPages(1);
        setTotalElements(dummyEvals.length);
      }
    } catch (err) {
      console.error('Failed to load evaluations', err);
    } finally {
      setLoading(false);
    }
  };

  const loadMetrics = async () => {
    if (!currentProject) return;
    try {
      const data = await api.getMetrics(currentProject.id);
      if (data && data.length > 0) {
        setMetrics(data);
      } else {
        setMetrics([
          { id: 'm-1', name: 'Answer Relevance', code: 'RELEVANCE', category: 'LLM_JUDGE', description: 'Assesses whether the response directly addresses user question.' },
          { id: 'm-2', name: 'Faithfulness', code: 'FAITHFULNESS', category: 'LLM_JUDGE', description: 'Assesses whether all claims are grounded in context.' },
          { id: 'm-3', name: 'Hallucination Risk', code: 'HALLUCINATION', category: 'HALLUCINATION', description: 'Flags fabricated or ungrounded assertions.' },
          { id: 'm-4', name: 'Context Precision', code: 'CONTEXT_PRECISION', category: 'RAG_TRIAD', description: 'Measures signal-to-noise ratio in retrieved documents.' },
        ] as any);
      }
    } catch (err) {
      console.error('Failed to load metrics', err);
    }
  };

  const handleRunJudge = async () => {
    if (!currentProject) return;
    setRunningJudge(true);
    setJudgeResult(null);
    try {
      const res = await api.runJudge({
        projectId: currentProject.id,
        metric: judgeMetric,
        input: judgeInput,
        output: judgeOutput,
        context: judgeContext,
      });
      setJudgeResult(res);
      loadEvaluations();
    } catch (err) {
      console.error('Judge execution failed', err);
    } finally {
      setRunningJudge(false);
    }
  };

  const handleDetectHallucination = async () => {
    if (!currentProject) return;
    setDetectingHallucination(true);
    setHallucinationResult(null);
    try {
      const res = await api.detectHallucination({
        projectId: currentProject.id,
        output: hallucinationOutput,
        context: hallucinationContext,
      });
      setHallucinationResult(res);
    } catch (err) {
      console.error('Hallucination detection failed', err);
    } finally {
      setDetectingHallucination(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
            <CheckCircle2 className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
            AI Evaluation & Hallucination Engine
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Automated LLM-as-a-Judge grading, sentence-level claim verification, and rubric-driven quality gates.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <button
            onClick={() => setShowHallucinationModal(true)}
            className="px-3.5 py-2 rounded-lg border border-amber-500/30 bg-amber-500/10 hover:bg-amber-500/20 text-amber-600 dark:text-amber-300 text-xs font-semibold flex items-center gap-2 transition-colors"
          >
            <ShieldAlert className="w-4 h-4" />
            Detect Hallucinations
          </button>
          <button
            onClick={() => setShowJudgeModal(true)}
            className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold flex items-center gap-2 transition-colors shadow-sm shadow-indigo-600/30"
          >
            <Scale className="w-4 h-4" />
            Run LLM Judge
          </button>
        </div>
      </div>

      {/* Metrics Summary Strip */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {metrics.slice(0, 4).map((m) => (
          <div key={m.id} className="p-4 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
            <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">{m.name}</div>
            <div className="text-base font-bold text-slate-900 dark:text-slate-100 flex items-center justify-between">
              <span className="font-mono">{m.passingThreshold ? `Threshold ≥ ${m.passingThreshold}` : 'Active'}</span>
              <span className="text-[10px] px-2 py-0.5 rounded bg-indigo-50 dark:bg-indigo-500/15 text-indigo-600 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-500/20 font-sans">
                {m.type}
              </span>
            </div>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-1">{m.description}</p>
          </div>
        ))}
      </div>

      {/* Evaluations Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden shadow-sm">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <h2 className="text-sm font-bold text-slate-800 dark:text-slate-200 uppercase tracking-wider flex items-center gap-2">
            <Layers className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> Evaluation Records ({totalElements})
          </h2>
        </div>

        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left border-collapse min-w-[750px]">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/40 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">Metric</th>
                <th className="py-3 px-4">Score</th>
                <th className="py-3 px-4">Judge Model</th>
                <th className="py-3 px-4">Explanation</th>
                <th className="py-3 px-4">Evaluated At</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 dark:divide-slate-800/60 text-xs">
              {loading ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-slate-400 dark:text-slate-500">
                    Loading evaluations...
                  </td>
                </tr>
              ) : evaluations.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-slate-400 dark:text-slate-500">
                    No evaluations executed yet. Run an LLM Judge or Hallucination check above!
                  </td>
                </tr>
              ) : (
                evaluations.map((ev) => (
                  <tr key={ev.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3.5 px-4">
                      {ev.passed ? (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 dark:bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/20">
                          <CheckCircle2 className="w-3 h-3" /> PASSED
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-rose-50 dark:bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-200 dark:border-rose-500/20">
                          <XCircle className="w-3 h-3" /> FAILED
                        </span>
                      )}
                    </td>
                    <td className="py-3.5 px-4 font-semibold text-slate-800 dark:text-slate-200">
                      {ev.metricName || 'relevance'}
                    </td>
                    <td className="py-3.5 px-4 font-mono font-bold">
                      <span className={(ev.score || 0) >= 0.7 ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400'}>
                        {Number(ev.score || 0).toFixed(2)}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 font-mono text-slate-500 dark:text-slate-400">
                      {ev.judgeModel || 'gpt-4o-mini'}
                    </td>
                    <td className="py-3.5 px-4 text-slate-600 dark:text-slate-300 max-w-md truncate">
                      {ev.reasoning || ev.explanation || 'Graded against rubric criteria.'}
                    </td>
                    <td className="py-3.5 px-4 text-slate-500 dark:text-slate-400">
                      {formatRelativeTime(ev.createdAt)}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* LLM-as-a-Judge Modal */}
      {showJudgeModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-2xl w-full p-5 sm:p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <Scale className="w-5 h-5 text-indigo-500 dark:text-indigo-400" />
                <h3 className="font-bold text-slate-900 dark:text-slate-100">Run LLM-as-a-Judge Evaluation</h3>
              </div>
              <button onClick={() => setShowJudgeModal(false)} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 text-xs">
                Close
              </button>
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Evaluation Metric</label>
                <select
                  value={judgeMetric}
                  onChange={(e) => setJudgeMetric(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                >
                  <option value="answer_correctness">Answer Correctness</option>
                  <option value="faithfulness">Faithfulness</option>
                  <option value="context_relevance">Context Relevance</option>
                  <option value="toxicity">Toxicity & Safety</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">User Prompt / Question</label>
                <input
                  type="text"
                  value={judgeInput}
                  onChange={(e) => setJudgeInput(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Model Generated Response</label>
                <textarea
                  rows={3}
                  value={judgeOutput}
                  onChange={(e) => setJudgeOutput(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg p-3 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Reference Context / Ground Truth</label>
                <textarea
                  rows={2}
                  value={judgeContext}
                  onChange={(e) => setJudgeContext(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg p-3 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>
            </div>

            <button
              onClick={handleRunJudge}
              disabled={runningJudge}
              className="w-full py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-40 text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-sm"
            >
              <Sparkles className={`w-4 h-4 ${runningJudge ? 'animate-spin' : ''}`} />
              {runningJudge ? 'Judge Evaluating Output...' : 'Execute LLM Judge'}
            </button>

            {judgeResult && (
              <div className="p-4 bg-slate-50 dark:bg-slate-950 rounded-xl border border-slate-200 dark:border-slate-800 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-200">Score Result:</span>
                  <span className={`text-sm font-bold font-mono ${judgeResult.score >= 0.7 ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400'}`}>
                    {Number(judgeResult.score).toFixed(2)} / 1.00 ({judgeResult.passed ? 'PASSED' : 'FAILED'})
                  </span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">{judgeResult.explanation || judgeResult.reasoning}</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Hallucination Detector Modal */}
      {showHallucinationModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-2xl w-full p-5 sm:p-6 space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto custom-scrollbar">
            <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-3">
              <div className="flex items-center gap-2">
                <ShieldAlert className="w-5 h-5 text-amber-500" />
                <h3 className="font-bold text-slate-900 dark:text-slate-100">Intelligent Sentence-Level Hallucination Detector</h3>
              </div>
              <button onClick={() => setShowHallucinationModal(false)} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 text-xs">
                Close
              </button>
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Generated Text to Audit</label>
                <textarea
                  rows={3}
                  value={hallucinationOutput}
                  onChange={(e) => setHallucinationOutput(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg p-3 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Source Knowledge Context</label>
                <textarea
                  rows={4}
                  value={hallucinationContext}
                  onChange={(e) => setHallucinationContext(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg p-3 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>
            </div>

            <button
              onClick={handleDetectHallucination}
              disabled={detectingHallucination}
              className="w-full py-2.5 rounded-lg bg-amber-600 hover:bg-amber-500 disabled:opacity-40 text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-sm"
            >
              <Sparkles className={`w-4 h-4 ${detectingHallucination ? 'animate-spin' : ''}`} />
              {detectingHallucination ? 'Auditing Claims Sentence-by-Sentence...' : 'Verify Claims Against Context'}
            </button>

            {hallucinationResult && (
              <div className="p-4 bg-slate-50 dark:bg-slate-950 rounded-xl border border-slate-200 dark:border-slate-800 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-300">Hallucination Risk Score</span>
                  <span className="text-sm font-bold font-mono text-amber-600 dark:text-amber-400">
                    {formatPercentage(hallucinationResult.hallucinationRiskScore)} Risk
                  </span>
                </div>

                <div className="space-y-2">
                  <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Sentence Verification Claims</div>
                  {hallucinationResult.claims?.map((claim: any, idx: number) => (
                    <div
                      key={idx}
                      className={`p-3 rounded-lg border text-xs space-y-1 ${
                        claim.classification === 'SUPPORTED'
                          ? 'bg-emerald-50/80 dark:bg-emerald-950/20 border-emerald-200 dark:border-emerald-500/30'
                          : claim.classification === 'UNSUPPORTED'
                          ? 'bg-amber-50/80 dark:bg-amber-950/20 border-amber-200 dark:border-amber-500/30'
                          : 'bg-rose-50/80 dark:bg-rose-950/20 border-rose-200 dark:border-rose-500/30'
                      }`}
                    >
                      <div className="flex justify-between items-center">
                        <span className="font-semibold text-slate-800 dark:text-slate-200">Claim {idx + 1}</span>
                        <span
                          className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                            claim.classification === 'SUPPORTED'
                              ? 'bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-300'
                              : claim.classification === 'UNSUPPORTED'
                              ? 'bg-amber-100 dark:bg-amber-500/20 text-amber-700 dark:text-amber-300'
                              : 'bg-rose-100 dark:bg-rose-500/20 text-rose-700 dark:text-rose-300'
                          }`}
                        >
                          {claim.classification}
                        </span>
                      </div>
                      <p className="text-slate-700 dark:text-slate-300 font-mono text-[11px]">"{claim.statement}"</p>
                      {claim.evidence && (
                        <p className="text-[10px] text-slate-500 dark:text-slate-400 italic">Evidence: {claim.evidence}</p>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
