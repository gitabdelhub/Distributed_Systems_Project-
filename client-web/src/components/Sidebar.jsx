import React from 'react';
import './Sidebar.css';

const Sidebar = ({ agents, selectedAgent, onSelectAgent }) => {
  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h2>Agents</h2>
      </div>
      
      <div className="agent-list">
        <div 
          className={`agent-item ${!selectedAgent ? 'active' : ''}`}
          onClick={() => onSelectAgent(null)}
        >
          <div className="agent-info">
            <div className="agent-name">All Agents</div>
            <div className="agent-status">Overview</div>
          </div>
        </div>
        
        {agents.map(agent => (
          <div 
            key={agent.id}
            className={`agent-item ${selectedAgent === agent.id ? 'active' : ''}`}
            onClick={() => onSelectAgent(agent.id)}
          >
            <div className="agent-info">
              <div className="agent-name">{agent.name}</div>
              <div className="agent-status">
                <span className={`status-indicator ${agent.status}`}></span>
                {agent.status}
              </div>
            </div>
            <div className="agent-metrics">
              <div className="metric">CPU: {agent.cpu}%</div>
              <div className="metric">RAM: {agent.ram}%</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Sidebar;
