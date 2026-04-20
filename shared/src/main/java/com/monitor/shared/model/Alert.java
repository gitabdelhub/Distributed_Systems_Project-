package com.monitor.shared.model;
import java.io.Serializable;

public record Alert(
    String id,
    String agentId,
    String type,      // "CPU", "RAM", "DISK"
    String severity,  // "CRITICAL", "WARNING"
    long timestamp
) implements Serializable {}