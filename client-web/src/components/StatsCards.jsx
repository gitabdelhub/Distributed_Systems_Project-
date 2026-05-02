import React from 'react';
import './StatsCards.css';

const StatsCards = ({ metrics, alerts }) => {
  // Calculate statistics
  const totalAgents = new Set(metrics.map(m => m.agentId)).size;
  const criticalAlerts = alerts.filter(a => a.severity === 'CRITICAL').length;
  const avgCpu = metrics.length > 0 
    ? (metrics.reduce((sum, m) => sum + m.cpu, 0) / metrics.length).toFixed(1)
    : 0;
  const avgRam = metrics.length > 0
    ? (metrics.reduce((sum, m) => sum + m.ram, 0) / metrics.length).toFixed(1)
    : 0;

  return (
    <div className="stats-cards">
      <div className="stat-card">
        <div className="stat-title">Active Agents</div>
        <div className="stat-value">{totalAgents}</div>
        <div className="stat-description">Connected agents</div>
      </div>
      
      <div className="stat-card">
        <div className="stat-title">Average CPU</div>
        <div className="stat-value">{avgCpu}%</div>
        <div className="stat-description">System usage</div>
      </div>
      
      <div className="stat-card">
        <div className="stat-title">Average RAM</div>
        <div className="stat-value">{avgRam}%</div>
        <div className="stat-description">Memory usage</div>
      </div>
      
      <div className="stat-card alert-card">
        <div className="stat-title">Critical Alerts</div>
        <div className="stat-value">{criticalAlerts}</div>
        <div className="stat-description">Requires attention</div>
      </div>
    </div>
  );
};

export default StatsCards;
