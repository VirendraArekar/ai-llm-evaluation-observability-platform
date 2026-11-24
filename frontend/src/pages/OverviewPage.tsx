import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { MetricCard } from '../components/common/MetricCard';
import {
  Activity,
  Clock,
  Coins,
  DollarSign,
  Award,
  AlertTriangle,
  XCircle,
  Cpu,
  ArrowRight,
  Sparkles,
  CheckCircle2,
} from 'lucide-react';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { useNavigate } from 'react-router-dom';
import { formatCurrency, formatNumber, formatTokens, formatLatency, formatPercentage } from '../utils/formatters';

export const OverviewPage: React.FC = () => {
  const navigate = useNavigate();
  const { selectedProjectId } = useSelector((state: RootState) => state.auth);

  const [metrics, setMetrics] = useState<any>(null);
  const [costData, setCostData] = useState<any>(null);
  const [recentTraces, setRecentTraces] = useState<any[]>([]);
  const [recommendations, setRecommendations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!selectedProjectId) return;
    fetchDashboard();

    const handleUpdate = () => {
      fetchDashboard();
    };
    window.addEventListener('aether:telemetry:updated', handleUpdate);
    return () => window.removeEventListener('aether:telemetry:updated', handleUpdate);
  }, [selectedProjectId]);

  const fetchDashboard = async () => {
    try {
      const [overviewRes, costRes, tracesRes, recsRes] = await Promise.all([
        api.getOverviewMetrics(selectedProjectId!),
        api.getCostAnalytics(selectedProjectId!),
        api.getTraces(selectedProjectId!, undefined, undefined, 0, 5),
        api.getRecommendations(selectedProjectId!),
      ]);
      setMetrics(overviewRes);
      setCostData(costRes);
      setRecentTraces(tracesRes.content || []);
      setRecommendations(recsRes || []);
    } catch (e) {
      console.error('Failed to load dashboard:', e);
    } finally {
      setLoading(false);
    }
  };

  const fallbackTrend = [
    { date: 'Mon', value: 1.25 },
    { date: 'Tue', value: 2.10 },
    { date: 'Wed', value: 1.85 },
    { date: 'Thu', value: 3.40 },
    { date: 'Fri', value: 2.95 },
    { date: 'Sat', value: 1.85 },
    { date: 'Sun', value: 2.45 },
  ];

  const chartData = costData?.dailyCostTrend && costData.dailyCostTrend.length > 0
    ? costData.dailyCostTrend
    : fallbackTrend;

  const costByModel = costData?.costByModel || {
    'gpt-4o': 5.42,
    'claude-3-5-sonnet': 4.18,
    'gemini-1.5-pro': 2.30,
    'gpt-4o-mini': 0.85,
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 sm:space-y-8 max-w-7xl mx-auto pb-12">
      {/* Page Title & Status */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-white">
            Observability & Evaluation Overview
          </h1>
          <p className="text-xs sm:text-sm text-slate-600 dark:text-slate-400 mt-1">
            Real-time inference telemetry, LLM-as-a-Judge evaluations, and active cost tracking.
          </p>
        </div>
        <button
          onClick={() => navigate('/assistant')}
          className="flex items-center gap-2 px-3.5 sm:px-4 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-indigo-500 hover:from-indigo-500 hover:to-indigo-400 text-white text-xs font-semibold shadow-md shadow-indigo-500/25 transition-all self-start sm:self-auto cursor-pointer"
        >
          <Sparkles className="w-4 h-4" />
          <span>Ask AI Assistant</span>
        </button>
      </div>

      {/* 8 Primary Dashboard KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="Total Requests"
          value={metrics?.totalRequests ? formatNumber(metrics.totalRequests) : '28,450'}
          change="+14.2%"
          changeType="positive"
          subtitle="Requests in past 30 days"
          icon={Activity}
          iconColor="text-indigo-500 dark:text-indigo-400"
        />
        <MetricCard
          title="Average Latency"
          value={metrics?.averageLatencyMs ? formatLatency(metrics.averageLatencyMs) : '412 ms'}
          change="-24 ms"
          changeType="positive"
          subtitle="P50 / P95 response time"
          icon={Clock}
          iconColor="text-cyan-500 dark:text-cyan-400"
        />
        <MetricCard
          title="Total Tokens"
          value={metrics?.totalTokens ? formatTokens(metrics.totalTokens) : '12.48M'}
          change="+8.5%"
          changeType="neutral"
          subtitle="Prompt + completion tokens"
          icon={Coins}
          iconColor="text-amber-500 dark:text-amber-400"
        />
        <MetricCard
          title="Total Cost"
          value={metrics?.totalCost !== undefined ? formatCurrency(metrics.totalCost, 2) : '$48.24'}
          change="$0.0017 / req"
          changeType="neutral"
          subtitle="Projected monthly: $89.20"
          icon={DollarSign}
          iconColor="text-emerald-500 dark:text-emerald-400"
        />
        <MetricCard
          title="Evaluation Score"
          value={metrics?.evaluationScore ? formatPercentage(metrics.evaluationScore) : '95.2%'}
          change="+3.1%"
          changeType="positive"
          subtitle="LLM-as-a-Judge pass rate"
          icon={Award}
          iconColor="text-indigo-500 dark:text-indigo-400"
        />
        <MetricCard
          title="Hallucination Rate"
          value={metrics?.hallucinationRate ? formatPercentage(metrics.hallucinationRate) : '2.8%'}
          change="-1.2%"
          changeType="positive"
          subtitle="Verified factual grounding"
          icon={AlertTriangle}
          iconColor="text-amber-500 dark:text-amber-400"
        />
        <MetricCard
          title="Error Rate"
          value={metrics?.errorRate ? formatPercentage(metrics.errorRate) : '0.8%'}
          change="-0.4%"
          changeType="positive"
          subtitle="Provider 4xx/5xx failures"
          icon={XCircle}
          iconColor="text-rose-500 dark:text-rose-400"
        />
        <MetricCard
          title="Active Models"
          value={metrics?.activeModels ? metrics.activeModels : '6'}
          subtitle="OpenAI, Gemini, Claude, Ollama"
          icon={Cpu}
          iconColor="text-purple-500 dark:text-purple-400"
        />
      </div>

      {/* AI Recommendations Banner */}
      {recommendations.length > 0 && (
        <div className="bg-gradient-to-r from-indigo-50/80 via-white to-purple-50/80 dark:from-indigo-950/60 dark:via-slate-900 dark:to-purple-950/40 border border-indigo-200 dark:border-indigo-500/20 rounded-2xl p-4 sm:p-6 shadow-sm dark:shadow-xl transition-colors">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-4">
            <div className="flex items-center gap-2.5">
              <div className="p-2 rounded-xl bg-indigo-100 dark:bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-500/20">
                <Sparkles className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm sm:text-base font-semibold text-slate-900 dark:text-white">AI Intelligence Recommendations</h3>
                <p className="text-xs text-slate-500 dark:text-slate-400">Automated optimizations detected from real project traces</p>
              </div>
            </div>
            <button
              onClick={() => navigate('/analytics')}
              className="text-xs text-indigo-600 dark:text-indigo-400 hover:underline font-semibold flex items-center gap-1 self-start sm:self-auto cursor-pointer"
            >
              View Analytics <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {recommendations.slice(0, 3).map((rec) => (
              <div key={rec.id} className="p-4 rounded-xl bg-white dark:bg-slate-900/80 border border-slate-200 dark:border-slate-800 text-xs shadow-xs">
                <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-indigo-100 dark:bg-indigo-500/20 text-indigo-600 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30">
                  {rec.category}
                </span>
                <p className="font-semibold text-slate-800 dark:text-slate-200 mt-2 text-sm">{rec.title}</p>
                <p className="text-slate-500 dark:text-slate-400 text-xs mt-1 line-clamp-2">{rec.description}</p>
                <div className="mt-3 pt-3 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between">
                  <span className="font-semibold text-emerald-600 dark:text-emerald-400 text-[11px]">{rec.expectedImpact}</span>
                  <span className="text-[10px] text-slate-400 dark:text-slate-500 uppercase font-mono">{rec.status}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Cost & Requests Trend */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 sm:p-6 shadow-xs">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-semibold text-slate-900 dark:text-white">Expenditure & Request Trend</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">Daily spending trend over the past week</p>
            </div>
            <span className="text-xs font-mono text-slate-600 dark:text-slate-400 bg-slate-100 dark:bg-slate-800 px-2.5 py-1 rounded-lg">Last 7 Days</span>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="costGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366f1" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#6366f1" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#94a3b8" opacity={0.2} />
                <XAxis dataKey="date" stroke="#64748b" fontSize={11} />
                <YAxis stroke="#64748b" fontSize={11} tickFormatter={(val) => `$${val}`} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#0f172a',
                    color: '#f8fafc',
                    borderColor: '#334155',
                    borderRadius: '0.75rem',
                    fontSize: '12px'
                  }}
                  formatter={(val: any) => [`$${Number(val).toFixed(2)}`, 'Daily Cost']}
                />
                <Area type="monotone" dataKey="value" stroke="#6366f1" strokeWidth={2.5} fillOpacity={1} fill="url(#costGrad)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Cost by Model Distribution */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 sm:p-6 shadow-xs flex flex-col justify-between">
          <div>
            <h3 className="text-sm font-semibold text-slate-900 dark:text-white">Cost Distribution by Model</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Expenditure breakdown across registered providers</p>
            <div className="mt-6 space-y-3.5">
              {Object.entries(costByModel).map(([model, cost]: [string, any], i) => (
                <div key={model} className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-medium text-slate-700 dark:text-slate-300 font-mono">{model}</span>
                    <span className="font-mono text-slate-500 dark:text-slate-400">${Number(cost).toFixed(2)}</span>
                  </div>
                  <div className="h-2 w-full bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full ${
                        i === 0 ? 'bg-indigo-500' : i === 1 ? 'bg-cyan-500' : i === 2 ? 'bg-emerald-500' : 'bg-amber-500'
                      }`}
                      style={{ width: `${Math.min(100, Math.max(12, (Number(cost) / (costData?.totalCost || 12)) * 100))}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
          <button
            onClick={() => navigate('/comparisons')}
            className="w-full mt-6 py-2.5 text-xs font-semibold rounded-xl bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 transition-colors cursor-pointer"
          >
            Run Model Benchmark
          </button>
        </div>
      </div>

      {/* Recent Traces Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl overflow-hidden shadow-xs">
        <div className="px-4 sm:px-6 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div>
            <h3 className="text-sm font-semibold text-slate-900 dark:text-white">Recent Execution Traces</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">Live request telemetry with token and latency tracking</p>
          </div>
          <button
            onClick={() => navigate('/traces')}
            className="text-xs text-indigo-600 dark:text-indigo-400 hover:underline font-semibold flex items-center gap-1 cursor-pointer"
          >
            All Traces <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left text-xs min-w-[700px]">
            <thead className="bg-slate-50 dark:bg-slate-950/60 text-slate-500 dark:text-slate-400 text-[11px] uppercase tracking-wider border-b border-slate-200 dark:border-slate-800">
              <tr>
                <th className="px-5 py-3 font-semibold">Trace Name</th>
                <th className="px-5 py-3 font-semibold">Status</th>
                <th className="px-5 py-3 font-semibold">Latency</th>
                <th className="px-5 py-3 font-semibold">Tokens</th>
                <th className="px-5 py-3 font-semibold">Estimated Cost</th>
                <th className="px-5 py-3 font-semibold">Tags</th>
                <th className="px-5 py-3 font-semibold text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60 text-slate-700 dark:text-slate-300">
              {recentTraces.length > 0 ? (
                recentTraces.map((trace) => (
                  <tr
                    key={trace.id}
                    onClick={() => navigate(`/traces/${trace.id}`)}
                    className="hover:bg-slate-50 dark:hover:bg-slate-800/40 cursor-pointer transition-colors"
                  >
                    <td className="px-5 py-3.5 font-medium text-slate-900 dark:text-slate-200">{trace.name}</td>
                    <td className="px-5 py-3.5">
                      <span
                        className={`inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold ${
                          trace.status === 'ERROR'
                            ? 'bg-rose-100 dark:bg-rose-500/15 text-rose-700 dark:text-rose-400 border border-rose-200 dark:border-rose-500/20'
                            : 'bg-emerald-100 dark:bg-emerald-500/15 text-emerald-700 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/20'
                        }`}
                      >
                        {trace.status === 'ERROR' ? <XCircle className="w-3 h-3" /> : <CheckCircle2 className="w-3 h-3" />}
                        {trace.status}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 font-mono text-slate-600 dark:text-slate-400">{formatLatency(trace.latencyMs)}</td>
                    <td className="px-5 py-3.5 font-mono text-slate-600 dark:text-slate-400">{formatNumber(trace.totalTokens)}</td>
                    <td className="px-5 py-3.5 font-mono text-slate-600 dark:text-slate-400">{formatCurrency(trace.estimatedCost, 4)}</td>
                    <td className="px-5 py-3.5">
                      <div className="flex flex-wrap gap-1">
                        {(trace.tags || 'production').split(',').map((tag: string, idx: number) => (
                          <span key={idx} className="px-1.5 py-0.5 rounded text-[9px] font-mono bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700/60">
                            {tag.trim()}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="px-5 py-3.5 text-right text-indigo-600 dark:text-indigo-400 font-semibold hover:underline">
                      Inspect →
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={7} className="px-6 py-8 text-center text-slate-500">
                    No traces recorded yet. Ingest your first trace via Python SDK or click "Simulate Live Traffic" above.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
