package com.monitor.shared.constants;

/**
 * General application constants.
 * Port constants are defined in {@link NetworkConstants}.
 * Threshold constants are defined in {@link ThresholdConstants}.
 */
public class Constants {
    public static final int UDP_PORT = NetworkConstants.PORT_UDP;
    public static final int TCP_PORT = NetworkConstants.PORT_TCP;
    public static final int RMI_PORT = NetworkConstants.PORT_RMI;
    public static final String RMI_SERVICE_NAME = NetworkConstants.RMI_SERVICE_NAME;
    public static final int AGENT_SEND_INTERVAL_MS = ThresholdConstants.AGENT_SEND_INTERVAL_MS;
    public static final double CPU_ALERT_THRESHOLD = ThresholdConstants.CPU_CRITICAL;
    public static final double RAM_ALERT_THRESHOLD = ThresholdConstants.RAM_CRITICAL;
    public static final double DISK_ALERT_THRESHOLD = ThresholdConstants.DISK_CRITICAL;
}
