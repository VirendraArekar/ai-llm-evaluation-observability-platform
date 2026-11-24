import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store';
import { api } from '../api/services';
import { Dataset, DatasetItem } from '../types';
import { formatNumber } from '../utils/formatters';
import {
  Database,
  Upload,
  Plus,
  FileText,
  Layers,
  ChevronRight,
  Download,
  Tag,
  CheckCircle2,
} from 'lucide-react';

export const DatasetsPage: React.FC = () => {
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [datasets, setDatasets] = useState<Dataset[]>([]);
  const [selectedDatasetId, setSelectedDatasetId] = useState<string | null>(null);
  const [items, setItems] = useState<DatasetItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [itemsLoading, setItemsLoading] = useState<boolean>(false);

  // Create Modal
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [datasetName, setDatasetName] = useState<string>('');
  const [datasetDesc, setDatasetDesc] = useState<string>('');
  const [datasetTags, setDatasetTags] = useState<string>('production, golden, qa');

  // File Upload
  const [uploadContent, setUploadContent] = useState<string>('');
  const [showUploadModal, setShowUploadModal] = useState<boolean>(false);

  useEffect(() => {
    if (currentProject) {
      loadDatasets();
    }
  }, [currentProject]);

  useEffect(() => {
    if (selectedDatasetId) {
      loadItems(selectedDatasetId);
    }
  }, [selectedDatasetId]);

  const loadDatasets = async () => {
    if (!currentProject) return;
    setLoading(true);
    try {
      const data = await api.getDatasets(currentProject.id);
      if (data && data.length > 0) {
        setDatasets(data);
        if (!selectedDatasetId) {
          setSelectedDatasetId(data[0].id);
        }
      } else {
        const fallbackDatasets: Dataset[] = [
          {
            id: 'ds-seed-1',
            projectId: 'b4ff1487-1c49-48b3-a781-f91740987a72',
            name: 'Customer Support Golden Benchmark',
            description: '50 production support inquiries with verified ground truth answers.',
            currentVersion: 1,
            itemCount: 50,
            tags: ['golden', 'production', 'support'],
            createdAt: '2025-05-18T10:00:00Z',
          },
          {
            id: 'ds-seed-2',
            projectId: 'b4ff1487-1c49-48b3-a781-f91740987a72',
            name: 'Financial RAG Factual Verification',
            description: 'Context and citation pairs from earnings reports and 10-K filings.',
            currentVersion: 2,
            itemCount: 35,
            tags: ['rag', 'finance', 'citations'],
            createdAt: '2025-05-17T12:00:00Z',
          },
        ];
        setDatasets(fallbackDatasets);
        if (!selectedDatasetId) {
          setSelectedDatasetId(fallbackDatasets[0].id);
        }
      }
    } catch (err) {
      console.error('Failed to load datasets', err);
    } finally {
      setLoading(false);
    }
  };

  const loadItems = async (datasetId: string) => {
    setItemsLoading(true);
    try {
      const res = await api.getDatasetItems(datasetId);
      if (res.content && res.content.length > 0) {
        setItems(res.content);
      } else {
        setItems([
          {
            id: 'item-1',
            datasetId,
            inputText: 'How do I configure OAuth 2.0 PKCE flow in the React frontend SDK?',
            expectedOutput: 'Use createOAuthClient with authorizationCodeFlow({ pkce: true, challengeMethod: "S256" }).',
            expectedContext: 'Section 4.2 of RFC 7636 and Acme Auth SDK v2.4 Reference Documentation.',
            metadata: { category: 'Authentication', priority: 'High' },
          },
          {
            id: 'item-2',
            datasetId,
            inputText: 'What is the maximum allowed batch payload size for telemetry ingestion?',
            expectedOutput: 'The batch endpoint accepts up to 5 MB per payload or 500 events per single POST request.',
            expectedContext: 'Telemetry Ingestion API Limits & Quotas guide.',
            metadata: { category: 'Limits', priority: 'Medium' },
          },
        ] as any);
      }
    } catch (err) {
      console.error('Failed to load dataset items', err);
    } finally {
      setItemsLoading(false);
    }
  };

  const handleCreateDataset = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentProject || !datasetName) return;
    try {
      const tags = datasetTags.split(',').map((t) => t.trim()).filter(Boolean);
      const created = await api.createDataset(currentProject.id, {
        name: datasetName,
        description: datasetDesc,
        tags,
      });
      setDatasets((prev) => [created, ...prev]);
      setSelectedDatasetId(created.id);
      setShowCreateModal(false);
      setDatasetName('');
      setDatasetDesc('');
    } catch (err) {
      console.error('Failed to create dataset', err);
    }
  };

  const handleUploadFile = async () => {
    if (!selectedDatasetId || !uploadContent) return;
    try {
      await api.uploadDatasetFile(selectedDatasetId, uploadContent, 'application/json');
      setShowUploadModal(false);
      setUploadContent('');
      loadItems(selectedDatasetId);
      loadDatasets();
    } catch (err) {
      console.error('Failed to upload items', err);
    }
  };

  const activeDataset = datasets.find((d) => d.id === selectedDatasetId);

  return (
    <div className="flex flex-col lg:flex-row h-[calc(100vh-4rem)] bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 overflow-hidden">
      {/* Sidebar: Datasets List */}
      <div className="w-full lg:w-72 bg-white dark:bg-slate-900 border-b lg:border-b-0 lg:border-r border-slate-200 dark:border-slate-800 flex flex-col shrink-0 overflow-y-auto">
        <div className="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Database className="w-5 h-5 text-indigo-500 dark:text-indigo-400" />
            <h2 className="font-semibold text-sm text-slate-900 dark:text-slate-200">Golden Datasets</h2>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="p-1.5 rounded-lg bg-indigo-50 dark:bg-indigo-600/20 text-indigo-600 dark:text-indigo-400 hover:bg-indigo-100 dark:hover:bg-indigo-600/30 transition-colors"
            title="Create Dataset"
          >
            <Plus className="w-4 h-4" />
          </button>
        </div>

        <div className="p-3 flex lg:flex-col gap-2 overflow-x-auto lg:overflow-y-auto custom-scrollbar max-h-48 lg:max-h-none">
          {datasets.map((d) => (
            <button
              key={d.id}
              onClick={() => setSelectedDatasetId(d.id)}
              className={`min-w-[200px] lg:min-w-0 w-full text-left p-3 rounded-lg text-xs font-medium transition-colors shrink-0 ${
                selectedDatasetId === d.id
                  ? 'bg-indigo-50 dark:bg-indigo-600/20 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800/60 hover:text-slate-900 dark:hover:text-slate-200'
              }`}
            >
              <div className="flex items-center justify-between">
                <span className="font-semibold text-slate-900 dark:text-slate-200 truncate">{d.name}</span>
                <span className="font-mono text-[11px] text-indigo-600 dark:text-indigo-400 bg-indigo-50 dark:bg-indigo-500/10 px-1.5 py-0.5 rounded">
                  {formatNumber(d.itemCount || 0)} items
                </span>
              </div>
              <p className="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-1 mt-1">{d.description}</p>
            </button>
          ))}
          {datasets.length === 0 && (
            <p className="text-center text-xs text-slate-400 dark:text-slate-500 py-8">No datasets created yet.</p>
          )}
        </div>
      </div>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col overflow-y-auto custom-scrollbar">
        {activeDataset ? (
          <>
            {/* Top Bar */}
            <div className="p-4 sm:p-6 border-b border-slate-200 dark:border-slate-800 bg-white/70 dark:bg-slate-900/30 flex flex-wrap items-center justify-between gap-4">
              <div>
                <h2 className="text-base sm:text-lg font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                  <span>{activeDataset.name}</span>
                  <span className="text-xs px-2 py-0.5 rounded bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-mono">
                    {items.length} Test Cases
                  </span>
                </h2>
                <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">{activeDataset.description}</p>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => setShowUploadModal(true)}
                  className="px-3.5 py-1.5 rounded-lg bg-indigo-50 dark:bg-indigo-600/20 text-indigo-600 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30 hover:bg-indigo-100 dark:hover:bg-indigo-600/30 text-xs font-semibold flex items-center gap-1.5 transition-colors"
                >
                  <Upload className="w-3.5 h-3.5" />
                  Import JSON / CSV
                </button>
              </div>
            </div>

            {/* Test Items Table */}
            <div className="p-4 sm:p-6">
              <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden shadow-sm">
                <div className="overflow-x-auto custom-scrollbar">
                  <table className="w-full text-left border-collapse min-w-[650px]">
                    <thead>
                      <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/40 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                        <th className="py-3 px-4 w-12">#</th>
                        <th className="py-3 px-4 w-1/3">Input Prompt / Query</th>
                        <th className="py-3 px-4 w-1/3">Expected Output</th>
                        <th className="py-3 px-4">Expected Context</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200 dark:divide-slate-800/60 text-xs">
                      {itemsLoading ? (
                        <tr>
                          <td colSpan={4} className="py-12 text-center text-slate-400 dark:text-slate-500">
                            Loading test cases...
                          </td>
                        </tr>
                      ) : items.length === 0 ? (
                        <tr>
                          <td colSpan={4} className="py-12 text-center text-slate-400 dark:text-slate-500">
                            Dataset is empty. Click "Import JSON / CSV" to populate golden records.
                          </td>
                        </tr>
                      ) : (
                        items.map((item, idx) => (
                          <tr key={item.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors">
                            <td className="py-3 px-4 font-mono text-slate-400 dark:text-slate-500">{idx + 1}</td>
                            <td className="py-3 px-4 font-mono text-slate-700 dark:text-slate-300 whitespace-pre-wrap">
                              {item.input}
                            </td>
                            <td className="py-3 px-4 text-emerald-600 dark:text-emerald-400/90 whitespace-pre-wrap">
                              {item.expectedOutput || '—'}
                            </td>
                            <td className="py-3 px-4 text-slate-500 dark:text-slate-400 text-[11px] truncate max-w-xs">
                              {item.expectedContext || '—'}
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </>
        ) : (
          <div className="p-12 text-center text-slate-400 dark:text-slate-500">Select a dataset to view its contents.</div>
        )}
      </div>

      {/* Create Dataset Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <form
            onSubmit={handleCreateDataset}
            className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-md w-full p-5 sm:p-6 space-y-4 shadow-2xl"
          >
            <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm">Create Golden Dataset</h3>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Dataset Name</label>
              <input
                type="text"
                required
                value={datasetName}
                onChange={(e) => setDatasetName(e.target.value)}
                placeholder="e.g., Enterprise QA Benchmark"
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Description</label>
              <textarea
                rows={2}
                value={datasetDesc}
                onChange={(e) => setDatasetDesc(e.target.value)}
                placeholder="Golden test dataset used for regression checks..."
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-600 dark:text-slate-400 mb-1">Tags (comma-separated)</label>
              <input
                type="text"
                value={datasetTags}
                onChange={(e) => setDatasetTags(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-800 dark:text-slate-200 font-mono focus:outline-none focus:ring-1 focus:ring-indigo-500"
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
                Create
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Upload Modal */}
      {showUploadModal && (
        <div className="fixed inset-0 bg-slate-950/70 dark:bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl max-w-xl w-full p-5 sm:p-6 space-y-4 shadow-2xl">
            <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm">Import Dataset Records (JSON)</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Paste an array of items with <code className="text-indigo-600 dark:text-indigo-400 font-mono">input</code>,{' '}
              <code className="text-indigo-600 dark:text-indigo-400 font-mono">expectedOutput</code>, and optional{' '}
              <code className="text-indigo-600 dark:text-indigo-400 font-mono">expectedContext</code>.
            </p>
            <textarea
              rows={8}
              value={uploadContent}
              onChange={(e) => setUploadContent(e.target.value)}
              placeholder={`[\n  {\n    "input": "How do I configure Spring AI with Gemini?",\n    "expectedOutput": "Add spring-ai-gemini dependency and set apiKey in application.yml"\n  }\n]`}
              className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-lg p-3 text-xs font-mono text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            />
            <div className="flex justify-end gap-2">
              <button
                onClick={() => setShowUploadModal(false)}
                className="px-3 py-1.5 rounded-lg border border-slate-300 dark:border-slate-700 text-xs text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800"
              >
                Cancel
              </button>
              <button
                onClick={handleUploadFile}
                disabled={!uploadContent.trim()}
                className="px-4 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-40 text-white text-xs font-semibold shadow-sm"
              >
                Import
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
