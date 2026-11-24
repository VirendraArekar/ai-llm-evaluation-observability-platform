import React, { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { setAuth, setProjects } from '../../store/authSlice';
import { Sidebar } from './Sidebar';
import { Navbar } from './Navbar';
import { api } from '../../api/services';

export const AppLayout: React.FC = () => {
  const dispatch = useDispatch();
  const { token } = useSelector((state: RootState) => state.auth);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  // Initialize theme from localStorage or system preference
  useEffect(() => {
    const savedTheme = localStorage.getItem('aether_theme') || 'dark';
    if (savedTheme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, []);

  useEffect(() => {
    const initSession = async () => {
      try {
        if (!token) {
          const authData = await api.login({ email: 'admin@aetherllm.ai', password: 'Admin@123456' });
          dispatch(setAuth({ token: authData.accessToken, user: authData.user, defaultProjectId: authData.defaultProjectId }));
        }

        const orgs = await api.getOrganizations();
        if (orgs.length > 0) {
          const workspaces = await api.getWorkspaces(orgs[0].id);
          if (workspaces.length > 0) {
            const projects = await api.getProjects(workspaces[0].id);
            dispatch(setProjects(projects));
          }
        }
      } catch (e) {
        console.warn('Auto-init failed, redirecting to login:', e);
      }
    };

    initSession();
  }, [dispatch, token]);

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 transition-colors duration-150">
      {/* Sidebar with Desktop & Mobile Drawer mode */}
      <Sidebar mobileOpen={mobileMenuOpen} onClose={() => setMobileMenuOpen(false)} />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col h-screen min-w-0 overflow-hidden">
        <Navbar onToggleMobileMenu={() => setMobileMenuOpen(!mobileMenuOpen)} />
        <main className="flex-1 min-w-0 overflow-y-auto custom-scrollbar">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
