import React, { useState, useMemo } from 'react';
import { CPU_CRITICAL, RAM_CRITICAL, DISK_CRITICAL } from '../constants';

function badgeColor(value, critical) {
  const warning = critical * 0.8;
  if (value >= critical) return 'bg-red-500 text-white';
  if (value >= warning)  return 'bg-yellow-400 text-black';
  return 'bg-green-500 text-white';
}

function MetricBadge({ value, critical }) {
  const label = typeof value === 'number' ? value.toFixed(1) + '%' : '—';
  return (
    <span className={`px-2 py-0.5 rounded text-xs font-semibold ${badgeColor(value, critical)}`}>
      {label}
    </span>
  );
}

export default function MetricsTable({ metrics }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');
  const [sortBy, setSortBy] = useState('agentId');
  const [sortOrder, setSortOrder] = useState('asc');

  const rows = useMemo(() => {
    let filtered = Object.values(metrics);

    // Apply search filter
    if (searchTerm) {
      filtered = filtered.filter(m => 
        m.agentId.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Apply status filter
    if (filterStatus !== 'all') {
      filtered = filtered.filter(m => {
        const cpuStatus = m.cpuUsage >= CPU_CRITICAL ? 'critical' : m.cpuUsage >= CPU_CRITICAL * 0.8 ? 'warning' : 'normal';
        const ramStatus = m.ramUsage >= RAM_CRITICAL ? 'critical' : m.ramUsage >= RAM_CRITICAL * 0.8 ? 'warning' : 'normal';
        const diskStatus = m.diskUsage >= DISK_CRITICAL ? 'critical' : m.diskUsage >= DISK_CRITICAL * 0.8 ? 'warning' : 'normal';
        
        if (filterStatus === 'critical') {
          return cpuStatus === 'critical' || ramStatus === 'critical' || diskStatus === 'critical';
        } else if (filterStatus === 'warning') {
          return cpuStatus === 'warning' || ramStatus === 'warning' || diskStatus === 'warning';
        } else if (filterStatus === 'normal') {
          return cpuStatus === 'normal' && ramStatus === 'normal' && diskStatus === 'normal';
        }
        return true;
      });
    }

    // Apply sorting
    filtered.sort((a, b) => {
      let aValue, bValue;
      
      switch (sortBy) {
        case 'agentId':
          aValue = a.agentId;
          bValue = b.agentId;
          break;
        case 'cpu':
          aValue = a.cpuUsage;
          bValue = b.cpuUsage;
          break;
        case 'ram':
          aValue = a.ramUsage;
          bValue = b.ramUsage;
          break;
        case 'disk':
          aValue = a.diskUsage;
          bValue = b.diskUsage;
          break;
        case 'timestamp':
          aValue = new Date(a.timestamp);
          bValue = new Date(b.timestamp);
          break;
        default:
          aValue = a.agentId;
          bValue = b.agentId;
      }

      if (aValue < bValue) return sortOrder === 'asc' ? -1 : 1;
      if (aValue > bValue) return sortOrder === 'asc' ? 1 : -1;
      return 0;
    });

    return filtered;
  }, [metrics, searchTerm, filterStatus, sortBy, sortOrder]);

  const handleSort = (column) => {
    if (sortBy === column) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(column);
      setSortOrder('asc');
    }
  };

  if (rows.length === 0 && Object.keys(metrics).length === 0) {
    return <p className="text-gray-500 text-sm mt-4">Aucun agent connecté.</p>;
  }

  return (
    <div className="mt-4">
      {/* Filters and Search */}
      <div className="bg-gray-50 p-4 rounded-lg border mb-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Search */}
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Recherche par Agent ID</label>
            <input
              type="text"
              placeholder="Rechercher un agent..."
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          
          {/* Status Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Filtrer par statut</label>
            <select
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
            >
              <option value="all">Tous</option>
              <option value="normal">Normal</option>
              <option value="warning">Avertissement</option>
              <option value="critical">Critique</option>
            </select>
          </div>

          {/* Sort */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Trier par</label>
            <select
              className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="agentId">Agent ID</option>
              <option value="cpu">CPU</option>
              <option value="ram">RAM</option>
              <option value="disk">Disque</option>
              <option value="timestamp">Dernière mise à jour</option>
            </select>
          </div>
        </div>

        {/* Results count */}
        <div className="mt-3 text-sm text-gray-600">
          {rows.length} agent{rows.length > 1 ? 's' : ''} trouvé{rows.length > 1 ? 's' : ''}
          {searchTerm && ` pour "${searchTerm}"`}
          {filterStatus !== 'all' && ` avec statut "${filterStatus}"`}
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm border rounded-lg overflow-hidden">
          <thead className="bg-gray-100 text-left text-gray-700 uppercase text-xs">
            <tr>
              <th 
                className="px-4 py-3 cursor-pointer hover:bg-gray-200"
                onClick={() => handleSort('agentId')}
              >
                Agent ID {sortBy === 'agentId' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th 
                className="px-4 py-3 cursor-pointer hover:bg-gray-200"
                onClick={() => handleSort('cpu')}
              >
                CPU {sortBy === 'cpu' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th 
                className="px-4 py-3 cursor-pointer hover:bg-gray-200"
                onClick={() => handleSort('ram')}
              >
                RAM {sortBy === 'ram' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th 
                className="px-4 py-3 cursor-pointer hover:bg-gray-200"
                onClick={() => handleSort('disk')}
              >
                Disque {sortBy === 'disk' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
              <th 
                className="px-4 py-3 cursor-pointer hover:bg-gray-200"
                onClick={() => handleSort('timestamp')}
              >
                Dernière mise à jour {sortBy === 'timestamp' && (sortOrder === 'asc' ? '↑' : '↓')}
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {rows.length === 0 ? (
              <tr>
                <td colSpan="5" className="px-4 py-8 text-center text-gray-500">
                  Aucun agent trouvé pour les filtres sélectionnés
                </td>
              </tr>
            ) : (
              rows.map(m => (
                <tr key={m.agentId} className="hover:bg-gray-50">
                  <td className="px-4 py-2 font-mono text-xs text-gray-600">
                    {m.agentId.substring(0, 8)}…
                  </td>
                  <td className="px-4 py-2">
                    <MetricBadge value={m.cpuUsage} critical={CPU_CRITICAL} />
                  </td>
                  <td className="px-4 py-2">
                    <MetricBadge value={m.ramUsage} critical={RAM_CRITICAL} />
                  </td>
                  <td className="px-4 py-2">
                    <MetricBadge value={m.diskUsage} critical={DISK_CRITICAL} />
                  </td>
                  <td className="px-4 py-2 text-gray-500">
                    {new Date(m.timestamp).toLocaleTimeString()}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
