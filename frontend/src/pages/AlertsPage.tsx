import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Alert, Notification } from '../types';
import { formatRelativeTime } from '../utils/formatters';
import {
  Bell,
  Plus,
  AlertTriangle,
  CheckCircle2,
  Clock,
  ShieldAlert,
  Sliders,
  Check,
} from 'lucide-react';

export const AlertsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  // New Alert Modal
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [alertName, setAlertName] = useState<string>('');
  const [alertMetric, setAlertMetric] = useState<string>('latency_p95');
  const [alertThreshold, setAlertThreshold] = useState<number>(2000);
  const [alertCondition, setAlertCondition] = useState<string>('GREATER_THAN');

  useEffect(() => {
    if (currentProject) {
      loadAlertsAndNotifications();
    }
  }, [currentProject]);

  const loadAlertsAndNotifications = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const [aData, nData] = await Promise.all([
        api.getAlerts(currentProject.id),
        api.getNotifications(),
      ]);
      setAlerts(aData);
      setNotifications(nData);
    } catch (err) {
      console.error('Failed to load alerts/notifications', err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleAlert = async (id: string) => {
    try {
      await api.toggleAlert(id);
      setAlerts((prev) =>
        prev.map((a) => (a.id === id ? { ...a, enabled: !a.enabled } : a))
      );
    } catch (err) {
      console.error('Failed to toggle alert', err);
    }
  };

  const handleCreateAlert = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentProject || !alertName) return;
    try {
      const created = await api.createAlert(currentProject.id, {
        name: alertName,
        metric: alertMetric,
        threshold: alertThreshold,
        condition: alertCondition,
        enabled: true,
      });
      setAlerts((prev) => [created, ...prev]);
      setShowCreateModal(false);
      setAlertName('');
    } catch (err) {
      console.error('Failed to create alert', err);
    }
  };

  const handleMarkNotificationRead = async (id: string) => {
    try {
      await api.markNotificationRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch (err) {
      console.error('Failed to mark notification read', err);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
            <Bell className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
            Real-Time Monitoring & Threshold Alerts
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Configure automated incident monitors for latency spikes, error rate breaches, hallucination risk, and budget limits.
          </p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold flex items-center gap-2 transition-colors shadow-sm self-start"
        >
          <Plus className="w-4 h-4" />
          Create Alert Rule
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left: Configured Alert Rules */}
        <div className="lg:col-span-2 space-y-4">
          <h2 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-2">
            <Sliders className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> Configured Alert Rules ({alerts.length})
          </h2>

          <div className="space-y-3">
            {alerts.map((alert) => (
              <div
                key={alert.id}
                className="p-4 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-sm"
              >
                <div className="space-y-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <span className="font-semibold text-sm text-slate-900 dark:text-slate-200">{alert.name}</span>
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-slate-100 dark:bg-slate-800 text-indigo-600 dark:text-indigo-300">
                      {alert.metric || alert.alertType} {alert.condition === 'GREATER_THAN' ? '>' : '<'} {alert.threshold || alert.thresholdValue}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 dark:text-slate-400">
                    Triggers incident notification to engineering team when breached.
                  </p>
                </div>

                <div className="flex items-center gap-3">
                  <button
                    onClick={() => handleToggleAlert(alert.id)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                      alert.enabled
                        ? 'bg-emerald-50 dark:bg-emerald-500/20 text-emerald-600 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/30'
                        : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700'
                    }`}
                  >
                    {alert.enabled ? 'ACTIVE' : 'PAUSED'}
                  </button>
                </div>
              </div>
            ))}
            {alerts.length === 0 && (
              <div className="p-8 text-center text-slate-400 dark:text-slate-500 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800">
                No alert rules created yet.
              </div>
            )}
          </div>
        </div>

        {/* Right: Recent In-App Incident Notifications */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-4 sm:p-5 space-y-4 shadow-sm">
          <h2 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-2">
            <ShieldAlert className="w-4 h-4 text-amber-500" /> Incident Notifications
          </h2>

          <div className="space-y-2.5 max-h-[500px] overflow-y-auto custom-scrollbar">
            {notifications.map((n) => (
              <div
                key={n.id}
                className={`p-3 rounded-lg border text-xs space-y-1.5 transition-colors ${
                  n.read || n.isRead
                    ? 'bg-slate-50 dark:bg-slate-950/40 border-slate-200 dark:border-slate-800/60 opacity-60'
                    : 'bg-amber-50/50 dark:bg-slate-950 border-amber-300 dark:border-amber-500/30 text-slate-900 dark:text-slate-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-amber-600 dark:text-amber-400 flex items-center gap-1.5">
                    <AlertTriangle className="w-3.5 h-3.5" /> {n.title}
                  </span>
                  {!(n.read || n.isRead) && (
                    <button
                      onClick={() => handleMarkNotificationRead(n.id)}
                      className="text-[10px] text-slate-500 hover:text-slate-700 dark:hover:text-slate-200"
                    >
                      Mark read
                    </button>
                  )}
                </div>
                <p className="text-slate-700 dark:text-slate-300 text-[11px] leading-relaxed">{n.message}</p>
                <div className="text-[10px] text-slate-400 dark:text-slate-500">
                  {formatRelativeTime(n.createdAt)}
                </div>
              </div>
            ))}
            {notifications.length === 0 && (
              <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-8">No incident alerts fired.</p>
            )}
          </div>
        </div>
      </div>

      {/* Create Alert Rule Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <form
            onSubmit={handleCreateAlert}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-5 sm:p-6 space-y-4 shadow-2xl"
          >
            <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm">Create Threshold Alert Rule</h3>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Rule Name</label>
              <input
                type="text"
                required
                value={alertName}
                onChange={(e) => setAlertName(e.target.value)}
                placeholder="e.g., Critical Latency Spike > 2500ms"
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Target Metric</label>
              <select
                value={alertMetric}
                onChange={(e) => setAlertMetric(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              >
                <option value="latency_p95">P95 Latency (ms)</option>
                <option value="error_rate">Error Rate (%)</option>
                <option value="daily_cost">Daily Cost ($)</option>
                <option value="hallucination_rate">Hallucination Rate (%)</option>
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Condition</label>
                <select
                  value={alertCondition}
                  onChange={(e) => setAlertCondition(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
                >
                  <option value="GREATER_THAN">Greater than (&gt;)</option>
                  <option value="LESS_THAN">Less than (&lt;)</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Threshold</label>
                <input
                  type="number"
                  required
                  value={alertThreshold}
                  onChange={(e) => setAlertThreshold(parseFloat(e.target.value))}
                  className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 font-mono focus:outline-none focus:ring-1 focus:ring-indigo-500"
                />
              </div>
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
                Save Rule
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
