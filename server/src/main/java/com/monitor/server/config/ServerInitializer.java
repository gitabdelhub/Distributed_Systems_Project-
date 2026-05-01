package com.monitor.server.config;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.server.network.TCPServer;
import com.monitor.server.network.UDPServer;
import com.monitor.server.rmi.RMIMetricsServiceImpl;
import com.monitor.shared.constants.NetworkConstants;
import com.monitor.shared.rmi.RMIMetricsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Initialisation des composants réseau au démarrage de Spring Boot.
 * Démarre le listener UDP (métriques), le serveur TCP (alertes) et le registre RMI.
 * Un seul démarrage par composant pour éviter les BindException.
 */
@Component
public class ServerInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        ConcurrentDataStore dataStore = ConcurrentDataStore.getInstance();

        // 1. Registre RMI
        try {
            LocateRegistry.createRegistry(NetworkConstants.PORT_RMI);
            RMIMetricsService service = new RMIMetricsServiceImpl(dataStore);
            String rmiUrl = "rmi://localhost:" + NetworkConstants.PORT_RMI
                          + "/" + NetworkConstants.RMI_SERVICE_NAME;
            Naming.rebind(rmiUrl, service);
            System.out.println("[RMI] Service lié : " + rmiUrl);
        } catch (Exception e) {
            System.err.println("[RMI] Erreur démarrage : " + e.getMessage());
        }

        // 2. Listener UDP — métriques (sérialisation Java)
        Thread udpThread = new Thread(new UDPServer(dataStore), "UDP-Server");
        udpThread.setDaemon(true);
        udpThread.start();
        System.out.println("[UDP] Listener démarré sur port " + NetworkConstants.PORT_UDP);

        // 3. Serveur TCP — alertes
        Thread tcpThread = new Thread(new TCPServer(dataStore), "TCP-Server");
        tcpThread.setDaemon(true);
        tcpThread.start();
        System.out.println("[TCP] Serveur démarré sur port " + NetworkConstants.PORT_TCP);
    }
}