import axios from 'axios';

const BASE = '/api';

export const fetchLatestMetrics = () =>
  axios.get(`${BASE}/metrics/latest`).then(r => r.data);

export const fetchAgents = () =>
  axios.get(`${BASE}/agents`).then(r => r.data);

export const fetchAlerts = () =>
  axios.get(`${BASE}/alerts`).then(r => r.data);

export const fetchHistory = (agentId) =>
  axios.get(`${BASE}/metrics/${encodeURIComponent(agentId)}/history`).then(r => r.data);

export const exportFile = (type, user, pass) =>
  axios.get(`/admin/export/${type}`, {
    auth: { username: user, password: pass },
    responseType: 'blob',
  }).then(r => {
    const ext = type === 'csv' ? 'csv' : 'json';
    const url = URL.createObjectURL(r.data);
    const a = document.createElement('a');
    a.href = url;
    a.download = `metrics.${ext}`;
    a.click();
    URL.revokeObjectURL(url);
  });
