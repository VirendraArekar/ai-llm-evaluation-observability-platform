import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { ApiKey } from '../types';
import { formatRelativeTime } from '../utils/formatters';
import {
  Settings,
  Key,
  Plus,
  Trash2,
  Copy,
  Check,
  ShieldAlert,
  Users,
  Building,
} from 'lucide-react';

export const SettingsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const user = useSelector((state: RootState) => state.auth.user);
  const [apiKeys, setApiKeys] = useState<ApiKey[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  // New Key Modal
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [keyName, setKeyName] = useState<string>('');
  const [newlyCreatedKey, setNewlyCreatedKey] = useState<string | null>(null);

  useEffect(() => {
    if (currentProject) {
      loadApiKeys();
    }
  }, [currentProject]);

  const loadApiKeys = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const data = await api.getApiKeys(currentProject.id);
      setApiKeys(data);
    } catch (err) {
      console.error('Failed to load API keys', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateKey = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentProject || !keyName) return;
    try {
      const created = await api.createApiKey(currentProject.id, { name: keyName });
      setApiKeys((prev) => [created, ...prev]);
      setNewlyCreatedKey(created.keyPrefix + '...');
      setShowCreateModal(false);
      setKeyName('');
    } catch (err) {
      console.error('Failed to create API key', err);
    }
  };

  const handleRevokeKey = async (id: string) => {
    try {
      await api.revokeApiKey(id);
      setApiKeys((prev) => prev.filter((k) => k.id !== id));
    } catch (err) {
      console.error('Failed to revoke API key', err);
    }
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(text);
    setTimeout(() => setCopiedKey(null), 2000);
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
          <Settings className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
          Workspace & Security Settings
        </h1>
        <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
          Manage ingestion API keys, team roles, and organization credentials.
        </p>
      </div>

      {/* Account Info */}
      <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-4 shadow-sm">
        <h2 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-2">
          <Building className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> Organization & Account
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
          <div className="p-3 rounded-lg bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800">
            <span className="text-slate-500 uppercase text-[10px] block">Signed in User</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{user?.email || 'admin@aetherllm.ai'}</span>
          </div>

          <div className="p-3 rounded-lg bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800">
            <span className="text-slate-500 uppercase text-[10px] block">Role</span>
            <span className="font-semibold text-indigo-600 dark:text-indigo-400">{user?.role || 'SUPER_ADMIN'}</span>
          </div>

          <div className="p-3 rounded-lg bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800">
            <span className="text-slate-500 uppercase text-[10px] block">Active Project</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{currentProject?.name}</span>
          </div>
        </div>
      </div>

      {/* Ingestion API Keys */}
      <div className="p-4 sm:p-5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 space-y-4 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <h2 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-2">
            <Key className="w-4 h-4 text-indigo-500 dark:text-indigo-400" /> Ingestion SDK API Keys ({apiKeys.length})
          </h2>
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-3.5 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold flex items-center gap-1.5 self-start shadow-sm"
          >
            <Plus className="w-3.5 h-3.5" />
            Generate New API Key
          </button>
        </div>

        <div className="overflow-x-auto custom-scrollbar">
          <table className="w-full text-left border-collapse min-w-[500px]">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                <th className="py-2.5 px-3">Name</th>
                <th className="py-2.5 px-3">Key Prefix</th>
                <th className="py-2.5 px-3">Created</th>
                <th className="py-2.5 px-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200 dark:divide-slate-800/60 text-xs">
              {apiKeys.map((key) => (
                <tr key={key.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40">
                  <td className="py-2.5 px-3 font-semibold text-slate-800 dark:text-slate-200">{key.name}</td>
                  <td className="py-2.5 px-3 font-mono text-indigo-600 dark:text-indigo-400">{key.keyPrefix}••••••••••••</td>
                  <td className="py-2.5 px-3 text-slate-500 dark:text-slate-400">
                    {formatRelativeTime(key.createdAt)}
                  </td>
                  <td className="py-2.5 px-3 text-right">
                    <button
                      onClick={() => handleRevokeKey(key.id)}
                      className="text-rose-600 dark:text-rose-400 hover:text-rose-500 font-medium inline-flex items-center gap-1"
                    >
                      <Trash2 className="w-3.5 h-3.5" /> Revoke
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Key Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <form
            onSubmit={handleCreateKey}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-5 sm:p-6 space-y-4 shadow-2xl"
          >
            <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm">Create Ingestion API Key</h3>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Key Description</label>
              <input
                type="text"
                required
                value={keyName}
                onChange={(e) => setKeyName(e.target.value)}
                placeholder="e.g., Python SDK Production Daemon"
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
                Create Key
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
