package com.monitor.server.alerting;

import com.monitor.shared.constants.ThresholdConstants;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import java.util.UUID;

public class ThresholdEngine {
    public Alert check(MetricData data) {
        if (data.cpuUsage() > ThresholdConstants.CPU_CRITICAL)
            return new Alert(UUID.randomUUID().toString(), data.agentId(), "CPU", "CRITICAL", System.currentTimeMillis());
        if (data.ramUsage() > ThresholdConstants.RAM_CRITICAL)
            return new Alert(UUID.randomUUID().toString(), data.agentId(), "RAM", "CRITICAL", System.currentTimeMillis());
        if (data.diskUsage() > ThresholdConstants.DISK_CRITICAL)
            return new Alert(UUID.randomUUID().toString(), data.agentId(), "DISK", "CRITICAL", System.currentTimeMillis());
        return null;
    }
}
