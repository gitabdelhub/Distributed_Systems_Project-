package com.monitor.server.rest;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MetricsController {
    private final ConcurrentDataStore store = ConcurrentDataStore.getInstance();

    @GetMapping("/metrics/latest")
    public Map<String, MetricData> getLatest() { return store.getLatest(); }

    @GetMapping("/metrics/{agentId}/history")
    public List<MetricData> getHistory(@PathVariable String agentId) { return store.getHistory(agentId); }

    @GetMapping("/agents")
    public List<String> getAgents() { return new ArrayList<>(store.getAgentIds()); }

    @GetMapping("/alerts")
    public List<Alert> getAlerts() { return store.getAlerts(); }
}
