package com.monitor.ui.desktop.controller;

import com.monitor.ui.desktop.view.DashboardView;
import com.monitor.ui.desktop.rmi.RMIServiceProxy;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Contrôleur MVC du dashboard desktop.
 * Orchestre le rafraîchissement des données (toutes les 5 s) et les exports.
 */
public class DashboardController {
    private DashboardView view;
    private RMIServiceProxy proxy;
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    public void launch() {
        proxy = new RMIServiceProxy();
        view  = new DashboardView(this);
        view.show();
        // Rafraîchissement automatique toutes les 5 secondes
        javax.swing.Timer timer = new javax.swing.Timer(5000, e -> loadData());
        timer.start();
        loadData();
    }

    public void loadData() {
        if (!proxy.isConnected()) {
            proxy.connect();
            view.setStatus("⚠ Non connecté au serveur RMI", false);
            return;
        }
        try {
            Map<String, MetricData> latest = proxy.getLatestMetrics();
            List<Alert>             alerts  = proxy.getAlerts();
            String updateTime = FMT.format(Instant.now());
            view.updateMetrics(latest);
            view.updateAlerts(alerts);
            view.setStatus("● Connecté | Mise à jour : " + updateTime
                + " | " + latest.size() + " agent(s) actif(s)", true);
        } catch (Exception e) {
            view.setStatus("⚠ Erreur RMI : " + e.getMessage(), false);
        }
    }

    /** Export CSV ou JSON de tout l'historique, sauvegardé dans un fichier. */
    public void exportData(String format) {
        if (!proxy.isConnected()) {
            JOptionPane.showMessageDialog(null, "Non connecté au serveur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<String>    agentIds = proxy.getAgentList();
            List<MetricData> allData = new ArrayList<>();
            for (String id : agentIds) allData.addAll(proxy.getHistory(id));

            String content;
            String ext;
            if ("CSV".equals(format)) {
                content = toCSV(allData);
                ext = "csv";
            } else {
                content = toJSON(allData);
                ext = "json";
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("metrics_export." + ext));
            chooser.setFileFilter(new FileNameExtensionFilter(format + " files", ext));
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                    fw.write(content);
                }
                JOptionPane.showMessageDialog(null, "Export réussi : " + chooser.getSelectedFile().getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erreur export : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String toCSV(List<MetricData> data) {
        StringBuilder sb = new StringBuilder("agentId,cpuUsage,ramUsage,diskUsage,timestamp\n");
        for (MetricData d : data)
            sb.append(String.format("%s,%.2f,%.2f,%.2f,%d%n",
                d.agentId(), d.cpuUsage(), d.ramUsage(), d.diskUsage(), d.timestamp()));
        return sb.toString();
    }

    private String toJSON(List<MetricData> data) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            MetricData d = data.get(i);
            sb.append(String.format(
                "{\"agentId\":\"%s\",\"cpuUsage\":%.2f,\"ramUsage\":%.2f,\"diskUsage\":%.2f,\"timestamp\":%d}",
                d.agentId(), d.cpuUsage(), d.ramUsage(), d.diskUsage(), d.timestamp()));
            if (i < data.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
