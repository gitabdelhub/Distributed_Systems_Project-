import React, { useState, useEffect, useCallback } from 'react';
import MetricsTable from './components/MetricsTable';
import AlertsTable from './components/AlertsTable';
import AgentChart from './components/AgentChart';
import { fetchLatestMetrics, fetchAlerts, fetchAgents, exportFile } from './api/api';

const TABS = ['Vue générale', 'Historique', 'Alertes'];
const REFRESH_INTERVAL = 5000;

function ExportModal({ onClose, onConfirm }) {
  const [user, setUser] = useState('admin');
  const [pass, setPass] = useState('');
  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-80">
        <h3 className="text-base font-semibold text-gray-800 mb-4">Authentification Admin</h3>
        <label className="block text-sm text-gray-600 mb-1">Nom d'utilisateur</label>
        <input
          className="w-full border rounded px-3 py-1.5 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={user}
          onChange={e => setUser(e.target.value)}
        />
        <label className="block text-sm text-gray-600 mb-1">Mot de passe</label>
        <input
          type="password"
          className="w-full border rounded px-3 py-1.5 text-sm mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={pass}
          onChange={e => setPass(e.target.value)}
        />
        <div className="flex justify-end gap-2">
          <button onClick={onClose} className="text-sm px-3 py-1.5 rounded border hover:bg-gray-50">
            Annuler
          </button>
          <button
            onClick={() => onConfirm(user, pass)}
            className="text-sm px-3 py-1.5 rounded bg-blue-600 text-white hover:bg-blue-700"
          >
            Exporter
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
  const [selectedAgent, setSelectedAgent] = useState('');
  const [lastUpdate, setLastUpdate] = useState(null);
  const [error, setError] = useState(null);
  const [exportModal, setExportModal] = useState(null); // 'csv' | 'json' | null

  const loadData = useCallback(() => {
    Promise.all([fetchLatestMetrics(), fetchAlerts(), fetchAgents()])
      .then(([m, a, ag]) => {
        setMetrics(m);
        setAlerts(a);
        setAgents(ag);
        setLastUpdate(new Date());
        setError(null);
        if (!selectedAgent && ag.length > 0) setSelectedAgent(ag[0]);
      })
      .catch(err => setError('Impossible de contacter le serveur : ' + err.message));
  }, [selectedAgent]);

  useEffect(() => {
    loadData();
    const id = setInterval(loadData, REFRESH_INTERVAL);
    return () => clearInterval(id);
  }, [loadData]);

  const handleExportConfirm = (user, pass) => {
    const type = exportModal;
    setExportModal(null);
    exportFile(type, user, pass).catch(err =>
      setError('Erreur export : ' + (err.response?.status === 401 ? 'Accès refusé (vérifiez vos identifiants)' : err.message))
    );
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {exportModal && (
        <ExportModal
          onClose={() => setExportModal(null)}
          onConfirm={handleExportConfirm}
        />
      )}

      {/* Header */}
      <header className="bg-blue-700 text-white px-6 py-4 shadow">
        <div className="flex items-center justify-between max-w-6xl mx-auto">
          <div>
            <h1 className="text-xl font-bold">🌐 Système de Surveillance Distribué</h1>
            <p className="text-blue-200 text-xs mt-0.5">
              {agents.length} agent(s) connecté(s)
              {lastUpdate && ` · Mis à jour à ${lastUpdate.toLocaleTimeString()}`}
            </p>
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => setExportModal('csv')}
              className="bg-white text-blue-700 text-xs font-semibold px-3 py-1.5 rounded hover:bg-blue-50 transition"
            >
              📥 Export CSV
            </button>
            <button
              onClick={() => setExportModal('json')}
              className="bg-white text-blue-700 text-xs font-semibold px-3 py-1.5 rounded hover:bg-blue-50 transition"
            >
              📥 Export JSON
            </button>
          </div>
        </div>
      </header>

      {/* Error banner */}
      {error && (
        <div className="bg-red-100 border-l-4 border-red-500 text-red-700 px-6 py-3 text-sm max-w-6xl mx-auto mt-4 rounded">
          ⚠️ {error}
        </div>
      )}

      {/* Tabs */}
      <div className="max-w-6xl mx-auto px-6 mt-6">
        <div className="flex gap-1 border-b border-gray-200">
          {TABS.map((t, i) => (
            <button
              key={t}
              onClick={() => setTab(i)}
              className={`px-5 py-2.5 text-sm font-medium transition rounded-t ${
                tab === i
                  ? 'bg-white border border-b-0 border-gray-200 text-blue-700'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {t}
              {t === 'Alertes' && alerts.length > 0 && (
                <span className="ml-1.5 bg-red-500 text-white text-xs px-1.5 py-0.5 rounded-full">
                  {alerts.length}
                </span>
              )}
            </button>
          ))}
        </div>

        {/* Tab content */}
        <div className="bg-white border border-t-0 border-gray-200 rounded-b p-5 shadow-sm">
          {tab === 0 && (
            <>
              <h2 className="text-base font-semibold text-gray-700">
                Métriques en temps réel
              </h2>
              <MetricsTable metrics={metrics} />
            </>
          )}

          {tab === 1 && (
            <>
              <h2 className="text-base font-semibold text-gray-700 mb-3">
                Historique par agent
              </h2>
              {agents.length > 0 ? (
                <>
                  <div className="flex items-center gap-3">
                    <label className="text-sm text-gray-600 font-medium">Agent :</label>
                    <select
                      value={selectedAgent}
                      onChange={e => setSelectedAgent(e.target.value)}
                      className="border rounded px-3 py-1.5 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400"
                    >
                      {agents.map(a => (
                        <option key={a} value={a}>{a.substring(0, 12)}…</option>
                      ))}
                    </select>
                  </div>
                  <AgentChart agentId={selectedAgent} />
                </>
              ) : (
                <p className="text-gray-500 text-sm mt-2">Aucun agent disponible.</p>
              )}
            </>
          )}

          {tab === 2 && (
            <>
              <h2 className="text-base font-semibold text-gray-700">
                Alertes ({alerts.length})
              </h2>
              <AlertsTable alerts={alerts} />
            </>
          )}
        </div>
      </div>

      {/* Footer */}
      <footer className="text-center text-xs text-gray-400 mt-8 pb-6">
        Distributed Monitoring System · Systèmes Distribués 2025-2026
      </footer>
    </div>
  );
}
