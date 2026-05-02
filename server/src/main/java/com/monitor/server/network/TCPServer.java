package com.monitor.server.network;

import com.monitor.server.alerting.AlertDispatcher;
import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.model.Alert;
import com.monitor.shared.utils.SerializationUtils;
import com.monitor.shared.constants.NetworkConstants;
import java.net.*;
import java.io.*;

public class TCPServer implements Runnable {
    private final ConcurrentDataStore store;
    private final AlertDispatcher dispatcher = new AlertDispatcher();

    public TCPServer(ConcurrentDataStore store) { this.store = store; }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConstants.PORT_TCP)) {
            System.out.println("TCP Server en écoute sur port " + NetworkConstants.PORT_TCP);
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (Exception e) {
            System.err.println("Erreur TCP : " + e.getMessage());
        }
    }

    private void handleClient(Socket client) {
        try (InputStream is = client.getInputStream()) {
            Alert alert = (Alert) SerializationUtils.deserialize(is.readAllBytes());
            store.addAlert(alert);
            dispatcher.dispatch(alert);
        } catch (Exception e) {
            System.err.println("Erreur traitement alerte TCP : " + e.getMessage());
        }
    }
}
