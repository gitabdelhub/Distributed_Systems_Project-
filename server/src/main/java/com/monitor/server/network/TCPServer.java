package com.monitor.server.network;

import com.monitor.server.alerting.AlertDispatcher;
import com.monitor.server.core.ConcurrentDataStore;
import com.monitor.shared.constants.NetworkConstants;
import com.monitor.shared.model.Alert;
import com.monitor.shared.utils.SerializationUtils;
import java.net.*;
import java.io.*;

/**
 * Serveur TCP — reçoit les alertes envoyées par les agents (sérialisation Java).
 * Chaque connexion est traitée dans un thread séparé pour la concurrence.
 */
public class TCPServer implements Runnable {
    private final ConcurrentDataStore store;
    private final AlertDispatcher dispatcher = new AlertDispatcher();

    public TCPServer(ConcurrentDataStore store) { this.store = store; }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConstants.PORT_TCP)) {
            System.out.println("[TCP] En écoute sur port " + NetworkConstants.PORT_TCP);
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client), "TCP-Handler").start();
            }
        } catch (Exception e) {
            System.err.println("[TCP] Erreur : " + e.getMessage());
        }
    }

    private void handleClient(Socket client) {
        try (InputStream is = client.getInputStream()) {
            Alert alert = (Alert) SerializationUtils.deserialize(is.readAllBytes());
            store.addAlert(alert);
            dispatcher.dispatch(alert);
        } catch (Exception e) {
            System.err.println("[TCP] Erreur traitement alerte : " + e.getMessage());
        }
    }
}
