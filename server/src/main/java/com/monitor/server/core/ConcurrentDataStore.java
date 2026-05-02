package com.monitor.server.core;

import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentDataStore {
    private static final ConcurrentDataStore INSTANCE = new ConcurrentDataStore();
    private final Map<String, List<MetricData>> metrics = new ConcurrentHashMap<>();
    private final List<Alert> alerts = Collections.synchronizedList(new ArrayList<>());

    private ConcurrentDataStore() {}

    public static ConcurrentDataStore getInstance() { return INSTANCE; }

    public void save(MetricData data) {
        metrics.computeIfAbsent(data.agentId(), k -> Collections.synchronizedList(new ArrayList<>())).add(data);
    }

    public List<MetricData> getHistory(String agentId) {
        return metrics.getOrDefault(agentId, List.of());
    }

    public Map<String, MetricData> getLatest() {
        Map<String, MetricData> latest = new ConcurrentHashMap<>();
        metrics.forEach((id, list) -> {
            synchronized (list) {
                if (!list.isEmpty()) latest.put(id, list.get(list.size() - 1));
            }
        });
        return latest;
    }

    public void addAlert(Alert alert) { alerts.add(alert); }
    public List<Alert> getAlerts() { return List.copyOf(alerts); }
    public Set<String> getAgentIds() { return metrics.keySet(); }
}
