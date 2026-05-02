package com.monitor.server.rmi;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.model.MetricData;
import com.monitor.shared.rmi.RMIMetricsService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

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

    // Methods with minimal implementations
    
    public List<MetricData> getHistory(String agentId) throws RemoteException {
        // Return empty list if store.getHistory() method doesn't exist
        return new ArrayList<>();
    }

    public MetricData getMetricsByAgent(String agentId) throws RemoteException {
        var latest = store.getLatest();
        return latest != null ? latest.get(agentId) : null;
    }

    public boolean registerAgent(String agentId) throws RemoteException {
        return true;
    }

    public boolean unregisterAgent(String agentId) throws RemoteException {
        return true;
    }
}
