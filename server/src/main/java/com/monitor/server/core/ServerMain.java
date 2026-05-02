package com.monitor.server.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.monitor.server")
public class ServerMain {
    public static void main(String[] args) throws Exception {
        ConcurrentDataStore store = ConcurrentDataStore.getInstance();
        new Thread(new com.monitor.server.network.UDPServer(store)).start();
        new Thread(new com.monitor.server.network.TCPServer(store)).start();
        com.monitor.server.rmi.RMIServer.start(store);
        SpringApplication.run(ServerMain.class, args);
    }
}
