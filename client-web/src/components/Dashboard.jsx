import React, { useState } from 'react';
import MetricsChart from './MetricsChart';
import AlertsPanel from './AlertsPanel';
import StatsCards from './StatsCards';
import './Dashboard.css';

const Dashboard = ({ metrics, alerts, selectedAgent }) => {
  const [timeRange, setTimeRange] = useState('1h');

  const filteredMetrics = selectedAgent 
    ? metrics.filter(metric => metric.agentId === selectedAgent)
    : metrics;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Monitoring Dashboard</h1>
        <div className="controls">
          <select 
            value={timeRange} 
            onChange={(e) => setTimeRange(e.target.value)}
            className="time-range-select"
          >
            <option value="1h">Last Hour</option>
            <option value="24h">Last 24 Hours</option>
            <option value="7d">Last 7 Days</option>
          </select>
        </div>
      </div>

      <StatsCards metrics={filteredMetrics} alerts={alerts} />
      
      <div className="dashboard-content">
        <div className="metrics-section">
          <h2>System Metrics</h2>
          <MetricsChart metrics={filteredMetrics} timeRange={timeRange} />
        </div>
        
        <div className="alerts-section">
          <h2>Recent Alerts</h2>
          <AlertsPanel alerts={alerts} />
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
