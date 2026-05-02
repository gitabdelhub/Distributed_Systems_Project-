import React, { useState } from 'react';

export default function ThresholdConfig({ thresholds, onUpdate }) {
  const [localThresholds, setLocalThresholds] = useState(thresholds);

  const handleChange = (key, value) => {
    setLocalThresholds(prev => ({
      ...prev,
      [key]: parseFloat(value)
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onUpdate(localThresholds);
  };

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <h2 className="text-lg font-medium text-gray-900 mb-4">Threshold Configuration</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">CPU Warning (%)</label>
            <input
              type="number"
              value={localThresholds.cpuWarning || 70}
              onChange={(e) => handleChange('cpuWarning', e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="0"
              max="100"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">CPU Critical (%)</label>
            <input
              type="number"
              value={localThresholds.cpuCritical || 90}
              onChange={(e) => handleChange('cpuCritical', e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="0"
              max="100"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">RAM Warning (%)</label>
            <input
              type="number"
              value={localThresholds.ramWarning || 75}
              onChange={(e) => handleChange('ramWarning', e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="0"
              max="100"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">RAM Critical (%)</label>
            <input
              type="number"
              value={localThresholds.ramCritical || 85}
              onChange={(e) => handleChange('ramCritical', e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="0"
              max="100"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Disk Warning (%)</label>
            <input
              type="number"
              value={localThresholds.diskWarning || 80}
              onChange={(e) => handleChange('diskWarning', e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="0"
              max="100"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Disk Critical (%)</label>
            <input
              type="number"
              value={localThresholds.diskCritical || 95}
              onChange={(e) => handleChange('diskCritical', e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              min="0"
              max="100"
            />
          </div>
        </div>
        <div className="flex justify-end">
          <button
            type="submit"
            className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Update Thresholds
          </button>
        </div>
      </form>
    </div>
  );
}
