import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, Legend, ResponsiveContainer
} from 'recharts';

const API = '/api';

// ── Helpers ──────────────────────────────────────────────────────────────────

function metricClass(value) {
  if (value >= 85) return 'critical';
  if (value >= 60) return 'warn';
  return 'ok';
}

function fillClass(value) {
  if (value >= 85) return 'fill-critical';
  if (value >= 60) return 'fill-warn';
  return 'fill-ok';
}

function cardClass(m) {
  if (m.cpuUsage >= 85 || m.ramUsage >= 90 || m.diskUsage >= 95) return 'card critical';
  if (m.cpuUsage >= 60 || m.ramUsage >= 70 || m.diskUsage >= 80) return 'card warning';
  return 'card';
}

function shortId(id) {
  return id && id.length > 18 ? id.substring(0, 18) + '…' : id;
}

function fmtDate(ts) {
  return new Date(ts).toLocaleString('fr-FR');
}

// ── Composant barre de progression ───────────────────────────────────────────

function MetricBar({ label, value }) {
  const cls = metricClass(value);
  return (
    <div className="metric">
      <div className="metric-header">
        <span>{label}</span>
        <span className={`metric-value ${cls}`}>{value.toFixed(1)}%</span>
      </div>
      <div className="progress-bar">
        <div
          className={`progress-fill ${fillClass(value)}`}
          style={{ width: `${Math.min(value, 100)}%` }}
        />
      </div>
    </div>
  );
}

// ── Composant carte agent ─────────────────────────────────────────────────────

function AgentCard({ m }) {
  return (
    <div className={cardClass(m)}>
      <div className="card-title">Agent</div>
      <div className="card-id" title={m.agentId}>{shortId(m.agentId)}</div>
      <MetricBar label="CPU"    value={m.cpuUsage}  />
      <MetricBar label="RAM"    value={m.ramUsage}  />
      <MetricBar label="Disque" value={m.diskUsage} />
    </div>
  );
}

// ── Application principale ────────────────────────────────────────────────────

export default function App() {
  const [metrics,    setMetrics]    = useState({});
  const [alerts,     setAlerts]     = useState([]);
  const [lastUpdate, setLastUpdate] = useState(null);
  const [activeTab,  setActiveTab]  = useState('dashboard');
  const [error,      setError]      = useState(null);

  const fetchData = useCallback(async () => {
    try {
      const [mRes, aRes] = await Promise.all([
        axios.get(`${API}/metrics/latest`),
        axios.get(`${API}/alerts`),
      ]);
      setMetrics(mRes.data || {});
      setAlerts(aRes.data  || []);
      setLastUpdate(new Date().toLocaleTimeString('fr-FR'));
      setError(null);
    } catch (err) {
      setError('Impossible de contacter le serveur (' + (err.message || 'réseau') + ')');
    }
  }, []);

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, [fetchData]);

  // ── Graphique Recharts ────────────────────────────────────────────────────
  const chartData = Object.values(metrics).map(m => ({
    name: shortId(m.agentId),
    CPU:  +m.cpuUsage.toFixed(1),
    RAM:  +m.ramUsage.toFixed(1),
    Disk: +m.diskUsage.toFixed(1),
  }));

  // ── Export ────────────────────────────────────────────────────────────────
  const doExport = async (format) => {
    try {
      const url   = `${API}/export/${format}`;
      const res   = await axios.get(url, { responseType: 'blob' });
      const blob  = new Blob([res.data]);
      const href  = URL.createObjectURL(blob);
      const a     = document.createElement('a');
      a.href      = href;
      a.download  = `metrics.${format}`;
      a.click();
      URL.revokeObjectURL(href);
    } catch (err) {
      alert('Erreur lors de l\'export : ' + (err.response?.statusText || err.message));
    }
  };

  const agentCount = Object.keys(metrics).length;
  const alertCount = alerts.filter(a => a.severity === 'CRITICAL').length;

  // ── Rendu ─────────────────────────────────────────────────────────────────
  return (
    <div className="app">

      {/* En-tête */}
      <header className="header">
        <div>
          <h1>🖥  Système de Surveillance Distribué</h1>
          <div className="subtitle">Monitoring temps réel · UDP / TCP / RMI / REST · Java 17</div>
        </div>
        <div className="meta">
          <div>Agents actifs : <strong>{agentCount}</strong></div>
          <div>Alertes critiques : <strong style={{ color: alertCount > 0 ? '#fca5a5' : '#86efac' }}>{alertCount}</strong></div>
          {lastUpdate && <div style={{ marginTop: 4 }}>Mise à jour : {lastUpdate}</div>}
        </div>
      </header>

      {/* Navigation */}
      <nav>
        {[
          { id: 'dashboard', label: '📊  Tableau de bord' },
          { id: 'alerts',    label: '🚨  Alertes (' + alerts.length + ')' },
          { id: 'export',    label: '📤  Export' },
        ].map(tab => (
          <button
            key={tab.id}
            className={activeTab === tab.id ? 'active' : ''}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      {/* Contenu principal */}
      <main>

        {/* Bannière d'erreur */}
        {error && (
          <div style={{
            background: '#fef2f2', border: '1px solid #fca5a5',
            borderRadius: 8, padding: '0.8rem 1.2rem', marginBottom: '1rem',
            color: '#dc2626', fontWeight: 600
          }}>
            ⚠  {error}
          </div>
        )}

        {/* ── Onglet Tableau de bord ── */}
        {activeTab === 'dashboard' && (
          <>
            {agentCount === 0 ? (
              <div className="empty">
                ⏳ En attente de données…<br />
                <small style={{ color: '#cbd5e1' }}>
                  Démarrez un ou plusieurs agents pour voir les métriques.
                </small>
              </div>
            ) : (
              <>
                {/* Cartes agents */}
                <div className="cards-grid">
                  {Object.values(metrics).map(m => (
                    <AgentCard key={m.agentId} m={m} />
                  ))}
                </div>

                {/* Graphique comparatif */}
                <div className="chart-container">
                  <h2>Comparaison des ressources par agent</h2>
                  <ResponsiveContainer width="100%" height={280}>
                    <BarChart data={chartData} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                      <YAxis domain={[0, 100]} tickFormatter={v => v + '%'} />
                      <Tooltip formatter={(v) => v + '%'} />
                      <Legend />
                      <Bar dataKey="CPU"  fill="#3b82f6" radius={[4,4,0,0]} />
                      <Bar dataKey="RAM"  fill="#8b5cf6" radius={[4,4,0,0]} />
                      <Bar dataKey="Disk" fill="#06b6d4" radius={[4,4,0,0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </>
            )}
          </>
        )}

        {/* ── Onglet Alertes ── */}
        {activeTab === 'alerts' && (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Agent ID</th>
                  <th>Type</th>
                  <th>Sévérité</th>
                  <th>Timestamp</th>
                </tr>
              </thead>
              <tbody>
                {[...alerts].reverse().map((a, i) => (
                  <tr key={a.id || i}
                    className={a.severity === 'CRITICAL' ? 'row-critical' : 'row-warning'}>
                    <td title={a.agentId}>{shortId(a.agentId)}</td>
                    <td><strong>{a.type}</strong></td>
                    <td>
                      <span className={`badge badge-${a.severity?.toLowerCase()}`}>
                        {a.severity}
                      </span>
                    </td>
                    <td>{fmtDate(a.timestamp)}</td>
                  </tr>
                ))}
                {alerts.length === 0 && (
                  <tr>
                    <td colSpan={4} style={{ textAlign: 'center', padding: '2.5rem', color: '#94a3b8' }}>
                      ✅  Aucune alerte enregistrée
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}

        {/* ── Onglet Export ── */}
        {activeTab === 'export' && (
          <div className="export-panel">
            <h2>📤  Export des données</h2>
            <div className="export-buttons">
              <button className="btn btn-green" onClick={() => doExport('csv')}>
                📄  Exporter CSV
              </button>
              <button className="btn btn-blue" onClick={() => doExport('json')}>
                📋  Exporter JSON
              </button>
            </div>
            <p className="export-hint">
              Télécharge toutes les métriques reçues depuis le démarrage du serveur.<br />
              Endpoints utilisés : <code>GET /api/export/csv</code> · <code>GET /api/export/json</code>
            </p>
          </div>
        )}

      </main>
    </div>
  );
}
