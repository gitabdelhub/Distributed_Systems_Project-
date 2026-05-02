import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const MetricsChart = ({ metrics, timeRange }) => {
  // Process metrics data for the chart
  const chartData = metrics.map(metric => ({
    timestamp: new Date(metric.timestamp).toLocaleTimeString(),
    cpu: metric.cpu,
    ram: metric.ram,
    disk: metric.disk
  }));

  return (
    <div className="metrics-chart">
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis 
            dataKey="timestamp" 
            angle={-45}
            textAnchor="end"
            height={80}
          />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line 
            type="monotone" 
            dataKey="cpu" 
            stroke="#8884d8" 
            name="CPU %"
            strokeWidth={2}
          />
          <Line 
            type="monotone" 
            dataKey="ram" 
            stroke="#82ca9d" 
            name="RAM %"
            strokeWidth={2}
          />
          <Line 
            type="monotone" 
            dataKey="disk" 
            stroke="#ffc658" 
            name="Disk %"
            strokeWidth={2}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default MetricsChart;
