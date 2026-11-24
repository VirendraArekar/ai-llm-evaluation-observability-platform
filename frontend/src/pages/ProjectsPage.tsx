import React, { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../store';
import { setCurrentProject } from '../store/authSlice';
import { api } from '../api/services';
import { Project } from '../types';
import { formatRelativeTime } from '../utils/formatters';
import {
  FolderKanban,
  Plus,
  CheckCircle2,
  Calendar,
  Layers,
  ArrowRight,
} from 'lucide-react';

export const ProjectsPage: React.FC = () => {
  const dispatch = useDispatch();
  const currentWorkspace = useSelector((state: RootState) => state.auth.currentWorkspace);
  const currentProject = useSelector((state: RootState) => state.auth.currentProject);
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (currentWorkspace) {
      loadProjects();
    }
  }, [currentWorkspace]);

  const loadProjects = async () => {
    if (!currentWorkspace) return;
    setLoading(true);
    try {
      const data = await api.getProjects(currentWorkspace.id);
      setProjects(data);
    } catch (err) {
      console.error('Failed to load projects', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 space-y-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-bold tracking-tight text-slate-900 dark:text-slate-100 flex items-center gap-2.5">
            <FolderKanban className="w-6 h-6 text-indigo-500 dark:text-indigo-400" />
            Projects & Workspaces
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Manage LLMOps environments, isolate evaluation datasets, and control API keys per project.
          </p>
        </div>
      </div>

      {/* Projects Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {projects.map((proj) => {
          const isSelected = currentProject?.id === proj.id;
          return (
            <div
              key={proj.id}
              className={`p-5 rounded-xl border transition-all flex flex-col justify-between ${
                isSelected
                  ? 'bg-indigo-50/80 dark:bg-indigo-950/20 border-indigo-400 dark:border-indigo-500/40 shadow-md shadow-indigo-600/10'
                  : 'bg-white dark:bg-slate-900 border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 shadow-sm'
              }`}
            >
              <div>
                <div className="flex items-center justify-between mb-2">
                  <h3 className="font-bold text-base text-slate-900 dark:text-slate-100">{proj.name}</h3>
                  {isSelected && (
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-indigo-100 dark:bg-indigo-500/20 text-indigo-700 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-500/30">
                      CURRENT
                    </span>
                  )}
                </div>
                <div className="text-xs font-mono text-indigo-600 dark:text-indigo-400 mb-2">{proj.slug}</div>
                <p className="text-xs text-slate-600 dark:text-slate-400 line-clamp-2">{proj.description}</p>
              </div>

              <div className="pt-4 mt-4 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between">
                <span className="text-[11px] text-slate-500">
                  Created {formatRelativeTime(proj.createdAt)}
                </span>
                {!isSelected && (
                  <button
                    onClick={() => dispatch(setCurrentProject(proj))}
                    className="px-3 py-1.5 rounded-lg bg-indigo-50 dark:bg-indigo-600/20 text-indigo-600 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30 hover:bg-indigo-100 dark:hover:bg-indigo-600/30 text-xs font-semibold flex items-center gap-1 transition-colors"
                  >
                    Switch to Project
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
