import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Header from './components/Header';
import NavigationTabs from './components/NavigationTabs';
import MetricsTable from './components/MetricsTable';
import AlertsSection from './components/AlertsSection';
import Footer from './components/Footer';
import './App.css';

function App() {
  const [metrics, setMetrics] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [activeTab, setActiveTab] = useState('overview');
  const [lastUpdate, setLastUpdate] = useState(new Date().toLocaleTimeString('fr-FR'));

  useEffect(() => {
    // Fetch initial data
    fetchData();
    
    // Set up polling for real-time updates
    const interval = setInterval(() => {
      fetchData();
      setLastUpdate(new Date().toLocaleTimeString('fr-FR'));
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const fetchData = async () => {
    try {
      // Simulate data for now - replace with actual API calls
      const mockMetrics = [
        {
          agentId: '4ccdcd67-1234-5678-9abc-123456789def',
          cpu: 10.2,
          ram: 86.0,
          disk: 87.7,
          timestamp: new Date()
        },
        {
          agentId: 'a1b2c3d4-5678-90ab-cdef-1234567890ab',
          cpu: 45.5,
          ram: 62.3,
          disk: 45.8,
          timestamp: new Date()
        }
      ];
      
      const mockAlerts = [
        {
          severity: 'warning',
          agentId: '4ccdcd67-1234',
          message: 'Utilisation RAM élevée sur l\'agent',
          timestamp: '16:49:23'
        },
        {
          severity: 'critical',
          agentId: 'a1b2c3d4-5678',
          message: 'Espace disque critique',
          timestamp: '16:48:15'
        }
      ];
      
      setMetrics(mockMetrics);
      setAlerts(mockAlerts);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const agentCount = new Set(metrics.map(m => m.agentId)).size;

  return (
    <div className="app">
      <Header agentCount={agentCount} lastUpdate={lastUpdate} />
      <NavigationTabs 
        activeTab={activeTab} 
        onTabChange={setActiveTab} 
        alertCount={alerts.length} 
      />
      <main className="main-content">
        {activeTab === 'overview' && (
          <MetricsTable metrics={metrics} />
        )}
        {activeTab === 'history' && (
          <div className="history-section">
            <h2>Historique</h2>
            <p>Fonctionnalité d'historique en cours de développement...</p>
          </div>
        )}
        {activeTab === 'alerts' && (
          <AlertsSection alerts={alerts} />
        )}
      </main>
      <Footer />
    </div>
  );
}

export default App;
