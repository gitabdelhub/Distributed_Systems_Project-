package com.monitor.server.config;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.server.network.UdpMetricsReceiver;
import com.monitor.server.rmi.RMIMetricsServiceImpl;
import com.monitor.shared.constants.Constants;
import com.monitor.shared.rmi.RMIMetricsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

@Component
public class ServerInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        try {
            // ✅ Récupérer l'instance Singleton du store
            ConcurrentDataStore dataStore = ConcurrentDataStore.getInstance();

            // 1️⃣ Initialiser le registre RMI
            LocateRegistry.createRegistry(Constants.RMI_PORT);
            RMIMetricsService service = new RMIMetricsServiceImpl(dataStore);
            String rmiUrl = "rmi://localhost:" + Constants.RMI_PORT + "/" + Constants.RMI_SERVICE_NAME;
            Naming.rebind(rmiUrl, service);
            System.out.println("[RMI] Service bound: " + rmiUrl);

            // 2️⃣ Démarrer le récepteur UDP
            new UdpMetricsReceiver(Constants.UDP_PORT, dataStore).start();
            System.out.println("[UDP] Listener started on port " + Constants.UDP_PORT);

        } catch (Exception e) {
            System.err.println("[ERROR] Server initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}