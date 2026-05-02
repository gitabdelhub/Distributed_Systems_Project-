package com.monitor.server.storage;

import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MetricsRepository {

    private final JdbcTemplate jdbc;

    public MetricsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void saveMetric(MetricData data) {
        jdbc.update(
            "INSERT INTO metric_data (agent_id, cpu_usage, ram_usage, disk_usage, timestamp) VALUES (?, ?, ?, ?, ?)",
            data.agentId(), data.cpuUsage(), data.ramUsage(), data.diskUsage(), data.timestamp()
        );
    }

    public void saveAlert(Alert alert) {
        jdbc.update(
            "INSERT INTO alerts (id, agent_id, type, severity, timestamp) VALUES (?, ?, ?, ?, ?)",
            alert.id(), alert.agentId(), alert.type(), alert.severity(), alert.timestamp()
        );
    }
}
