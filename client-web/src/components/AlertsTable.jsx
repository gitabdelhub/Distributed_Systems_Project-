import React from 'react';

function severityClass(severity) {
  if (severity === 'CRITICAL') return 'bg-red-100 text-red-800';
  if (severity === 'WARNING')  return 'bg-yellow-100 text-yellow-800';
  return 'bg-gray-100 text-gray-800';
}

export default function AlertsTable({ alerts }) {
  if (alerts.length === 0) {
    return <p className="text-gray-500 text-sm mt-4">Aucune alerte enregistrée.</p>;
  }

  return (
    <div className="overflow-x-auto mt-4">
      <table className="min-w-full text-sm border rounded-lg overflow-hidden">
        <thead className="bg-gray-100 text-left text-gray-700 uppercase text-xs">
          <tr>
            <th className="px-4 py-3">ID</th>
            <th className="px-4 py-3">Agent</th>
            <th className="px-4 py-3">Type</th>
            <th className="px-4 py-3">Sévérité</th>
            <th className="px-4 py-3">Horodatage</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">
          {alerts.map(a => (
            <tr key={a.id} className="hover:bg-gray-50">
              <td className="px-4 py-2 font-mono text-xs text-gray-500">
                {a.id.substring(0, 8)}…
              </td>
              <td className="px-4 py-2 font-mono text-xs">
                {a.agentId.substring(0, 8)}…
              </td>
              <td className="px-4 py-2 font-semibold">{a.type}</td>
              <td className="px-4 py-2">
                <span className={`px-2 py-0.5 rounded text-xs font-semibold ${severityClass(a.severity)}`}>
                  {a.severity}
                </span>
              </td>
              <td className="px-4 py-2 text-gray-500">
                {new Date(a.timestamp).toLocaleString()}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
