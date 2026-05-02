import React from 'react';
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
