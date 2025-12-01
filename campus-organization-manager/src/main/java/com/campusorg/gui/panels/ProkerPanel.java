package com.campusorg.gui.panels;

import com.campusorg.models.Division;
import com.campusorg.models.Proker;
import com.campusorg.services.OrgManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProkerPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    // --- LIST VIEW COMPONENTS ---
    private JTable table;
    private DefaultTableModel model;

    // --- DETAIL VIEW COMPONENTS ---
    private JLabel lblName, lblStatus, lblDivAsal, lblKetupel, lblWaketupel;
    private JTextArea txtDesc;
    private Proker currentProker; // Untuk menyimpan proker yang sedang dibuka

    public ProkerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(244, 246, 247));

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(new Color(244, 246, 247));

        // Inisialisasi 2 Tampilan
        initListView();
        initDetailView();

        mainContainer.add(createListViewPanel(), "LIST");
        mainContainer.add(createDetailViewPanel(), "DETAIL");

        add(mainContainer, BorderLayout.CENTER);
    }

    // ==================== 1. LIST VIEW (TABEL) ====================
    private JPanel createListViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Daftar Program Kerja Seluruh HIMAKOM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // Tabel Setup
        String[] cols = { "Nama Proker", "Status", "Divisi Penanggung Jawab", "Ketua Pelaksana" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(33, 47, 60));
        table.getTableHeader().setForeground(Color.WHITE);

        // Double Click Listener
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedProker();
                }
            }
        });

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel hint = new JLabel("ℹ️ Tips: Klik 2x pada baris untuk melihat detail & ubah status.");
        hint.setForeground(Color.GRAY);
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== 2. DETAIL VIEW ====================
    private JPanel createDetailViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Header (Tombol Back)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        JButton btnBack = new JButton("⬅ Kembali ke List");
        btnBack.setBackground(Color.WHITE);
        btnBack.addActionListener(e -> showList());
        topBar.add(btnBack);

        // Content Card (Kotak Putih)
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Init Components
        lblName = new JLabel("Nama Proker");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblName.setForeground(new Color(41, 128, 185));

        lblStatus = new JLabel("Status: -");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setOpaque(true);
        lblStatus.setBorder(new EmptyBorder(5, 10, 5, 10));

        lblDivAsal = new JLabel("Divisi: -");
        lblKetupel = new JLabel("Ketupel: -");
        lblWaketupel = new JLabel("Waketupel: -");

        txtDesc = new JTextArea(5, 40);
        txtDesc.setEditable(false);
        txtDesc.setBorder(BorderFactory.createTitledBorder("Divisi/Seksi Internal Proker"));
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnUpdate = new JButton("Ubah Status (Cycle)");
        btnUpdate.setBackground(new Color(230, 126, 34)); // Orange
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> actionUpdateStatus());

        // Layouting
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(lblName, gbc);
        gbc.gridy = 1;
        card.add(lblStatus, gbc);
        gbc.gridy = 2;
        card.add(new JSeparator(), gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        card.add(lblDivAsal, gbc);
        gbc.gridx = 1;
        card.add(new JLabel(""), gbc); // Spacer

        gbc.gridx = 0;
        gbc.gridy = 4;
        card.add(lblKetupel, gbc);
        gbc.gridx = 1;
        card.add(lblWaketupel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        card.add(new JScrollPane(txtDesc), gbc);

        gbc.gridy = 6;
        card.add(btnUpdate, gbc);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    // ==================== LOGIC METHODS ====================

    // Dipanggil dari MainFrame saat menu diklik
    public void refreshData() {
        model.setRowCount(0);
        String[] divNames = OrgManager.getInstance().getDivisionNames();

        for (String dName : divNames) {
            Division div = OrgManager.getInstance().getDivisionByName(dName);
            if (div != null) {
                for (Proker p : div.getProkerList()) {
                    model.addRow(new Object[] {
                            p.getNamaProker(),
                            p.getStatus(),
                            dName,
                            p.getKetupel()
                    });
                }
            }
        }
    }

    private void openSelectedProker() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        int modelRow = table.convertRowIndexToModel(row);
        String pName = (String) model.getValueAt(modelRow, 0);
        String pDivName = (String) model.getValueAt(modelRow, 2);

        // Cari Objek Proker Asli
        Division div = OrgManager.getInstance().getDivisionByName(pDivName);
        if (div != null) {
            for (Proker p : div.getProkerList()) {
                if (p.getNamaProker().equals(pName)) {
                    currentProker = p;
                    populateDetail();
                    cardLayout.show(mainContainer, "DETAIL");
                    return;
                }
            }
        }
    }

    private void populateDetail() {
        if (currentProker == null)
            return;

        lblName.setText(currentProker.getNamaProker());
        lblDivAsal.setText("Divisi Penanggung Jawab: " + currentProker.getParentDivisi());
        lblKetupel.setText("Ketua Pelaksana: " + currentProker.getKetupel());
        lblWaketupel.setText("Wakil Ketupel: " + currentProker.getWaketupel());
        txtDesc.setText(currentProker.getDeskripsiDivisi());

        updateStatusLabel(currentProker.getStatus());
    }

    private void updateStatusLabel(String status) {
        lblStatus.setText("Status: " + status);
        if (status.equals("Selesai"))
            lblStatus.setBackground(new Color(46, 204, 113)); // Hijau
        else if (status.equals("Berjalan"))
            lblStatus.setBackground(new Color(52, 152, 219)); // Biru
        else
            lblStatus.setBackground(new Color(241, 196, 15)); // Kuning
    }

    private void actionUpdateStatus() {
        if (currentProker == null)
            return;

        String s = currentProker.getStatus();
        // Cycle: Rencana -> Berjalan -> Selesai -> Rencana
        if (s.equals("Rencana"))
            currentProker.setStatus("Berjalan");
        else if (s.equals("Berjalan"))
            currentProker.setStatus("Selesai");
        else
            currentProker.setStatus("Rencana");

        updateStatusLabel(currentProker.getStatus());
        JOptionPane.showMessageDialog(this, "Status diubah menjadi: " + currentProker.getStatus());
    }

    private void showList() {
        refreshData(); // Refresh tabel saat kembali
        cardLayout.show(mainContainer, "LIST");
    }

    // Init Component (Kosong, dipanggil di Constructor)
    private void initListView() {
    }

    private void initDetailView() {
    }
}
