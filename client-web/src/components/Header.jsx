import React from 'react';
import './Header.css';

const Header = ({ agentCount, lastUpdate }) => {
  return (
    <div className="header">
      <div className="header-content">
        <div className="header-info">
          <h1>Système de Surveillance</h1>
          <div className="status-info">
            <span className="agent-count">{agentCount} agents connectés</span>
            <span className="last-update">Dernière mise à jour: {lastUpdate}</span>
          </div>
        </div>
        <div className="header-actions">
          <button className="export-btn">Export CSV</button>
          <button className="export-btn">Export JSON</button>
        </div>
      </div>
    </div>
  );
};

export default Header;
