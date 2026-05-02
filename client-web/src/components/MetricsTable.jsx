import React from 'react';
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
