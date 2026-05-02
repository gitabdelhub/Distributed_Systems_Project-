import React from 'react';
<<<<<<< HEAD
import './MetricsTable.css';

const MetricsTable = ({ metrics }) => {
  const getUsageColor = (value) => {
    if (value < 30) return 'low';
    if (value < 70) return 'medium';
    return 'high';
  };

  const formatAgentId = (id) => {
    if (!id) return 'N/A';
    return id.length > 8 ? id.substring(0, 8) + '...' : id;
  };

  const formatTime = (timestamp) => {
    if (!timestamp) return 'N/A';
    return new Date(timestamp).toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit', 
      second: '2-digit' 
    });
  };

  return (
    <div className="metrics-table-container">
      <h2>Métriques en temps réel</h2>
      <div className="table-wrapper">
        <table className="metrics-table">
          <thead>
            <tr>
              <th>AGENT ID</th>
              <th>CPU</th>
              <th>RAM</th>
              <th>DISQUE</th>
              <th>DERNIÈRE MISE À JOUR</th>
            </tr>
          </thead>
          <tbody>
            {metrics.length === 0 ? (
              <tr>
                <td colSpan="5" className="no-data">Aucune donnée disponible</td>
              </tr>
            ) : (
              metrics.map((metric, index) => (
                <tr key={index}>
                  <td className="agent-id">{formatAgentId(metric.agentId)}</td>
                  <td className={`usage ${getUsageColor(metric.cpu)}`}>
                    {metric.cpu.toFixed(1)}%
                  </td>
                  <td className={`usage ${getUsageColor(metric.ram)}`}>
                    {metric.ram.toFixed(1)}%
                  </td>
                  <td className={`usage ${getUsageColor(metric.disk)}`}>
                    {metric.disk.toFixed(1)}%
                  </td>
                  <td className="timestamp">{formatTime(metric.timestamp)}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default MetricsTable;
=======
import { CPU_CRITICAL, RAM_CRITICAL, DISK_CRITICAL } from '../constants';

function badgeColor(value, critical) {
  const warning = critical * 0.8;
  if (value >= critical) return 'bg-red-500 text-white';
  if (value >= warning)  return 'bg-yellow-400 text-black';
  return 'bg-green-500 text-white';
}

function MetricBadge({ value, critical }) {
  const label = typeof value === 'number' ? value.toFixed(1) + '%' : '—';
  return (
    <span className={`px-2 py-0.5 rounded text-xs font-semibold ${badgeColor(value, critical)}`}>
      {label}
    </span>
  );
}

export default function MetricsTable({ metrics }) {
  const rows = Object.values(metrics);

  if (rows.length === 0) {
    return <p className="text-gray-500 text-sm mt-4">Aucun agent connecté.</p>;
  }

  return (
    <div className="overflow-x-auto mt-4">
      <table className="min-w-full text-sm border rounded-lg overflow-hidden">
        <thead className="bg-gray-100 text-left text-gray-700 uppercase text-xs">
          <tr>
            <th className="px-4 py-3">Agent ID</th>
            <th className="px-4 py-3">CPU</th>
            <th className="px-4 py-3">RAM</th>
            <th className="px-4 py-3">Disque</th>
            <th className="px-4 py-3">Dernière mise à jour</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">
          {rows.map(m => (
            <tr key={m.agentId} className="hover:bg-gray-50">
              <td className="px-4 py-2 font-mono text-xs text-gray-600">
                {m.agentId.substring(0, 8)}…
              </td>
              <td className="px-4 py-2">
                <MetricBadge value={m.cpuUsage} critical={CPU_CRITICAL} />
              </td>
              <td className="px-4 py-2">
                <MetricBadge value={m.ramUsage} critical={RAM_CRITICAL} />
              </td>
              <td className="px-4 py-2">
                <MetricBadge value={m.diskUsage} critical={DISK_CRITICAL} />
              </td>
              <td className="px-4 py-2 text-gray-500">
                {new Date(m.timestamp).toLocaleTimeString()}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
>>>>>>> 571da7956945e98c1c15a481251ea9217044e674
