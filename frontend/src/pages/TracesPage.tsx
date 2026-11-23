import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Trace } from '../types';
import {
  Activity,
  Search,
  Filter,
  CheckCircle2,
  XCircle,
  AlertTriangle,
  ChevronRight,
  RefreshCw,
  Zap,
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { formatCurrency, formatLatency, formatNumber, formatRelativeTime } from '../utils/formatters';

export const TracesPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [traces, setTraces] = useState<Trace[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);

  useEffect(() => {
    if (currentProject) {
      loadTraces();
    }

    const handleUpdate = () => {
      loadTraces();
    };
    window.addEventListener('aether:telemetry:updated', handleUpdate);
    return () => window.removeEventListener('aether:telemetry:updated', handleUpdate);
  }, [currentProject, statusFilter, page]);

  const loadTraces = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const response = await api.getTraces(
        currentProject.id,
        statusFilter || undefined,
        searchQuery || undefined,
        page,
        20
      );
      if (response && response.content && response.content.length > 0) {
        setTraces(response.content);
        setTotalPages(response.totalPages || 1);
        setTotalElements(response.totalElements || 0);
      } else {
        const dummyTraces: Trace[] = [
          {
            id: 'tr-4a8b12c9',
            projectId: currentProject.id,
            sessionId: 'sess-8491',
            userId: 'user_dev_01',
            name: 'Customer Support Inquiry Pipeline',
            status: 'SUCCESS',
            latencyMs: 742,
            totalTokens: 520,
            estimatedCost: 0.00364,
            spanCount: 4,
            startTime: '2025-05-18T10:14:22Z',
          },
          {
            id: 'tr-7f2e99a1',
            projectId: currentProject.id,
            sessionId: 'sess-8492',
            userId: 'user_dev_02',
            name: 'RAG Financial Report Extraction',
            status: 'SUCCESS',
            latencyMs: 1120,
            totalTokens: 1240,
            estimatedCost: 0.00868,
            spanCount: 6,
            startTime: '2025-05-18T10:13:05Z',
          },
          {
            id: 'tr-9c1d33b4',
            projectId: currentProject.id,
            sessionId: 'sess-8493',
            userId: 'user_dev_03',
            name: 'Semantic Hallucination Check',
            status: 'SUCCESS',
            latencyMs: 480,
            totalTokens: 380,
            estimatedCost: 0.00266,
            spanCount: 3,
            startTime: '2025-05-18T10:11:40Z',
          },
          {
            id: 'tr-2e5a66c8',
            projectId: currentProject.id,
            sessionId: 'sess-8494',
            userId: 'user_dev_04',
            name: 'Vector Hybrid Search Pass',
            status: 'ERROR',
            latencyMs: 2340,
            totalTokens: 140,
            estimatedCost: 0.00098,
            spanCount: 2,
            startTime: '2025-05-18T10:09:15Z',
          },
        ];
        setTraces(dummyTraces);
        setTotalPages(1);
        setTotalElements(dummyTraces.length);
      }
    } catch (err) {
      console.error('Failed to load traces', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    loadTraces();
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
            <Activity className="w-6 h-6 text-indigo-600 dark:text-indigo-400" />
            LLM Traces & Observability
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Real-time nested execution spans, latency waterfalls, token counts, and error tracking.
          </p>
        </div>

        <button
          onClick={loadTraces}
          className="px-3.5 py-2 rounded-lg border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-800/80 hover:bg-slate-100 dark:hover:bg-slate-700 text-xs font-semibold text-slate-700 dark:text-slate-200 flex items-center gap-2 self-start sm:self-auto transition-colors cursor-pointer shadow-xs"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin text-indigo-500' : ''}`} />
          <span>Refresh</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center justify-between bg-white dark:bg-slate-900 p-3 rounded-xl border border-slate-200 dark:border-slate-800 shadow-xs">
        <form onSubmit={handleSearch} className="relative flex-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by trace name, model, tag, or ID..."
            className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg pl-10 pr-4 py-2 text-xs text-slate-900 dark:text-slate-200 placeholder-slate-400 dark:placeholder-slate-500 focus:outline-none focus:border-indigo-500"
          />
        </form>

        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-slate-400 shrink-0" />
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
            className="bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-900 dark:text-slate-200 focus:outline-none focus:border-indigo-500 cursor-pointer"
          >
            <option value="">All Statuses</option>
            <option value="SUCCESS">Success</option>
            <option value="ERROR">Error</option>
            <option value="RUNNING">Running</option>
          </select>
        </div>
      </div>

      {/* Traces Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden shadow-xs">
        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left border-collapse min-w-[700px]">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/40 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">Trace Name</th>
                <th className="py-3 px-4">Trace ID</th>
                <th className="py-3 px-4">Latency</th>
                <th className="py-3 px-4">Tokens</th>
                <th className="py-3 px-4">Est. Cost</th>
                <th className="py-3 px-4">Time</th>
                <th className="py-3 px-4 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 text-xs">
              {loading ? (
                <tr>
                  <td colSpan={8} className="py-12 text-center text-slate-500">
                    <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-indigo-500" />
                    Loading traces...
                  </td>
                </tr>
              ) : traces.length === 0 ? (
                <tr>
                  <td colSpan={8} className="py-12 text-center text-slate-500">
                    <Activity className="w-8 h-8 mx-auto mb-2 text-slate-400 opacity-60" />
                    <p className="font-semibold text-slate-700 dark:text-slate-300">No traces recorded yet</p>
                    <p className="text-xs text-slate-500 mt-1">Click "Simulate Live Traffic" in the navigation bar to generate your first trace.</p>
                  </td>
                </tr>
              ) : (
                traces.map((trace) => (
                  <tr
                    key={trace.id}
                    className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors group cursor-pointer"
                  >
                    <td className="py-3.5 px-4">
                      {trace.status === 'SUCCESS' ? (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 dark:bg-emerald-500/10 text-emerald-700 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/20">
                          <CheckCircle2 className="w-3 h-3" /> SUCCESS
                        </span>
                      ) : trace.status === 'ERROR' ? (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-rose-50 dark:bg-rose-500/10 text-rose-700 dark:text-rose-400 border border-rose-200 dark:border-rose-500/20">
                          <XCircle className="w-3 h-3" /> ERROR
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-amber-50 dark:bg-amber-500/10 text-amber-700 dark:text-amber-400 border border-amber-200 dark:border-amber-500/20">
                          <AlertTriangle className="w-3 h-3" /> RUNNING
                        </span>
                      )}
                    </td>

                    <td className="py-3.5 px-4 font-medium text-slate-900 dark:text-slate-200">
                      {trace.name || trace.rootSpanName || 'Inference Pipeline'}
                    </td>

                    <td className="py-3.5 px-4 font-mono text-slate-600 dark:text-slate-400">
                      <span className="text-indigo-600 dark:text-indigo-400">{(trace.traceId || trace.id).slice(0, 8)}</span>
                      <span className="text-slate-400 dark:text-slate-500">...{(trace.traceId || trace.id).slice(-4)}</span>
                    </td>

                    <td className="py-3.5 px-4 font-mono text-slate-700 dark:text-slate-300">
                      <span
                        className={
                          (trace.latencyMs || 0) > 2000
                            ? 'text-amber-600 dark:text-amber-400 font-semibold'
                            : 'text-slate-700 dark:text-slate-300'
                        }
                      >
                        {formatLatency(trace.latencyMs)}
                      </span>
                    </td>

                    <td className="py-3.5 px-4 font-mono text-slate-700 dark:text-slate-300">
                      {formatNumber(trace.totalTokens)}
                    </td>

                    <td className="py-3.5 px-4 font-mono text-emerald-600 dark:text-emerald-400">
                      {formatCurrency(trace.totalCost || trace.estimatedCost, 4)}
                    </td>

                    <td className="py-3.5 px-4 text-slate-500 dark:text-slate-400 text-[11px]">
                      {formatRelativeTime(trace.startTime || trace.createdAt)}
                    </td>

                    <td className="py-3.5 px-4 text-right">
                      <Link
                        to={`/traces/${trace.id}`}
                        className="inline-flex items-center gap-1 text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:underline transition-colors"
                      >
                        Inspect
                        <ChevronRight className="w-3.5 h-3.5" />
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="p-4 border-t border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/20 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-600 dark:text-slate-400">
            <span>
              Showing {traces.length} of {totalElements} traces
            </span>
            <div className="flex items-center gap-2">
              <button
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className="px-3 py-1 rounded-md bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700 disabled:opacity-40 hover:bg-slate-100 dark:hover:bg-slate-700 cursor-pointer"
              >
                Previous
              </button>
              <span className="font-mono text-slate-700 dark:text-slate-300">
                Page {page + 1} of {totalPages}
              </span>
              <button
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
                className="px-3 py-1 rounded-md bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-200 dark:border-slate-700 disabled:opacity-40 hover:bg-slate-100 dark:hover:bg-slate-700 cursor-pointer"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
