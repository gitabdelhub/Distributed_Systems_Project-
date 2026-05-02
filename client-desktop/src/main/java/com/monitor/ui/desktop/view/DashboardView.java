package com.monitor.ui.desktop.view;

import com.monitor.shared.constants.ThresholdConstants;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardView {

    private JFrame frame;
    private JTabbedPane tabbedPane;

    // Tab 1 – Overview
    private DefaultTableModel overviewModel;

    // Tab 2 – Metrics (with color renderer)
    private DefaultTableModel metricsModel;

    // Tab 3 – Alerts
    private DefaultTableModel alertsModel;

    public void show() {
        frame = new JFrame("Système de Surveillance Distribué");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);

        tabbedPane = new JTabbedPane();

        // ── Tab 1: Overview ──────────────────────────────────────────────────
        overviewModel = new DefaultTableModel(
            new String[]{"Agent ID", "CPU %", "RAM %", "Disque %", "Dernière mise à jour"}, 0);
        JTable overviewTable = new JTable(overviewModel);
        tabbedPane.addTab("Vue générale", new JScrollPane(overviewTable));

        // ── Tab 2: Metrics with color coding ─────────────────────────────────
        metricsModel = new DefaultTableModel(
            new String[]{"Agent ID", "CPU %", "RAM %", "Disque %", "Timestamp"}, 0);
        JTable metricsTable = new JTable(metricsModel);
        metricsTable.setDefaultRenderer(Object.class, new MetricColorRenderer());
        tabbedPane.addTab("Métriques", new JScrollPane(metricsTable));

        // ── Tab 3: Alerts ────────────────────────────────────────────────────
        alertsModel = new DefaultTableModel(
            new String[]{"ID", "Agent", "Type", "Sévérité", "Timestamp"}, 0);
        JTable alertsTable = new JTable(alertsModel);
        tabbedPane.addTab("Alertes", new JScrollPane(alertsTable));

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void updateMetrics(Map<String, MetricData> data) {
        overviewModel.setRowCount(0);
        metricsModel.setRowCount(0);
        data.forEach((id, m) -> {
            Object[] row = new Object[]{
                m.agentId(),
                String.format("%.1f", m.cpuUsage()),
                String.format("%.1f", m.ramUsage()),
                String.format("%.1f", m.diskUsage()),
                m.timestamp()
            };
            overviewModel.addRow(row);
            metricsModel.addRow(row);
        });
    }

    public void updateAlerts(List<Alert> alerts) {
        alertsModel.setRowCount(0);
        for (Alert a : alerts) {
            alertsModel.addRow(new Object[]{
                a.id(), a.agentId(), a.type(), a.severity(), a.timestamp()
            });
        }
    }

    // ── Color renderer ───────────────────────────────────────────────────────
    private static class MetricColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // Columns 1 (CPU), 2 (RAM), 3 (Disk) contain numeric strings
            if (column >= 1 && column <= 3) {
                try {
                    double val = Double.parseDouble(value.toString().trim());
                    double critical = getCriticalThreshold(column);
                    double warning = critical * 0.80;
                    if (val >= critical) {
                        c.setBackground(new Color(255, 80, 80));   // red
                    } else if (val >= warning) {
                        c.setBackground(new Color(255, 200, 60));  // orange/yellow
                    } else {
                        c.setBackground(new Color(130, 210, 130)); // green
                    }
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }
            } else {
                c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            }
            return c;
        }

        private double getCriticalThreshold(int column) {
            switch (column) {
                case 1: return ThresholdConstants.CPU_CRITICAL;
                case 2: return ThresholdConstants.RAM_CRITICAL;
                case 3: return ThresholdConstants.DISK_CRITICAL;
                default: return 100.0;
            }
        }
    }
}
