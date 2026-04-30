package com.monitor.server.network;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.model.MetricData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpMetricsReceiver implements Runnable {
    private final int port;
    private final ConcurrentDataStore store;
    private final ObjectMapper mapper = new ObjectMapper();
    private volatile boolean running = true;

    public UdpMetricsReceiver(int port, ConcurrentDataStore store) {
        this.port = port;
        this.store = store;
    }

    public void start() {
        new Thread(this, "UDP-Receiver").start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("[UDP] Listening on port " + port);
            byte[] buffer = new byte[4096];
            
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                String json = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                MetricData data = mapper.readValue(json, MetricData.class);
                
                // ✅ CORRECTION : appeler save() avec un seul paramètre
                store.save(data);
                
                System.out.println("[UDP] Received metrics from " + data.agentId() + 
                    " | CPU=" + data.cpuUsage() + "% RAM=" + data.ramUsage() + "%");
                
            }
        } catch (Exception e) {
            if (running) System.err.println("[UDP] Error: " + e.getMessage());
        }
    }
}