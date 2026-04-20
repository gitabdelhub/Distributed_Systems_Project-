package com.monitor.shared.model;
import java.io.Serializable;

public record MetricData(
    String agentId,
    double cpuUsage,
    double ramUsage,
    double diskUsage,
    long timestamp
) implements Serializable {}