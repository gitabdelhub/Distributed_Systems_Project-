package com.monitor.shared.rmi;

import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface RMI partagée entre le serveur et les clients.
 * Définit le contrat de communication pour l'accès aux métriques système.
 */
public interface RMIMetricsService extends Remote {

    /** Récupère les dernières métriques de tous les agents connectés. */
    Map<String, MetricData> getLatestMetrics() throws RemoteException;

    /** Récupère la liste des IDs des agents actuellement enregistrés. */
    List<String> getAgentList() throws RemoteException;

    /** Récupère les métriques d'un agent spécifique (ou null). */
    MetricData getMetricsByAgent(String agentId) throws RemoteException;

    /** Récupère l'historique complet des métriques d'un agent. */
    List<MetricData> getHistory(String agentId) throws RemoteException;

    /** Récupère toutes les alertes enregistrées. */
    List<Alert> getAlerts() throws RemoteException;

    /** Enregistre un nouvel agent auprès du serveur. */
    boolean registerAgent(String agentId) throws RemoteException;

    /** Déconnecte un agent du serveur. */
    boolean unregisterAgent(String agentId) throws RemoteException;
}