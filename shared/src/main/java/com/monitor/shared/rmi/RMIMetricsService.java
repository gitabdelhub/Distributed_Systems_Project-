package com.monitor.shared.rmi;

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

    /**
     * Récupère les dernières métriques de tous les agents connectés.
     * @return Une map où la clé est l'ID de l'agent et la valeur ses métriques
     * @throws RemoteException si la communication RMI échoue
     */
    Map<String, MetricData> getLatestMetrics() throws RemoteException;

    /**
     * Récupère la liste des IDs des agents actuellement enregistrés.
     * @return Une liste d'IDs d'agents (chaînes de caractères)
     * @throws RemoteException si la communication RMI échoue
     */
    List<String> getAgentList() throws RemoteException;

    /**
     * Récupère les métriques d'un agent spécifique.
     * @param agentId L'identifiant de l'agent
     * @return Les métriques de l'agent, ou null s'il n'existe pas
     * @throws RemoteException si la communication RMI échoue
     */
    MetricData getMetricsByAgent(String agentId) throws RemoteException;

    /**
     * Enregistre un nouvel agent auprès du serveur.
     * @param agentId L'identifiant unique de l'agent
     * @return true si l'enregistrement a réussi, false sinon
     * @throws RemoteException si la communication RMI échoue
     */
    boolean registerAgent(String agentId) throws RemoteException;

    /**
     * Déconnecte un agent du serveur.
     * @param agentId L'identifiant de l'agent à déconnecter
     * @return true si la déconnexion a réussi, false sinon
     * @throws RemoteException si la communication RMI échoue
     */
    boolean unregisterAgent(String agentId) throws RemoteException;
}