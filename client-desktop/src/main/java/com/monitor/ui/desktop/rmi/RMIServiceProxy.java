package com.monitor.ui.desktop.rmi;

import com.monitor.shared.constants.NetworkConstants;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import com.monitor.shared.rmi.RMIMetricsService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Proxy RMI — façade locale vers le service RMI du serveur.
 * Gère la connexion et isole les clients desktop des détails RMI.
 */
public class RMIServiceProxy {
    private RMIMetricsService service;

    public RMIServiceProxy() {
        connect();
    }

    public void connect() {
        try {
            Registry registry = LocateRegistry.getRegistry(
                NetworkConstants.SERVER_HOST, NetworkConstants.PORT_RMI);
            service = (RMIMetricsService) registry.lookup(NetworkConstants.RMI_SERVICE_NAME);
            System.out.println("[RMI] Connecté au serveur " + NetworkConstants.SERVER_HOST);
        } catch (Exception e) {
            System.err.println("[RMI] Connexion échouée : " + e.getMessage());
            service = null;
        }
    }

    public boolean isConnected() { return service != null; }

    public Map<String, MetricData> getLatestMetrics() throws Exception {
        return service.getLatestMetrics();
    }

    public List<String> getAgentList() throws Exception {
        return service.getAgentList();
    }

    public List<MetricData> getHistory(String agentId) throws Exception {
        return service.getHistory(agentId);
    }

    public List<Alert> getAlerts() throws Exception {
        return service.getAlerts();
    }
}
