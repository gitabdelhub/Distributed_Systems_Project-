package com.monitor.server.config;

import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.server.network.TCPServer;
import com.monitor.server.network.UDPServer;
import com.monitor.server.rmi.RMIMetricsServiceImpl;
import com.monitor.shared.constants.NetworkConstants;
import com.monitor.shared.rmi.RMIMetricsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Component
public class ServerInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        ConcurrentDataStore dataStore = ConcurrentDataStore.getInstance();

        // Fix RMI hostname for local-only deployments
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

        // Start RMI registry (single call)
        Registry registry = LocateRegistry.createRegistry(NetworkConstants.PORT_RMI);
        RMIMetricsService service = new RMIMetricsServiceImpl(dataStore);
        registry.rebind(NetworkConstants.RMI_SERVICE_NAME, service);
        System.out.println("[RMI] Service bound on port " + NetworkConstants.PORT_RMI);

        // Start UDP receiver (Java serialization, consistent with UDPSender)
        new Thread(new UDPServer(dataStore), "UDP-Receiver").start();
        System.out.println("[UDP] Listener started on port " + NetworkConstants.PORT_UDP);

        // Start TCP alert receiver
        new Thread(new TCPServer(dataStore), "TCP-Receiver").start();
        System.out.println("[TCP] Listener started on port " + NetworkConstants.PORT_TCP);
    }
}
