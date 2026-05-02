// Alert thresholds (default values)
export const CPU_CRITICAL = 85;
export const RAM_CRITICAL = 90;
export const DISK_CRITICAL = 95;

export const CPU_WARNING = 70;
export const RAM_WARNING = 75;
export const DISK_WARNING = 80;

// API endpoints
export const API_BASE_URL = 'http://localhost:8080/api';

export const ENDPOINTS = {
  METRICS: `${API_BASE_URL}/metrics`,
  ALERTS: `${API_BASE_URL}/alerts`,
  AGENTS: `${API_BASE_URL}/agents`,
  EXPORT_CSV: `${API_BASE_URL}/export/csv`,
  EXPORT_JSON: `${API_BASE_URL}/export/json`,
  THRESHOLDS: `${API_BASE_URL}/thresholds`
};

// Refresh intervals
export const REFRESH_INTERVALS = {
  FAST: 2000,    // 2 seconds
  NORMAL: 5000,  // 5 seconds
  SLOW: 10000    // 10 seconds
};
