import React, { useState, useEffect } from 'react';

const ThresholdConfig = ({ onThresholdsChange }) => {
  const [thresholds, setThresholds] = useState({
    cpu: { warning: 70, critical: 85 },
    ram: { warning: 75, critical: 90 },
    disk: { warning: 80, critical: 95 }
  });

  const [isEditing, setIsEditing] = useState(false);
  const [tempThresholds, setTempThresholds] = useState({ ...thresholds });

  useEffect(() => {
    // Load thresholds from localStorage or API
    const saved = localStorage.getItem('alertThresholds');
    if (saved) {
      const parsed = JSON.parse(saved);
      setThresholds(parsed);
      setTempThresholds(parsed);
      onThresholdsChange?.(parsed);
    }
  }, [onThresholdsChange]);

  const handleEdit = () => {
    setTempThresholds({ ...thresholds });
    setIsEditing(true);
  };

  const handleCancel = () => {
    setTempThresholds({ ...thresholds });
    setIsEditing(false);
  };

  const handleSave = () => {
    // Validate thresholds
    const isValid = Object.entries(tempThresholds).every(([metric, values]) => 
      values.warning >= 0 && values.warning < 100 &&
      values.critical >= 0 && values.critical <= 100 &&
      values.warning < values.critical
    );

    if (!isValid) {
      alert('Valeurs invalides : Warning doit être < Critical et entre 0-100');
      return;
    }

    setThresholds({ ...tempThresholds });
    setIsEditing(false);
    
    // Save to localStorage (in production, save to API)
    localStorage.setItem('alertThresholds', JSON.stringify(tempThresholds));
    onThresholdsChange?.(tempThresholds);
  };

  const handleChange = (metric, type, value) => {
    const numValue = Math.max(0, Math.min(100, parseInt(value) || 0));
    setTempThresholds(prev => ({
      ...prev,
      [metric]: {
        ...prev[metric],
        [type]: numValue
      }
    }));
  };

  const resetDefaults = () => {
    const defaults = {
      cpu: { warning: 70, critical: 85 },
      ram: { warning: 75, critical: 90 },
      disk: { warning: 80, critical: 95 }
    };
    setTempThresholds(defaults);
  };

  return (
    <div className="bg-white border rounded-lg p-6 shadow-sm">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold text-gray-800">🔧 Configuration des Seuils d'Alertes</h3>
        {!isEditing ? (
          <button
            onClick={handleEdit}
            className="px-4 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 transition"
          >
            Modifier
          </button>
        ) : (
          <div className="flex gap-2">
            <button
              onClick={resetDefaults}
              className="px-3 py-2 bg-gray-500 text-white text-sm rounded-md hover:bg-gray-600 transition"
            >
              Défaut
            </button>
            <button
              onClick={handleCancel}
              className="px-3 py-2 bg-gray-300 text-gray-700 text-sm rounded-md hover:bg-gray-400 transition"
            >
              Annuler
            </button>
            <button
              onClick={handleSave}
              className="px-3 py-2 bg-green-600 text-white text-sm rounded-md hover:bg-green-700 transition"
            >
              Sauvegarder
            </button>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {Object.entries(thresholds).map(([metric, values]) => (
          <div key={metric} className="border rounded-lg p-4 bg-gray-50">
            <h4 className="font-medium text-gray-700 mb-3 capitalize">
              {metric === 'cpu' ? 'CPU' : metric === 'ram' ? 'RAM' : 'Disque'}
            </h4>
            
            {isEditing ? (
              <div className="space-y-3">
                <div>
                  <label className="block text-sm text-gray-600 mb-1">Avertissement (%)</label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={tempThresholds[metric].warning}
                    onChange={(e) => handleChange(metric, 'warning', e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm text-gray-600 mb-1">Critique (%)</label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={tempThresholds[metric].critical}
                    onChange={(e) => handleChange(metric, 'critical', e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>
            ) : (
              <div className="space-y-2">
                <div className="flex justify-between items-center">
                  <span className="text-sm text-gray-600">Avertissement:</span>
                  <span className="px-2 py-1 bg-yellow-100 text-yellow-800 rounded text-sm font-medium">
                    {values.warning}%
                  </span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-gray-600">Critique:</span>
                  <span className="px-2 py-1 bg-red-100 text-red-800 rounded text-sm font-medium">
                    {values.critical}%
                  </span>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      {!isEditing && (
        <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-md">
          <p className="text-sm text-blue-700">
            💡 Les alertes seront déclenchées lorsque l'utilisation dépasse les seuils configurés ci-dessus.
            Les seuils d'avertissement sont utilisés pour les notifications jaunes, 
            les seuils critiques pour les notifications rouges.
          </p>
        </div>
      )}
    </div>
  );
};

export default ThresholdConfig;
