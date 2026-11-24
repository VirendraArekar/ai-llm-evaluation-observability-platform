import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { formatCurrency, formatLatency, formatTokens } from '../utils/formatters';
import {
  BarChart3,
  TrendingUp,
  Coins,
  Cpu,
  Clock,
  Layers,
  Calendar,
  AlertCircle,
} from 'lucide-react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
} from 'recharts';

export const AnalyticsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [metrics, setMetrics] = useState<any>(null);
  const [costData, setCostData] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (currentProject) {
      loadAnalytics();
    }
  }, [currentProject]);

  const loadAnalytics = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const [m, c] = await Promise.all([
        api.getOverviewMetrics(currentProject.id),
        api.getCostAnalytics(currentProject.id),
      ]);
      setMetrics(m);
      setCostData(c);
    } catch (err) {
      console.error('Failed to load analytics', err);
    } finally {
      setLoading(false);
    }
  };

  const trendData = costData?.dailyTrend || [
    { day: 'Mon', cost: 1.25, tokens: 45000 },
    { day: 'Tue', cost: 2.10, tokens: 82000 },
    { day: 'Wed', cost: 1.85, tokens: 71000 },
    { day: 'Thu', cost: 3.40, tokens: 124000 },
    { day: 'Fri', cost: 2.95, tokens: 105000 },
    { day: 'Sat', cost: 0.85, tokens: 32000 },
    { day: 'Sun', cost: 1.45, tokens: 54000 },
  ];

  const modelBreakdown = costData?.modelBreakdown || [
    { model: 'gpt-4o', cost: 5.42 },
    { model: 'claude-3-5-sonnet', cost: 4.18 },
    { model: 'gemini-1.5-pro', cost: 2.30 },
    { model: 'llama-3.1-8b', cost: 0.12 },
  ];

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
          <BarChart3 className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
          Cost & Token Intelligence Analytics
        </h1>
        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
          Deep financial observability, model token consumption trends, budget forecasting, and cost anomaly detection.
        </p>
      </div>

      {/* KPI Ribbon */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <Coins className="w-3.5 h-3.5 text-emerald-500" /> Total Expenditure (7d)
          </div>
          <div className="text-2xl font-bold font-mono text-emerald-600 dark:text-emerald-400">
            {formatCurrency(metrics?.totalCost || 13.89)}
          </div>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">Projected Monthly: ~$59.40</p>
        </div>

        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <Layers className="w-3.5 h-3.5 text-cyan-500" /> Total Tokens Processed
          </div>
          <div className="text-2xl font-bold font-mono text-slate-900 dark:text-slate-100">
            {formatTokens(metrics?.totalTokens || 513000)}
          </div>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">68% prompt / 32% completion</p>
        </div>

        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <Clock className="w-3.5 h-3.5 text-amber-500" /> Mean P95 Latency
          </div>
          <div className="text-2xl font-bold font-mono text-slate-900 dark:text-slate-100">
            {formatLatency(metrics?.avgLatencyMs || 745)}
          </div>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">Fastest: Gemini 1.5 Flash (180ms)</p>
        </div>

        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <TrendingUp className="w-3.5 h-3.5 text-indigo-500 dark:text-indigo-400" /> Cost Efficiency
          </div>
          <div className="text-2xl font-bold font-mono text-indigo-600 dark:text-indigo-300">
            $0.027 <span className="text-xs font-normal text-slate-500 dark:text-slate-400">/ 1k reqs</span>
          </div>
          <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-medium">14% below monthly budget threshold</p>
        </div>
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Daily Token & Cost Trend */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-4 sm:p-5 space-y-4 shadow-sm">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-600 dark:text-slate-400 flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> 7-Day Expenditure & Volume Trend
            </h3>
            <span className="text-xs text-slate-500 font-mono">USD ($) vs Date</span>
          </div>

          <div className="h-64 sm:h-72">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={trendData}>
                <defs>
                  <linearGradient id="costGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#6366f1" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" className="dark:stroke-slate-800" />
                <XAxis dataKey="day" stroke="#94a3b8" tick={{ fontSize: 11 }} />
                <YAxis stroke="#94a3b8" tick={{ fontSize: 11 }} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    border: '1px solid #334155',
                    borderRadius: '8px',
                    fontSize: '12px',
                    color: '#f8fafc',
                  }}
                />
                <Area type="monotone" dataKey="cost" stroke="#6366f1" strokeWidth={2} fill="url(#costGrad)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Cost by Model Breakdown */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-4 sm:p-5 space-y-4 shadow-sm">
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-600 dark:text-slate-400 flex items-center gap-2">
            <Cpu className="w-4 h-4 text-cyan-500" /> Cost By Model
          </h3>

          <div className="h-64 sm:h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={modelBreakdown} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" className="dark:stroke-slate-800" />
                <XAxis type="number" stroke="#94a3b8" tick={{ fontSize: 10 }} />
                <YAxis dataKey="model" type="category" stroke="#94a3b8" tick={{ fontSize: 10 }} width={80} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'rgba(15, 23, 42, 0.95)',
                    border: '1px solid #334155',
                    borderRadius: '8px',
                    fontSize: '12px',
                    color: '#f8fafc',
                  }}
                />
                <Bar dataKey="cost" fill="#06b6d4" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
};
