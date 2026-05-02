package com.monitor.ui.desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitor.shared.constants.NetworkConstants;
import com.monitor.shared.model.Alert;
import com.monitor.ui.desktop.rmi.RMIServiceProxy;
import com.monitor.ui.desktop.view.DashboardView;

import javax.swing.Timer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DashboardController {

    private DashboardView view;
    private RMIServiceProxy proxy;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String restBase = "http://localhost:" + NetworkConstants.PORT_REST + "/api";

    public void launch() {
        proxy = new RMIServiceProxy();
        view = new DashboardView();
        view.show();
        Timer timer = new Timer(5000, e -> loadData());
        timer.start();
        loadData();
    }

    public void loadData() {
        try {
            // Metrics via RMI
            var latest = proxy.getLatestMetrics();
            var agents = proxy.getAgentList();
            view.updateMetrics(latest);
        } catch (Exception e) {
            System.err.println("Erreur chargement métriques RMI : " + e.getMessage());
        }

        // Alerts via REST
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(restBase + "/alerts"))
                .GET()
                .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            List<Alert> alerts = mapper.readValue(resp.body(), new TypeReference<List<Alert>>() {});
            view.updateAlerts(alerts);
        } catch (Exception e) {
            System.err.println("Erreur chargement alertes REST : " + e.getMessage());
        }
    }
}
