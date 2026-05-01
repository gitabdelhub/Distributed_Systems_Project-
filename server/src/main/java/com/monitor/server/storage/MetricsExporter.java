package com.monitor.server.storage;

import com.monitor.shared.model.MetricData;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Utilitaire d'export des métriques en CSV ou JSON.
 * Injecté dans MetricsController pour exposer les endpoints d'export.
 */
@Component
public class MetricsExporter {

    public String toCSV(List<MetricData> data) {
        StringBuilder sb = new StringBuilder("agentId,cpuUsage,ramUsage,diskUsage,timestamp\n");
        for (MetricData d : data)
            sb.append(String.format("%s,%.2f,%.2f,%.2f,%d%n",
                d.agentId(), d.cpuUsage(), d.ramUsage(), d.diskUsage(), d.timestamp()));
        return sb.toString();
    }

    public String toJSON(List<MetricData> data) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            MetricData d = data.get(i);
            sb.append(String.format(
                "{\"agentId\":\"%s\",\"cpuUsage\":%.2f,\"ramUsage\":%.2f,\"diskUsage\":%.2f,\"timestamp\":%d}",
                d.agentId(), d.cpuUsage(), d.ramUsage(), d.diskUsage(), d.timestamp()
            ));
            if (i < data.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
