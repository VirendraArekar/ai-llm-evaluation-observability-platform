import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import { OverviewPage } from './pages/OverviewPage';
import { AssistantPage } from './pages/AssistantPage';
import { ProjectsPage } from './pages/ProjectsPage';
import { ProvidersPage } from './pages/ProvidersPage';
import { ModelsPage } from './pages/ModelsPage';
import { PlaygroundPage } from './pages/PlaygroundPage';
import { PromptsPage } from './pages/PromptsPage';
import { TracesPage } from './pages/TracesPage';
import { TraceDetailPage } from './pages/TraceDetailPage';
import { EvaluationsPage } from './pages/EvaluationsPage';
import { DatasetsPage } from './pages/DatasetsPage';
import { ExperimentsPage } from './pages/ExperimentsPage';
import { ComparisonsPage } from './pages/ComparisonsPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { AlertsPage } from './pages/AlertsPage';
import { FeedbackPage } from './pages/FeedbackPage';
import { SettingsPage } from './pages/SettingsPage';
import { LoginPage } from './pages/LoginPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        {/* Dashboard layout wrapped routes */}
        <Route path="/" element={<AppLayout />}>
          <Route index element={<OverviewPage />} />
          <Route path="assistant" element={<AssistantPage />} />
          <Route path="projects" element={<ProjectsPage />} />
          <Route path="providers" element={<ProvidersPage />} />
          <Route path="models" element={<ModelsPage />} />
          <Route path="playground" element={<PlaygroundPage />} />
          <Route path="prompts" element={<PromptsPage />} />
          <Route path="traces" element={<TracesPage />} />
          <Route path="traces/:id" element={<TraceDetailPage />} />
          <Route path="evaluations" element={<EvaluationsPage />} />
          <Route path="datasets" element={<DatasetsPage />} />
          <Route path="experiments" element={<ExperimentsPage />} />
          <Route path="comparisons" element={<ComparisonsPage />} />
          <Route path="analytics" element={<AnalyticsPage />} />
          <Route path="alerts" element={<AlertsPage />} />
          <Route path="feedback" element={<FeedbackPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
