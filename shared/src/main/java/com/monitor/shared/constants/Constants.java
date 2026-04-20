package com.monitor.shared.constants;

public class Constants {
    public static final int UDP_PORT = 9000;
    public static final int TCP_PORT = 9001;
    public static final int RMI_PORT = 1099;
    public static final String RMI_SERVICE_NAME = "MetricsService";
    public static final int AGENT_SEND_INTERVAL_MS = 5000;
    public static final double CPU_ALERT_THRESHOLD = 90.0;
    public static final double RAM_ALERT_THRESHOLD = 85.0;
    public static final double DISK_ALERT_THRESHOLD = 95.0;
}
