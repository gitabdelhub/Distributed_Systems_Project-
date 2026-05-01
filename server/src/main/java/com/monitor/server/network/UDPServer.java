package com.monitor.server.network;

import com.monitor.server.alerting.AlertDispatcher;
import com.monitor.server.alerting.ThresholdEngine;
import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.constants.NetworkConstants;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import com.monitor.shared.utils.SerializationUtils;
import java.net.*;

/**
 * Listener UDP — reçoit les métriques envoyées par les agents (sérialisation Java).
 * Évalue les seuils côté serveur et stocke les alertes si nécessaire.
 */
public class UDPServer implements Runnable {
    private final ConcurrentDataStore store;
    private final ThresholdEngine thresholdEngine = new ThresholdEngine();
    private final AlertDispatcher dispatcher = new AlertDispatcher();

    public UDPServer(ConcurrentDataStore store) { this.store = store; }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(NetworkConstants.PORT_UDP)) {
            byte[] buffer = new byte[8192];
            System.out.println("[UDP] En écoute sur port " + NetworkConstants.PORT_UDP);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());

                MetricData metric = (MetricData) SerializationUtils.deserialize(data);
                store.save(metric);
                System.out.printf("[UDP] Reçu de %s | CPU=%.1f%% RAM=%.1f%% Disk=%.1f%%%n",
                    metric.agentId(), metric.cpuUsage(), metric.ramUsage(), metric.diskUsage());

                // Évaluation des seuils côté serveur
                Alert alert = thresholdEngine.check(metric);
                if (alert != null) {
                    store.addAlert(alert);
                    dispatcher.dispatch(alert);
                }
            }
        } catch (Exception e) {
            System.err.println("[UDP] Erreur : " + e.getMessage());
        }
    }
}
