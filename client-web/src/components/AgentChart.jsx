import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

export default function AgentChart({ agents }) {
  const data = agents.map(agent => ({
    name: agent.name,
    cpu: agent.metrics?.cpu || 0,
    ram: agent.metrics?.ram || 0,
    disk: agent.metrics?.disk || 0,
  }));

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <h2 className="text-lg font-medium text-gray-900 mb-4">Agent Performance History</h2>
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="cpu" stroke="#ef4444" name="CPU %" />
          <Line type="monotone" dataKey="ram" stroke="#3b82f6" name="RAM %" />
          <Line type="monotone" dataKey="disk" stroke="#10b981" name="Disk %" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
