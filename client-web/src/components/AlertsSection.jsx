import React from 'react';
import './AlertsSection.css';

const AlertsSection = ({ alerts }) => {
  return (
    <div className="alerts-section">
      <h2>Alertes ({alerts.length})</h2>
      <div className="alerts-list">
        {alerts.length === 0 ? (
          <div className="no-alerts">
            <p>Aucune alerte active</p>
          </div>
        ) : (
          alerts.map((alert, index) => (
            <div key={index} className={`alert-item ${alert.severity.toLowerCase()}`}>
              <div className="alert-header">
                <span className="alert-time">{alert.timestamp}</span>
                <span className="alert-agent">Agent: {alert.agentId}</span>
              </div>
              <div className="alert-message">{alert.message}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default AlertsSection;
