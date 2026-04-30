package com.monitor.ui.desktop.rmi;

import com.monitor.shared.rmi.RMIMetricsService;
import com.monitor.shared.constants.Constants;
import com.monitor.shared.model.MetricData;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

public class RMIServiceProxy {
    private RMIMetricsService service;

    public RMIServiceProxy() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Constants.RMI_PORT);
            service = (RMIMetricsService) registry.lookup(Constants.RMI_SERVICE_NAME);
        } catch (Exception e) {
            System.err.println("Connexion RMI échouée : " + e.getMessage());
        }
    }

    public Map<String, MetricData> getLatestMetrics() throws Exception { return service.getLatestMetrics(); }
    public List<String> getAgentList() throws Exception { return service.getAgentList(); }
}
