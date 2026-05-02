<<<<<<< HEAD
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

// @Component
public class ServerInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        try {
            // Get Singleton instance of the data store
            ConcurrentDataStore dataStore = ConcurrentDataStore.getInstance();

            // 1. Initialize the RMI registry
            LocateRegistry.createRegistry(Constants.RMI_PORT);
            RMIMetricsService service = new RMIMetricsServiceImpl(dataStore);
            String rmiUrl = "rmi://localhost:" + Constants.RMI_PORT + "/" + Constants.RMI_SERVICE_NAME;
            Naming.rebind(rmiUrl, service);
            System.out.println("[RMI] Service bound: " + rmiUrl);

            // 2. Start the UDP receiver
            new UdpMetricsReceiver(Constants.UDP_PORT, dataStore).start();
            System.out.println("[UDP] Listener started on port " + Constants.UDP_PORT);

        } catch (Exception e) {
            System.err.println("[ERROR] Server initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
=======
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
>>>>>>> 571da7956945e98c1c15a481251ea9217044e674
