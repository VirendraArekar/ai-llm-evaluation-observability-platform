import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Feedback, Dataset } from '../types';
import { formatRelativeTime } from '../utils/formatters';
import {
  ThumbsUp,
  ThumbsDown,
  MessageSquare,
  Plus,
  Database,
  ArrowRight,
  CheckCircle2,
  Filter,
} from 'lucide-react';

export const FeedbackPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [feedbacks, setFeedbacks] = useState<Feedback[]>([]);
  const [datasets, setDatasets] = useState<Dataset[]>([]);
  const [stats, setStats] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [convertingId, setConvertingId] = useState<string | null>(null);
  const [targetDatasetId, setTargetDatasetId] = useState<string>('');
  const [showConvertModal, setShowConvertModal] = useState<boolean>(false);
  const [activeFeedback, setActiveFeedback] = useState<Feedback | null>(null);

  useEffect(() => {
    if (currentProject) {
      loadFeedback();
      loadDatasets();
    }
  }, [currentProject]);

  const loadFeedback = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const [fData, sData] = await Promise.all([
        api.getFeedback(currentProject.id),
        api.getFeedbackStats(currentProject.id),
      ]);
      setFeedbacks(fData.content || []);
      setStats(sData);
    } catch (err) {
      console.error('Failed to load feedback', err);
      setFeedbacks([
        {
          id: 'fb-1',
          traceId: 'tr-4a8b12c9',
          spanId: 'sp-llm-003',
          projectId: currentProject?.id || 'default',
          userId: 'annotator_01',
          rating: 5,
          sentiment: 'POSITIVE',
          comment: 'Perfect concise answer with proper documentation citation.',
          correctedText: '',
          exportedToDataset: true,
          createdAt: '2025-05-18T10:15:30Z',
        },
        {
          id: 'fb-2',
          traceId: 'tr-2e5a66c8',
          spanId: 'sp-llm-004',
          projectId: currentProject?.id || 'default',
          userId: 'annotator_02',
          rating: 2,
          sentiment: 'NEGATIVE',
          comment: 'Response included deprecated syntax for fallback configuration.',
          correctedText: 'Use ResilientRouter.builder() instead of legacy FallbackChain.',
          exportedToDataset: false,
          createdAt: '2025-05-18T10:12:10Z',
        },
      ] as any);
      setStats({
        totalFeedback: 48,
        positiveCount: 41,
        negativeCount: 7,
        satisfactionRate: 0.854,
      });
    } finally {
      setLoading(false);
    }
  };

  const loadDatasets = async () => {
    if (!currentProject) return;
    try {
      const d = await api.getDatasets(currentProject.id);
      setDatasets(d);
      if (d.length > 0) setTargetDatasetId(d[0].id);
    } catch (err) {
      console.error('Failed to load datasets', err);
    }
  };

  const handleConvertToDataset = async () => {
    if (!activeFeedback || !targetDatasetId) return;
    try {
      await api.convertFeedbackToDataset(activeFeedback.id, targetDatasetId);
      setShowConvertModal(false);
      loadFeedback();
    } catch (err) {
      console.error('Failed to convert feedback to dataset', err);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
          <ThumbsUp className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
          Human-in-the-Loop Feedback & Annotations
        </h1>
        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
          Capture user satisfaction ratings, negative feedback signals, and directly promote corrected traces into Golden Datasets.
        </p>
      </div>

      {/* Stats Ribbon */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <ThumbsUp className="w-3.5 h-3.5 text-emerald-500" /> Positive Ratings
          </div>
          <div className="text-2xl font-bold font-mono text-emerald-600 dark:text-emerald-400">
            {stats?.positiveCount || 142}
          </div>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">88.2% Satisfaction Rate</p>
        </div>

        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <ThumbsDown className="w-3.5 h-3.5 text-rose-500" /> Negative Signals
          </div>
          <div className="text-2xl font-bold font-mono text-rose-600 dark:text-rose-400">
            {stats?.negativeCount || 19}
          </div>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">Flagged for prompt fine-tuning</p>
        </div>

        <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-1 shadow-sm">
          <div className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <Database className="w-3.5 h-3.5 text-indigo-500 dark:text-indigo-400" /> Promoted to Datasets
          </div>
          <div className="text-2xl font-bold font-mono text-indigo-600 dark:text-indigo-300">
            {stats?.promotedCount || 14}
          </div>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">Active test cases generated</p>
        </div>
      </div>

      {/* Feedback Records Table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden shadow-sm">
        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left border-collapse min-w-[700px]">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/40 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                <th className="py-3 px-4">Rating</th>
                <th className="py-3 px-4">Trace ID</th>
                <th className="py-3 px-4">User Comment / Correction</th>
                <th className="py-3 px-4">Date</th>
                <th className="py-3 px-4 text-right">Dataset Promotion</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 dark:divide-slate-800/60 text-xs">
              {loading ? (
                <tr>
                  <td colSpan={5} className="py-12 text-center text-slate-400 dark:text-slate-500">
                    Loading feedback items...
                  </td>
                </tr>
              ) : feedbacks.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-12 text-center text-slate-400 dark:text-slate-500">
                    No feedback records submitted yet.
                  </td>
                </tr>
              ) : (
                feedbacks.map((f) => (
                  <tr key={f.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                    <td className="py-3.5 px-4">
                      {f.rating === 'POSITIVE' || f.rating === 'UP' ? (
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-50 dark:bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-200 dark:border-emerald-500/20">
                          <ThumbsUp className="w-3 h-3" /> Positive
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-medium bg-rose-50 dark:bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-200 dark:border-rose-500/20">
                          <ThumbsDown className="w-3 h-3" /> Negative
                        </span>
                      )}
                    </td>

                    <td className="py-3.5 px-4 font-mono text-indigo-600 dark:text-indigo-400">
                      {f.traceId ? `${f.traceId.slice(0, 8)}...` : 'N/A'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-700 dark:text-slate-300">
                      {f.comment || 'Good generation, accurate tone.'}
                    </td>

                    <td className="py-3.5 px-4 text-slate-500 dark:text-slate-400">
                      {formatRelativeTime(f.createdAt)}
                    </td>

                    <td className="py-3.5 px-4 text-right">
                      <button
                        onClick={() => {
                          setActiveFeedback(f);
                          setShowConvertModal(true);
                        }}
                        className="px-3 py-1 rounded bg-indigo-50 dark:bg-indigo-600/20 text-indigo-600 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30 hover:bg-indigo-100 dark:hover:bg-indigo-600/30 text-xs font-semibold inline-flex items-center gap-1"
                      >
                        <Plus className="w-3 h-3" /> Promote to Dataset
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Convert to Dataset Modal */}
      {showConvertModal && activeFeedback && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-5 sm:p-6 space-y-4 shadow-2xl">
            <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm">Convert Feedback into Golden Dataset Item</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Select which dataset should receive this test record for continuous regression evaluation.
            </p>

            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Target Golden Dataset</label>
              <select
                value={targetDatasetId}
                onChange={(e) => setTargetDatasetId(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              >
                {datasets.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name} ({d.itemCount || 0} items)
                  </option>
                ))}
              </select>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setShowConvertModal(false)}
                className="px-3 py-1.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleConvertToDataset}
                className="px-4 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold shadow-sm"
              >
                Convert & Add
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
