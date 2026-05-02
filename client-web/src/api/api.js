import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

export const fetchLatestMetrics = async () => {
  const response = await axios.get(`${API_BASE}/metrics/latest`);
  return response.data;
};

export const fetchAlerts = async () => {
  const response = await axios.get(`${API_BASE}/alerts`);
  return response.data;
};

export const fetchAgents = async () => {
  const response = await axios.get(`${API_BASE}/agents`);
  return response.data;
};

export const exportFile = async (format, username, password) => {
  const response = await axios.post(`${API_BASE}/export`, {
    format,
    username,
    password
  }, {
    responseType: 'blob'
  });
  
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `metrics.${format}`);
  document.body.appendChild(link);
  link.click();
  link.remove();
};

export const getThresholds = async () => {
  const response = await axios.get(`${API_BASE}/thresholds`);
  return response.data;
};

export const updateThresholds = async (thresholds) => {
  const response = await axios.put(`${API_BASE}/thresholds`, thresholds);
  return response.data;
};
