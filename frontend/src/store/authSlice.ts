import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import * as T from '../types';

interface AuthState {
  token: string | null;
  user: T.User | null;
  selectedProjectId: string;
  selectedProject: T.Project | null;
  currentProject: T.Project | null;
  currentWorkspace: T.Workspace | null;
  projects: T.Project[];
}

const defaultSeedProject: T.Project = {
  id: 'b4ff1487-1c49-48b3-a781-f91740987a72',
  workspaceId: 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d',
  name: 'Customer Support Agent',
  slug: 'customer-support-agent',
  description: 'Autonomous support triage and resolution LLM pipeline',
  createdAt: new Date().toISOString(),
};

const initialState: AuthState = {
  token: localStorage.getItem('aether_token'),
  user: null,
  selectedProjectId: localStorage.getItem('aether_project_id') || defaultSeedProject.id,
  selectedProject: defaultSeedProject,
  currentProject: defaultSeedProject,
  currentWorkspace: {
    id: 'ws-prod-001',
    organizationId: 'org-acme-001',
    name: 'Production Workspace',
    slug: 'production',
    description: 'Production workspace for customer-facing AI agents',
    createdAt: new Date().toISOString(),
  },
  projects: [defaultSeedProject],
};

export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setAuth: (state, action: PayloadAction<{ token: string; user: T.User; defaultProjectId?: string }>) => {
      state.token = action.payload.token;
      state.user = action.payload.user;
      localStorage.setItem('aether_token', action.payload.token);
      if (action.payload.defaultProjectId) {
        state.selectedProjectId = action.payload.defaultProjectId;
        localStorage.setItem('aether_project_id', action.payload.defaultProjectId);
      }
    },
    setCredentials: (state, action: PayloadAction<{ token?: string; accessToken?: string; user: T.User }>) => {
      const tok = action.payload.token || action.payload.accessToken || '';
      state.token = tok;
      state.user = action.payload.user;
      localStorage.setItem('aether_token', tok);
    },
    logout: (state) => {
      state.token = null;
      state.user = null;
      state.selectedProjectId = '';
      state.selectedProject = null;
      state.currentProject = null;
      state.projects = [];
      localStorage.removeItem('aether_token');
      localStorage.removeItem('aether_project_id');
    },
    setSelectedProjectId: (state, action: PayloadAction<string>) => {
      state.selectedProjectId = action.payload;
      localStorage.setItem('aether_project_id', action.payload);
      const match = state.projects.find((p) => p.id === action.payload) || null;
      state.selectedProject = match;
      state.currentProject = match;
    },
    setCurrentProject: (state, action: PayloadAction<T.Project>) => {
      state.currentProject = action.payload;
      state.selectedProject = action.payload;
      state.selectedProjectId = action.payload.id;
      localStorage.setItem('aether_project_id', action.payload.id);
    },
    setProjects: (state, action: PayloadAction<T.Project[]>) => {
      state.projects = action.payload;
      if (action.payload && action.payload.length > 0) {
        const found = action.payload.find((p) => p.id === state.selectedProjectId) || action.payload[0];
        state.selectedProjectId = found.id;
        state.selectedProject = found;
        state.currentProject = found;
        localStorage.setItem('aether_project_id', found.id);
      }
    },
  },
});

export const { setAuth, setCredentials, logout, setSelectedProjectId, setCurrentProject, setProjects } = authSlice.actions;
export default authSlice.reducer;
