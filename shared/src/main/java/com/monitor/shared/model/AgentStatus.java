package com.monitor.shared.model;
import java.io.Serializable;
import java.util.Date;

public record AgentStatus(
    String agentId,
    String ip,
    boolean online,
    Date lastHeartbeat
) implements Serializable {}