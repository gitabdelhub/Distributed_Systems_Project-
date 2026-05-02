package com.monitor.server.network;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.model.MetricData;
import com.monitor.shared.utils.SerializationUtils;
import com.monitor.shared.constants.NetworkConstants;
import java.net.*;

public class UDPServer implements Runnable {
    private final ConcurrentDataStore store;

    public UDPServer(ConcurrentDataStore store) { this.store = store; }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(NetworkConstants.PORT_UDP)) {
            byte[] buffer = new byte[4096];
            System.out.println("UDP Server en écoute sur port " + NetworkConstants.PORT_UDP);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
                MetricData metricData = (MetricData) SerializationUtils.deserialize(data);
                store.save(metricData);
                System.out.println("Reçu de " + metricData.agentId() + " CPU=" + String.format("%.1f", metricData.cpuUsage()) + "%");
            }
        } catch (Exception e) {
            System.err.println("Erreur UDP : " + e.getMessage());
        }
    }
}
