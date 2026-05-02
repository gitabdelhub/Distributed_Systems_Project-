import React from 'react';
import './AlertsPanel.css';

const AlertsPanel = ({ alerts }) => {
  const getAlertSeverity = (severity) => {
    switch (severity) {
      case 'CRITICAL':
        return 'severity-critical';
      case 'WARNING':
        return 'severity-warning';
      case 'INFO':
        return 'severity-info';
      default:
        return 'severity-info';
    }
  };

  const formatTimestamp = (timestamp) => {
    return new Date(timestamp).toLocaleString();
  };

  return (
    <div className="alerts-panel">
      {alerts.length === 0 ? (
        <div className="no-alerts">
          <p>No recent alerts</p>
        </div>
      ) : (
        <div className="alerts-list">
          {alerts.map((alert, index) => (
            <div key={index} className={`alert-item ${getAlertSeverity(alert.severity)}`}>
              <div className="alert-header">
                <span className="alert-severity">{alert.severity}</span>
                <span className="alert-timestamp">{formatTimestamp(alert.timestamp)}</span>
              </div>
              <div className="alert-content">
                <div className="alert-agent">Agent: {alert.agentId}</div>
                <div className="alert-message">{alert.message}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default AlertsPanel;
