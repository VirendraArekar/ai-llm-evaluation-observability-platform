import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setSelectedProjectId } from '../../store/authSlice';
import { Bell, Sparkles, ChevronDown, Check, LogOut, Sun, Moon, Zap, RefreshCw, Menu } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { api } from '../../api/services';

interface NavbarProps {
  onToggleMobileMenu?: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onToggleMobileMenu }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user, projects, selectedProjectId, selectedProject } = useSelector((state: RootState) => state.auth);
  const [showProjectDropdown, setShowProjectDropdown] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [isDark, setIsDark] = useState(false);
  const [simulating, setSimulating] = useState(false);
  const [autoSimulate, setAutoSimulate] = useState(false);
  const [lastTelemetryMessage, setLastTelemetryMessage] = useState<string | null>(null);

  // Sync theme with document class
  useEffect(() => {
    const isDarkCurrent = document.documentElement.classList.contains('dark');
    setIsDark(isDarkCurrent);
  }, []);

  const toggleTheme = () => {
    const nextDark = !isDark;
    setIsDark(nextDark);
    if (nextDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('aether_theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('aether_theme', 'light');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('aether_token');
    window.location.href = '/login';
  };

  const handleSimulate = async () => {
    setSimulating(true);
    try {
      const res = await api.simulateTelemetry(selectedProjectId);
      const trace = res;
      const msg = `⚡ Ingested: ${trace.name || 'Trace'} (${trace.latencyMs}ms, ${trace.totalTokens} tokens)`;
      setLastTelemetryMessage(msg);
      window.dispatchEvent(new CustomEvent('aether:telemetry:updated', { detail: trace }));
      setTimeout(() => setLastTelemetryMessage(null), 4000);
    } catch (e) {
      console.error('Failed to simulate telemetry', e);
    } finally {
      setSimulating(false);
    }
  };

  // Auto-stream telemetry interval
  useEffect(() => {
    if (!autoSimulate) return;
    const interval = setInterval(() => {
      handleSimulate();
    }, 12000);
    return () => clearInterval(interval);
  }, [autoSimulate, selectedProjectId]);

  return (
    <header className="h-16 bg-white/90 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200 dark:border-slate-800 px-3 sm:px-6 flex items-center justify-between z-20 shrink-0 transition-colors">
      {/* Left Area: Mobile Hamburger + Project Selector */}
      <div className="flex items-center gap-2 sm:gap-4 min-w-0">
        {/* Mobile Hamburger Menu Toggle */}
        <button
          onClick={onToggleMobileMenu}
          className="lg:hidden p-2 rounded-lg text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          title="Open Navigation Menu"
        >
          <Menu className="w-5 h-5" />
        </button>

        {/* Project Selector */}
        <div className="relative">
          <button
            onClick={() => setShowProjectDropdown(!showProjectDropdown)}
            className="flex items-center gap-2 px-2.5 sm:px-3.5 py-1.5 rounded-lg bg-slate-100 dark:bg-slate-800/80 hover:bg-slate-200 dark:hover:bg-slate-800 border border-slate-300 dark:border-slate-700/60 text-xs sm:text-sm font-medium text-slate-800 dark:text-slate-200 transition-colors shadow-xs"
          >
            <span className="w-2 h-2 rounded-full bg-indigo-500 animate-pulse shrink-0" />
            <span className="max-w-[110px] sm:max-w-[180px] truncate">{selectedProject?.name || 'Customer Support Agent'}</span>
            <ChevronDown className="w-3.5 h-3.5 text-slate-500 dark:text-slate-400 shrink-0" />
          </button>

          {showProjectDropdown && (
            <div className="absolute left-0 mt-2 w-64 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-2xl py-1 z-50">
              <div className="px-3 py-2 text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider border-b border-slate-200 dark:border-slate-800">
                Active Projects
              </div>
              {projects.length > 0 ? (
                projects.map((p) => (
                  <button
                    key={p.id}
                    onClick={() => {
                      dispatch(setSelectedProjectId(p.id));
                      setShowProjectDropdown(false);
                    }}
                    className="w-full flex items-center justify-between px-3 py-2.5 text-xs sm:text-sm text-left hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 transition-colors"
                  >
                    <span className="truncate">{p.name}</span>
                    {p.id === selectedProjectId && <Check className="w-4 h-4 text-indigo-600 dark:text-indigo-400 shrink-0" />}
                  </button>
                ))
              ) : (
                <div className="px-3 py-2 text-xs text-slate-500">Default Project</div>
              )}
            </div>
          )}
        </div>

        {/* Live Ingestion Toast Notification */}
        {lastTelemetryMessage && (
          <div className="hidden xl:flex items-center gap-2 px-3 py-1 bg-emerald-500/10 border border-emerald-500/30 text-emerald-600 dark:text-emerald-400 text-xs font-mono rounded-lg animate-fade-in shadow-xs">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-ping" />
            <span>{lastTelemetryMessage}</span>
          </div>
        )}
      </div>

      {/* Right Area Actions */}
      <div className="flex items-center gap-1.5 sm:gap-3 shrink-0">
        {/* Simulate Live Telemetry Button */}
        <button
          onClick={handleSimulate}
          disabled={simulating}
          className="flex items-center gap-1.5 px-2.5 sm:px-3 py-1.5 rounded-lg bg-emerald-50 dark:bg-emerald-600/20 hover:bg-emerald-100 dark:hover:bg-emerald-600/30 text-emerald-700 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-500/30 text-xs font-semibold transition-all shadow-xs active:scale-95 disabled:opacity-50"
          title="Click to instantly generate and ingest a live realistic production trace with spans and evaluations"
        >
          <Zap className={`w-3.5 h-3.5 text-emerald-600 dark:text-emerald-400 shrink-0 ${simulating ? 'animate-spin' : ''}`} />
          <span className="hidden sm:inline">{simulating ? 'Ingesting...' : 'Simulate Traffic'}</span>
          <span className="sm:hidden">{simulating ? '...' : 'Simulate'}</span>
        </button>

        {/* Auto-Stream Toggle */}
        <button
          onClick={() => setAutoSimulate(!autoSimulate)}
          className={`hidden md:flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium border transition-colors ${
            autoSimulate
              ? 'bg-indigo-50 dark:bg-indigo-600/20 border-indigo-200 dark:border-indigo-500/40 text-indigo-700 dark:text-indigo-300'
              : 'bg-slate-100 dark:bg-slate-800/60 border-slate-300 dark:border-slate-700/60 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
          }`}
          title="Toggle continuous real-time background telemetry streaming"
        >
          <RefreshCw className={`w-3 h-3 ${autoSimulate ? 'animate-spin text-indigo-600 dark:text-indigo-400' : ''}`} />
          <span>Auto {autoSimulate ? 'ON' : 'OFF'}</span>
        </button>

        {/* Quick Launch Playground */}
        <button
          onClick={() => navigate('/playground')}
          className="hidden sm:flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold shadow-xs shadow-indigo-600/20 transition-colors"
        >
          <Sparkles className="w-3.5 h-3.5" />
          <span>Playground</span>
        </button>

        {/* Theme Toggle Button */}
        <button
          onClick={toggleTheme}
          className="p-2 rounded-lg text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800/80 transition-colors"
          title={`Switch to ${isDark ? 'Light' : 'Dark'} Mode`}
        >
          {isDark ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-indigo-600" />}
        </button>

        {/* Notifications Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="p-2 rounded-lg text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800/80 transition-colors relative"
            title="System Notifications"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-indigo-500" />
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-2 w-72 sm:w-80 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl shadow-2xl p-3 z-50">
              <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-200 dark:border-slate-800">
                <span className="text-xs font-bold text-slate-800 dark:text-slate-200">System Notifications</span>
                <span className="text-[10px] text-indigo-600 dark:text-indigo-400 cursor-pointer hover:underline">Mark all read</span>
              </div>
              <div className="space-y-2 text-xs">
                <div className="p-2 rounded-lg bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800">
                  <p className="font-semibold text-slate-800 dark:text-slate-200">Regression Benchmark Passed</p>
                  <p className="text-slate-500 dark:text-slate-400 text-[11px] mt-0.5">Prompt v2 achieved 96.8% accuracy.</p>
                </div>
                <div className="p-2 rounded-lg bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800">
                  <p className="font-semibold text-slate-800 dark:text-slate-200">Cost Optimization Alert</p>
                  <p className="text-slate-500 dark:text-slate-400 text-[11px] mt-0.5">Migrating triage to GPT-4o-mini saves $68/mo.</p>
                </div>
                <div className="p-2 rounded-lg bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800">
                  <p className="font-semibold text-slate-800 dark:text-slate-200">Cluster Telemetry Online</p>
                  <p className="text-slate-500 dark:text-slate-400 text-[11px] mt-0.5">Real-time inference tracing active.</p>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* User Profile */}
        <div className="flex items-center gap-2 pl-2 sm:pl-3 border-l border-slate-200 dark:border-slate-800">
          <div className="w-7 h-7 sm:w-8 sm:h-8 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-600 flex items-center justify-center text-xs font-bold text-white shadow-xs shrink-0">
            {user?.fullName ? user.fullName.charAt(0) : 'A'}
          </div>
          <div className="hidden md:block text-left">
            <p className="text-xs font-semibold text-slate-800 dark:text-slate-200 leading-none truncate max-w-[120px]">
              {user?.fullName || 'Dr. Alex Vance'}
            </p>
            <p className="text-[10px] text-slate-500 dark:text-slate-400 mt-0.5 leading-none font-mono">
              {user?.role || 'SUPER_ADMIN'}
            </p>
          </div>
          <button
            onClick={handleLogout}
            className="p-1 text-slate-400 hover:text-rose-500 transition-colors ml-0.5"
            title="Log Out"
          >
            <LogOut className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </header>
  );
};
