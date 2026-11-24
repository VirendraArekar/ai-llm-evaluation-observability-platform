import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Sparkles,
  FolderKanban,
  Cpu,
  Box,
  Play,
  Code2,
  Activity,
  CheckCircle2,
  Database,
  FlaskConical,
  Scale,
  BarChart3,
  Bell,
  ThumbsUp,
  Settings,
  Flame,
  X,
} from 'lucide-react';

const navigation = [
  { name: 'Overview', to: '/', icon: LayoutDashboard },
  { name: 'AI Assistant', to: '/assistant', icon: Sparkles, badge: 'AI' },
  { name: 'Projects', to: '/projects', icon: FolderKanban },
  { name: 'Providers', to: '/providers', icon: Cpu },
  { name: 'Models', to: '/models', icon: Box },
  { name: 'Playground', to: '/playground', icon: Play },
  { name: 'Prompts', to: '/prompts', icon: Code2 },
  { name: 'Traces', to: '/traces', icon: Activity },
  { name: 'Evaluations', to: '/evaluations', icon: CheckCircle2 },
  { name: 'Datasets', to: '/datasets', icon: Database },
  { name: 'Experiments', to: '/experiments', icon: FlaskConical },
  { name: 'Comparisons', to: '/comparisons', icon: Scale },
  { name: 'Analytics', to: '/analytics', icon: BarChart3 },
  { name: 'Alerts', to: '/alerts', icon: Bell },
  { name: 'Feedback', to: '/feedback', icon: ThumbsUp },
  { name: 'Settings', to: '/settings', icon: Settings },
];

interface SidebarProps {
  mobileOpen?: boolean;
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ mobileOpen = false, onClose }) => {
  const content = (
    <div className="flex flex-col h-full bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 select-none">
      {/* Brand Header */}
      <div className="h-16 flex items-center justify-between px-5 border-b border-slate-200 dark:border-slate-800 shrink-0">
        <div className="flex items-center gap-3">
          <div className="h-9 w-9 rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-cyan-400 flex items-center justify-center text-white shadow-md shadow-indigo-500/20 shrink-0">
            <Flame className="w-5 h-5 fill-white text-white" />
          </div>
          <div>
            <span className="font-bold text-lg tracking-tight text-slate-900 dark:text-white flex items-center gap-1">
              Aether<span className="text-indigo-600 dark:text-indigo-400">LLM</span>
            </span>
            <span className="text-[10px] text-slate-500 dark:text-slate-400 tracking-wider uppercase font-semibold block">
              Enterprise LLMOps
            </span>
          </div>
        </div>

        {/* Mobile close button */}
        {onClose && (
          <button
            onClick={onClose}
            className="lg:hidden p-1.5 rounded-lg text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            title="Close Menu"
          >
            <X className="w-5 h-5" />
          </button>
        )}
      </div>

      {/* Nav List */}
      <nav className="flex-1 overflow-y-auto py-3 px-3 space-y-0.5 custom-scrollbar">
        {navigation.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.name}
              to={item.to}
              end={item.to === '/'}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center justify-between px-3 py-2 rounded-lg text-xs sm:text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-indigo-50 dark:bg-indigo-600/15 text-indigo-600 dark:text-indigo-400 border border-indigo-200/80 dark:border-indigo-500/20 font-semibold shadow-xs'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800/60'
                }`
              }
            >
              <div className="flex items-center gap-2.5">
                <Icon className="w-4 h-4 shrink-0" />
                <span className="truncate">{item.name}</span>
              </div>
              {item.badge && (
                <span className="px-1.5 py-0.5 text-[9px] font-bold rounded bg-indigo-100 dark:bg-indigo-500/20 text-indigo-600 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-500/30">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* System Status Footer */}
      <div className="p-3.5 border-t border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/60 shrink-0">
        <div className="flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
          <span className="flex items-center gap-2 font-medium">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            Cluster Online
          </span>
          <span className="font-mono text-[10px] text-slate-400 dark:text-slate-500 font-semibold">v1.0.0</span>
        </div>
      </div>
    </div>
  );

  return (
    <>
      {/* Desktop Persistent Sidebar */}
      <aside className="hidden lg:flex w-64 flex-col h-screen shrink-0 shadow-xs">
        {content}
      </aside>

      {/* Mobile Slide-Over Drawer */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 lg:hidden flex">
          {/* Backdrop */}
          <div
            className="fixed inset-0 bg-slate-950/60 backdrop-blur-xs transition-opacity animate-fade-in"
            onClick={onClose}
          />
          {/* Drawer Panel */}
          <div className="relative w-72 max-w-[80vw] h-full shadow-2xl z-50 animate-slide-in">
            {content}
          </div>
        </div>
      )}
    </>
  );
};
