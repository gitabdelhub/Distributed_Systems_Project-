package com.monitor.shared.constants;

/**
 * Constantes unifiées du système (ports, seuils, intervalles).
 * Toujours alignées avec NetworkConstants pour éviter les conflits de ports.
 */
public class Constants {
    public static final int UDP_PORT  = 5000;   // Port UDP métriques (agent → serveur)
    public static final int TCP_PORT  = 6000;   // Port TCP alertes   (agent → serveur)
    public static final int RMI_PORT  = 1099;   // Port RMI           (serveur → client desktop)
    public static final String RMI_SERVICE_NAME = "MetricsService";
    public static final int AGENT_SEND_INTERVAL_MS = 5000;  // 5 secondes entre chaque envoi
    public static final double CPU_ALERT_THRESHOLD  = 85.0; // Seuil critique CPU (%)
    public static final double RAM_ALERT_THRESHOLD  = 90.0; // Seuil critique RAM (%)
    public static final double DISK_ALERT_THRESHOLD = 95.0; // Seuil critique Disque (%)
}
