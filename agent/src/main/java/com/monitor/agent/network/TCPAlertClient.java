package com.monitor.agent.network;

import java.io.OutputStream;
import java.net.Socket;

import com.monitor.agent.config.AgentConfig;
import com.monitor.shared.model.Alert;
import com.monitor.shared.utils.Logger;
import com.monitor.shared.utils.SerializationUtils;

/**
 * Envoie les alertes critiques au serveur central via TCP (sérialisation Java).
 * L'adresse du serveur et le port sont lus depuis AgentConfig (agent.properties).
 */
public class TCPAlertClient {
    public void sendAlert(Alert alert) throws Exception {
        try (Socket socket = new Socket(AgentConfig.getServerHost(), AgentConfig.getTcpPort());
             OutputStream os = socket.getOutputStream()) {
            os.write(SerializationUtils.serialize(alert));
            Logger.warning("TCPAlertClient",
                "Alerte envoyée : " + alert.type() + " [" + alert.severity() + "]"
                + " → " + AgentConfig.getServerHost() + ":" + AgentConfig.getTcpPort());
        }
    }
}