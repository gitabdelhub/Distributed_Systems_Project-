package com.monitor.server.rest;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.server.storage.MetricsExporter;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * API REST du serveur — consommée par le client web (React).
 * Tous les endpoints sont publics (voir SecurityConfig).
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MetricsController {

    private final ConcurrentDataStore store = ConcurrentDataStore.getInstance();

    @Autowired
    private MetricsExporter exporter;

    // ── Métriques ──────────────────────────────────────────────────────────────

    @GetMapping("/metrics/latest")
    public Map<String, MetricData> getLatest() {
        return store.getLatest();
    }

    @GetMapping("/metrics/{agentId}/history")
    public List<MetricData> getHistory(@PathVariable String agentId) {
        return store.getHistory(agentId);
    }

    // ── Agents ─────────────────────────────────────────────────────────────────

    @GetMapping("/agents")
    public List<String> getAgents() {
        return new ArrayList<>(store.getAgentIds());
    }

    // ── Alertes ────────────────────────────────────────────────────────────────

    @GetMapping("/alerts")
    public List<Alert> getAlerts() {
        return store.getAlerts();
    }

    // ── Export ─────────────────────────────────────────────────────────────────

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCSV() {
        List<MetricData> all = new ArrayList<>();
        store.getAgentIds().forEach(id -> all.addAll(store.getHistory(id)));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=metrics.csv")
            .contentType(MediaType.TEXT_PLAIN)
            .body(exporter.toCSV(all));
    }

    @GetMapping("/export/json")
    public ResponseEntity<String> exportJSON() {
        List<MetricData> all = new ArrayList<>();
        store.getAgentIds().forEach(id -> all.addAll(store.getHistory(id)));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=metrics.json")
            .contentType(MediaType.APPLICATION_JSON)
            .body(exporter.toJSON(all));
    }
}
