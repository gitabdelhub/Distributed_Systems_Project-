package com.monitor.agent.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Charge la configuration de l'agent depuis agent.properties (classpath).
 * Valeurs par défaut cohérentes avec NetworkConstants si le fichier est absent.
 */
public class AgentConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = AgentConfig.class.getResourceAsStream("/agent.properties")) {
            if (is != null) {
                props.load(is);
                System.out.println("[AgentConfig] Configuration chargée depuis agent.properties");
            } else {
                System.out.println("[AgentConfig] Fichier agent.properties introuvable, valeurs par défaut utilisées");
            }
        } catch (Exception e) {
            System.err.println("[AgentConfig] Erreur de chargement : " + e.getMessage());
        }
    }

    public static String getServerHost() {
        return props.getProperty("agent.server.host", "localhost");
    }

    public static int getUdpPort() {
        return Integer.parseInt(props.getProperty("agent.server.port.udp", "5000"));
    }

    public static int getTcpPort() {
        return Integer.parseInt(props.getProperty("agent.server.port.tcp", "6000"));
    }

    public static int getSendIntervalMs() {
        return Integer.parseInt(props.getProperty("agent.send.interval.ms", "5000"));
    }
}
