package com.monitor.server.rmi;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import com.monitor.shared.rmi.RMIMetricsService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Implémentation du service RMI — exposée aux clients desktop.
 * Toutes les méthodes délèguent au ConcurrentDataStore singleton.
 */
public class RMIMetricsServiceImpl extends UnicastRemoteObject implements RMIMetricsService {
    private final ConcurrentDataStore store;

    public RMIMetricsServiceImpl(ConcurrentDataStore store) throws RemoteException {
        super();
        this.store = store;
    }

    @Override
    public Map<String, MetricData> getLatestMetrics() throws RemoteException {
        return store.getLatest();
    }

    @Override
    public List<String> getAgentList() throws RemoteException {
        return new ArrayList<>(store.getAgentIds());
    }

    @Override
    public MetricData getMetricsByAgent(String agentId) throws RemoteException {
        return store.getLatest().get(agentId);
    }

    @Override
    public List<MetricData> getHistory(String agentId) throws RemoteException {
        return new ArrayList<>(store.getHistory(agentId));
    }

    @Override
    public List<Alert> getAlerts() throws RemoteException {
        return store.getAlerts();
    }

    @Override
    public boolean registerAgent(String agentId) throws RemoteException {
        return true; // enregistrement implicite lors de la 1ère métrique reçue
    }

    @Override
    public boolean unregisterAgent(String agentId) throws RemoteException {
        return true;
    }
}