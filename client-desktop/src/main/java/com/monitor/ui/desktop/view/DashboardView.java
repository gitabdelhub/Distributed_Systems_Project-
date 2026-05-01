package com.monitor.ui.desktop.view;

import com.monitor.ui.desktop.controller.DashboardController;
import com.monitor.shared.model.Alert;
import com.monitor.shared.model.MetricData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Interface graphique Swing du client desktop (MVC - Vue).
 *
 * Onglets :
 *  1. Tableau de bord — métriques temps réel avec code couleur CPU/RAM/Disk
 *  2. Alertes         — liste des alertes avec sévérité colorée
 *  3. Export          — export CSV / JSON des données historiques
 *
 * Rafraîchissement automatique toutes les 5 secondes via DashboardController.
 */
public class DashboardView {

    // ── Constantes visuelles ──────────────────────────────────────────────────
    private static final Color COLOR_OK       = new Color(220, 252, 231); // vert clair
    private static final Color COLOR_WARN     = new Color(254, 243, 199); // orange clair
    private static final Color COLOR_CRITICAL = new Color(254, 226, 226); // rouge clair
    private static final Color COLOR_HEADER   = new Color(30, 58, 138);   // bleu foncé
    private static final Color COLOR_BG       = new Color(241, 245, 249); // gris très clair

    // ── Composants principaux ─────────────────────────────────────────────────
    private JFrame frame;
    private JLabel statusLabel;

    // Onglet métriques
    private DefaultTableModel metricsModel;
    private JTable metricsTable;

    // Onglet alertes
    private DefaultTableModel alertsModel;
    private JTable alertsTable;

    private final DashboardController controller;

    public DashboardView(DashboardController controller) {
        this.controller = controller;
    }

    // ── Construction de la fenêtre ────────────────────────────────────────────

    public void show() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame("Système de Surveillance Distribué");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 680);
        frame.setMinimumSize(new Dimension(900, 550));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(buildHeader(), BorderLayout.NORTH);
        frame.add(buildTabbedPane(), BorderLayout.CENTER);
        frame.add(buildStatusBar(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ── En-tête ───────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("🖥  Système de Surveillance Distribué");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Monitoring distribué temps réel · Java RMI + UDP/TCP");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(186, 230, 253));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);
        header.add(textPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("⟳ Rafraîchir");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> controller.loadData());
        header.add(refreshBtn, BorderLayout.EAST);

        return header;
    }

    // ── Onglets ───────────────────────────────────────────────────────────────

    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab("📊  Tableau de bord", buildMetricsTab());
        tabs.addTab("🚨  Alertes",          buildAlertsTab());
        tabs.addTab("📤  Export",            buildExportTab());

        return tabs;
    }

    // ── Onglet 1 : Métriques ──────────────────────────────────────────────────

    private JPanel buildMetricsTab() {
        String[] cols = {"Agent ID", "CPU (%)", "RAM (%)", "Disque (%)", "État", "Timestamp"};
        metricsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        metricsTable = new JTable(metricsModel);
        metricsTable.setRowHeight(28);
        metricsTable.setFont(new Font("Monospaced", Font.PLAIN, 13));
        metricsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        metricsTable.getTableHeader().setBackground(new Color(226, 232, 240));

        // Renderer coloré pour colonnes CPU/RAM/Disk
        TableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
                if (!isSelected && (col == 1 || col == 2 || col == 3)) {
                    try {
                        double v = Double.parseDouble(value.toString().replace(",", "."));
                        c.setBackground(v >= 85 ? COLOR_CRITICAL : v >= 60 ? COLOR_WARN : COLOR_OK);
                    } catch (NumberFormatException e) {
                        System.err.println("[DashboardView] Valeur non numérique dans colonne " + col + " : " + value);
                        c.setBackground(Color.WHITE);
                    }
                } else if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        for (int i = 1; i <= 4; i++) metricsTable.getColumnModel().getColumn(i).setCellRenderer(colorRenderer);
        metricsTable.getColumnModel().getColumn(0).setPreferredWidth(280);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel legend = new JLabel(
            "  Légende :  🟢 < 60%   🟠 60–84%   🔴 ≥ 85%  (CPU / RAM / Disque)");
        legend.setFont(new Font("SansSerif", Font.ITALIC, 11));
        legend.setBorder(new EmptyBorder(5, 0, 8, 0));

        panel.add(legend, BorderLayout.NORTH);
        panel.add(new JScrollPane(metricsTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Onglet 2 : Alertes ────────────────────────────────────────────────────

    private JPanel buildAlertsTab() {
        String[] cols = {"ID Alerte", "Agent ID", "Type", "Sévérité", "Timestamp"};
        alertsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        alertsTable = new JTable(alertsModel);
        alertsTable.setRowHeight(26);
        alertsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        alertsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        alertsTable.getTableHeader().setBackground(new Color(226, 232, 240));

        // Renderer : rouge pour CRITICAL, orange pour WARNING
        TableCellRenderer alertRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    String sev = (String) table.getModel().getValueAt(row, 3);
                    c.setBackground("CRITICAL".equals(sev) ? COLOR_CRITICAL
                                  : "WARNING".equals(sev)  ? COLOR_WARN
                                  : Color.WHITE);
                }
                return c;
            }
        };
        for (int i = 0; i < 5; i++) alertsTable.getColumnModel().getColumn(i).setCellRenderer(alertRenderer);
        alertsTable.getColumnModel().getColumn(0).setPreferredWidth(270);
        alertsTable.getColumnModel().getColumn(1).setPreferredWidth(270);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Onglet 3 : Export ─────────────────────────────────────────────────────

    private JPanel buildExportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Export des données historiques");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btnPanel.setOpaque(false);

        JButton csvBtn = new JButton("📄  Exporter en CSV");
        csvBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        csvBtn.setBackground(new Color(22, 163, 74));
        csvBtn.setForeground(Color.WHITE);
        csvBtn.setFocusPainted(false);
        csvBtn.setPreferredSize(new Dimension(200, 40));
        csvBtn.addActionListener(e -> controller.exportData("CSV"));

        JButton jsonBtn = new JButton("📋  Exporter en JSON");
        jsonBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        jsonBtn.setBackground(new Color(37, 99, 235));
        jsonBtn.setForeground(Color.WHITE);
        jsonBtn.setFocusPainted(false);
        jsonBtn.setPreferredSize(new Dimension(200, 40));
        jsonBtn.addActionListener(e -> controller.exportData("JSON"));

        btnPanel.add(csvBtn);
        btnPanel.add(jsonBtn);

        JLabel info = new JLabel(
            "<html><br><i>L'export inclut toutes les métriques reçues depuis le démarrage du serveur.<br>"
            + "Les fichiers sont sauvegardés à l'emplacement choisi.</i></html>");
        info.setFont(new Font("SansSerif", Font.PLAIN, 12));
        info.setForeground(Color.DARK_GRAY);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(title);
        content.add(btnPanel);
        content.add(Box.createVerticalStrut(20));
        content.add(info);

        panel.add(content, BorderLayout.NORTH);
        return panel;
    }

    // ── Barre de statut ───────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(203, 213, 225)));
        statusLabel = new JLabel("● Démarrage...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bar.add(statusLabel);
        return bar;
    }

    // ── Méthodes de mise à jour (appelées par le contrôleur) ──────────────────

    public void updateMetrics(Map<String, MetricData> data) {
        SwingUtilities.invokeLater(() -> {
            metricsModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            data.forEach((id, m) -> {
                String etat = m.cpuUsage() >= 85 || m.ramUsage() >= 90 || m.diskUsage() >= 95
                              ? "⚠ Critique" : m.cpuUsage() >= 60 || m.ramUsage() >= 70
                              ? "⚠ Avertissement" : "✔ Normal";
                metricsModel.addRow(new Object[]{
                    m.agentId(),
                    String.format("%.1f", m.cpuUsage()),
                    String.format("%.1f", m.ramUsage()),
                    String.format("%.1f", m.diskUsage()),
                    etat,
                    sdf.format(new Date(m.timestamp()))
                });
            });
        });
    }

    public void updateAlerts(List<Alert> alerts) {
        SwingUtilities.invokeLater(() -> {
            alertsModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss");
            // Afficher les alertes les plus récentes en premier
            List<Alert> sorted = new ArrayList<>(alerts);
            sorted.sort(Comparator.comparingLong(Alert::timestamp).reversed());
            for (Alert a : sorted) {
                alertsModel.addRow(new Object[]{
                    a.id(),
                    a.agentId(),
                    a.type(),
                    a.severity(),
                    sdf.format(new Date(a.timestamp()))
                });
            }
        });
    }

    public void setStatus(String message, boolean connected) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(connected ? new Color(22, 163, 74) : new Color(220, 38, 38));
        });
    }
}
