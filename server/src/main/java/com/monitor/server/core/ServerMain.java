package com.monitor.server.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du serveur central.
 * Le démarrage des composants réseau (UDP, TCP, RMI) est délégué à
 * {@link com.monitor.server.config.ServerInitializer} (Spring CommandLineRunner),
 * ce qui évite les conflits de ports dus aux démarrages dupliqués.
 */
@SpringBootApplication
public class ServerMain {
    public static void main(String[] args) {
        SpringApplication.run(ServerMain.class, args);
    }
}
