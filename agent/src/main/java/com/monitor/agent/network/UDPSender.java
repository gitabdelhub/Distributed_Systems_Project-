package com.monitor.agent.network;

import com.monitor.agent.config.AgentConfig;
import com.monitor.shared.model.MetricData;
import com.monitor.shared.utils.Logger;
import com.monitor.shared.utils.SerializationUtils;
import java.net.*;

/**
 * Envoie les métriques au serveur central via UDP (sérialisation Java).
 * L'adresse du serveur et le port sont lus depuis AgentConfig (agent.properties).
 */
public class UDPSender {
    public void send(MetricData data) throws Exception {
        byte[] bytes = SerializationUtils.serialize(data);
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(AgentConfig.getServerHost());
            DatagramPacket packet = new DatagramPacket(
                bytes, bytes.length, address, AgentConfig.getUdpPort()
            );
            socket.send(packet);
            Logger.info("UDPSender", "Métriques envoyées → "
                + AgentConfig.getServerHost() + ":" + AgentConfig.getUdpPort());
        }
    }
}