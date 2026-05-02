import React, { useState, useEffect } from 'react';
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import { fetchHistory } from '../api/api';

export default function AgentChart({ agentId }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!agentId) return;
    setLoading(true);
    fetchHistory(agentId)
      .then(data => {
        const formatted = data.map(m => ({
          time: new Date(m.timestamp).toLocaleTimeString(),
          CPU: Math.round(m.cpuUsage * 10) / 10,
          RAM: Math.round(m.ramUsage * 10) / 10,
          Disque: Math.round(m.diskUsage * 10) / 10,
        }));
        setHistory(formatted);
      })
      .catch(() => setHistory([]))
      .finally(() => setLoading(false));
  }, [agentId]);

  if (!agentId) return <p className="text-gray-500 text-sm mt-4">Sélectionnez un agent.</p>;
  if (loading)   return <p className="text-gray-400 text-sm mt-4">Chargement…</p>;
  if (history.length === 0) return <p className="text-gray-500 text-sm mt-4">Aucun historique disponible.</p>;

  return (
    <div className="mt-4">
      <p className="text-xs text-gray-500 mb-2 font-mono">Agent : {agentId}</p>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={history} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="time" tick={{ fontSize: 11 }} />
          <YAxis domain={[0, 100]} unit="%" tick={{ fontSize: 11 }} />
          <Tooltip formatter={(v) => v + '%'} />
          <Legend />
          <Line type="monotone" dataKey="CPU"    stroke="#ef4444" dot={false} strokeWidth={2} />
          <Line type="monotone" dataKey="RAM"    stroke="#3b82f6" dot={false} strokeWidth={2} />
          <Line type="monotone" dataKey="Disque" stroke="#f59e0b" dot={false} strokeWidth={2} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
