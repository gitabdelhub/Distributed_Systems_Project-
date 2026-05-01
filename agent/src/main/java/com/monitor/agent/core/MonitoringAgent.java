package com.monitor.agent.core;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.monitor.agent.config.AgentConfig;
import com.monitor.agent.threads.MetricPublisherTask;
import com.monitor.shared.utils.Logger;

/**
 * Agent de surveillance — démarre la collecte périodique des métriques.
 * L'ID unique est généré au démarrage. L'intervalle est lu depuis agent.properties.
 */
public class MonitoringAgent {
    private final String agentId = UUID.randomUUID().toString();
    private final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(2);

    public void start() {
        int intervalMs = AgentConfig.getSendIntervalMs();
        Logger.info("MonitoringAgent", "Démarrage agent ID = " + agentId);
        Logger.info("MonitoringAgent", "Serveur : " + AgentConfig.getServerHost()
            + " | UDP:" + AgentConfig.getUdpPort()
            + " | TCP:" + AgentConfig.getTcpPort()
            + " | Intervalle: " + intervalMs + "ms");

        MetricPublisherTask task = new MetricPublisherTask(agentId);
        scheduler.scheduleAtFixedRate(task, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
        Logger.info("MonitoringAgent", "Agent arrêté.");
    }
}