import React, { useState, useEffect, useCallback } from 'react';
import MetricsTable from './components/MetricsTable';
import AlertsTable from './components/AlertsTable';
import AgentChart from './components/AgentChart';
import ThresholdConfig from './components/ThresholdConfig';
import { fetchLatestMetrics, fetchAlerts, fetchAgents, exportFile, getThresholds, updateThresholds } from './api/api';

const TABS = ['Overview', 'History', 'Alerts', 'Configuration'];
const REFRESH_INTERVAL = 5000;

function ExportModal({ onClose, onConfirm }) {
  const [user, setUser] = useState('admin');
  const [pass, setPass] = useState('');
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-80">
        <h3 className="text-base font-semibold text-gray-800 mb-4">Admin Authentication</h3>
        <label className="block text-sm text-gray-600 mb-1">Username</label>
        <input
          className="w-full border rounded px-3 py-1.5 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={user}
          onChange={e => setUser(e.target.value)}
        />
        <label className="block text-sm text-gray-600 mb-1">Password</label>
        <input
          type="password"
          className="w-full border rounded px-3 py-1.5 text-sm mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={pass}
          onChange={e => setPass(e.target.value)}
        />
        <div className="flex justify-end gap-2">
          <button onClick={onClose} className="text-sm px-3 py-1.5 rounded border hover:bg-gray-50">
            Cancel
          </button>
          <button
            onClick={() => onConfirm(user, pass)}
            className="text-sm px-3 py-1.5 rounded bg-blue-600 text-white hover:bg-blue-700"
          >
            Export
          </button>
        </div>
      </div>
    </div>
  );
}

export default function App() {
  const [tab, setTab] = useState(0);
  const [metrics, setMetrics] = useState({});
  const [alerts, setAlerts] = useState([]);
  const [agents, setAgents] = useState([]);
  const [showExportModal, setShowExportModal] = useState(false);
  const [thresholds, setThresholds] = useState({});

  const fetchAll = useCallback(async () => {
    try {
      const [metricsRes, alertsRes, agentsRes, thresholdsRes] = await Promise.all([
        fetchLatestMetrics(),
        fetchAlerts(),
        fetchAgents(),
        getThresholds()
      ]);
      setMetrics(metricsRes);
      setAlerts(alertsRes);
      setAgents(agentsRes);
      setThresholds(thresholdsRes);
    } catch (err) {
      console.error('Failed to fetch data:', err);
    }
  }, []);

  useEffect(() => {
    fetchAll();
    const interval = setInterval(fetchAll, REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [fetchAll]);

  const handleExport = async (username, password) => {
    try {
      await exportFile('csv', username, password);
      setShowExportModal(false);
    } catch (err) {
      alert('Export failed');
    }
  };

  return (
    <div className="app">
      <header className="bg-blue-600 text-white p-4 shadow">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">Distributed Monitoring System</h1>
          <button
            onClick={() => setShowExportModal(true)}
            className="bg-blue-500 hover:bg-blue-400 px-4 py-2 rounded text-sm font-medium"
          >
            Export Data
          </button>
        </div>
      </header>

      <div className="flex border-b">
        {TABS.map((t, i) => (
          <button
            key={t}
            onClick={() => setTab(i)}
            className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
              tab === i
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            {t}
            {t === 'Alerts' && alerts.length > 0 && (
              <span className="ml-2 bg-red-500 text-white text-xs px-2 py-1 rounded-full">
                {alerts.length}
              </span>
            )}
          </button>
        ))}
      </div>

      <main className="main-content p-6">
        {tab === 0 && <MetricsTable metrics={metrics} />}
        {tab === 1 && <AgentChart agents={agents} />}
        {tab === 2 && <AlertsTable alerts={alerts} />}
        {tab === 3 && <ThresholdConfig thresholds={thresholds} onUpdate={updateThresholds} />}
      </main>

      {showExportModal && (
        <ExportModal
          onClose={() => setShowExportModal(false)}
          onConfirm={handleExport}
        />
      )}
    </div>
  );
}
