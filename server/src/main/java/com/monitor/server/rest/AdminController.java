package com.monitor.server.rest;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.server.storage.MetricsExporter;
import com.monitor.shared.model.MetricData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ConcurrentDataStore store = ConcurrentDataStore.getInstance();
    private final MetricsExporter exporter = new MetricsExporter();

    @GetMapping("/agents")
    public Set<String> getAgents() {
        return store.getAgentIds();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCsv() {
        List<MetricData> all = new ArrayList<>();
        store.getAgentIds().forEach(id -> all.addAll(store.getHistory(id)));
        return ResponseEntity.ok()
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=metrics.csv")
            .body(exporter.toCSV(all));
    }

    @GetMapping("/export/json")
    public ResponseEntity<String> exportJson() {
        List<MetricData> all = new ArrayList<>();
        store.getAgentIds().forEach(id -> all.addAll(store.getHistory(id)));
        return ResponseEntity.ok()
            .header("Content-Type", "application/json")
            .body(exporter.toJSON(all));
    }
}
