import React from 'react';
import './NavigationTabs.css';

const NavigationTabs = ({ activeTab, onTabChange, alertCount }) => {
  const tabs = [
    { id: 'overview', label: 'Vue générale' },
    { id: 'history', label: 'Historique' },
    { id: 'alerts', label: 'Alertes', hasBadge: true, count: alertCount }
  ];

  return (
    <div className="navigation-tabs">
      {tabs.map(tab => (
        <button
          key={tab.id}
          className={`tab ${activeTab === tab.id ? 'active' : ''}`}
          onClick={() => onTabChange(tab.id)}
        >
          {tab.label}
          {tab.hasBadge && (
            <span className={`alert-badge ${tab.count > 0 ? 'has-alerts' : ''}`}>
              {tab.count > 0 ? tab.count : ''}
            </span>
          )}
        </button>
      ))}
    </div>
  );
};

export default NavigationTabs;
