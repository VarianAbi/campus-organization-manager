package com.campusorg.gui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.campusorg.models.Proker;
import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.singleton.OrgManager;
import com.campusorg.utils.Constants;

public class ProkerPanel extends JPanel {

    private static final String FONT_INRIA_SANS = "Inria Sans";
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    // --- LIST VIEW COMPONENTS ---
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    // --- DETAIL VIEW COMPONENTS ---
    private JLabel lblName;
    private JLabel lblStatus;
    private JLabel lblDivAsal;
    private JLabel lblKetupel;
    private JLabel lblWaketupel;
    private JTextArea txtDesc;
    private JTextArea txtCatatan;
    private JProgressBar progressBar;
    private JLabel lblLampiran;
    private JList<String> listTim;
    private transient Proker currentProker; // Untuk menyimpan proker yang sedang dibuka

    public ProkerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241)); // Lebih terang

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(new Color(236, 240, 241));

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

        // --- Header & Filter Bar mengikuti AllMembersPanel ---
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);
        JLabel title = new JLabel("📅 Daftar Program Kerja Seluruh HIMAKOM");
        title.setFont(new Font(FONT_INRIA_SANS, Font.BOLD, 22));
        title.setForeground(Color.BLACK);
        title.setForeground(Color.BLACK);

        // Tombol Tambah & Hapus Proker
        JButton btnAddProker = new JButton("➕ Tambah Proker");
        btnAddProker.setBackground(new Color(46, 204, 113));
        btnAddProker.setForeground(Color.BLACK);
        btnAddProker.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnAddProker.setFocusPainted(false);
        btnAddProker.addActionListener(e -> showInputProkerDialog());

        JButton btnDeleteProker = new JButton("🗑️ Hapus Proker");
        btnDeleteProker.setBackground(new Color(231, 76, 60));
        btnDeleteProker.setForeground(Color.black);
        btnDeleteProker.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnDeleteProker.setFocusPainted(false);
        btnDeleteProker.addActionListener(e -> deleteSelectedProker());

        // Filter status
        statusFilter = new JComboBox<>(new String[] { Constants.STATUS_SEMUA, Constants.STATUS_RENCANA, Constants.STATUS_BERJALAN, Constants.STATUS_SELESAI });
        statusFilter.setSelectedIndex(0);
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setForeground(new Color(41, 128, 185));
        statusFilter.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        statusFilter.addActionListener(e -> doFilter());

        // Search field
        searchField = new JTextField(12);
        searchField.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        searchField.addActionListener(e -> doFilter());

        // Panel Filter bergaya putih dengan border, seperti AllMembersPanel
        JPanel filterPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPnl.setBackground(Color.WHITE);
        filterPnl.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        JLabel lblCari = new JLabel("🔍 Cari:");
        lblCari.setForeground(Color.BLACK);
        JLabel lblStatusFilterLabel = new JLabel("  Status:");
        lblStatusFilterLabel.setForeground(Color.BLACK);
        filterPnl.add(lblCari);
        filterPnl.add(searchField);
        filterPnl.add(lblStatusFilterLabel);
        filterPnl.add(statusFilter);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAddProker);
        btnPanel.add(btnDeleteProker);
        actionPanel.add(filterPnl, BorderLayout.WEST);
        actionPanel.add(btnPanel, BorderLayout.EAST);

        topContainer.add(title, BorderLayout.NORTH);
        topContainer.add(actionPanel, BorderLayout.CENTER);

        // Tabel Setup
        // Kolom: Nama Proker, DBU, Status, Ketua Pelaksana, Progress
        String[] cols = { "Nama Proker", "DBU", "Status", "Ketua Pelaksana", "Progress (%)" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font(FONT_INRIA_SANS, Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(255, 234, 167));
        table.setSelectionForeground(Color.BLACK);

        // HANYA lakukan ini, jangan yang lain!
        JScrollPane scrollPane = new JScrollPane(table);

        // Double Click Listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedProker();
                }
            }
        });

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER); // PASTIKAN INI!

        JLabel hint = new JLabel("ℹ️ Klik 2x pada baris untuk melihat detail & ubah status.");
        hint.setForeground(new Color(41, 128, 185));
        hint.setFont(new Font(Constants.FONT_POPPINS, Font.ITALIC, 12));
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
        topBar.setOpaque(true);
        topBar.setBackground(new Color(41, 128, 185));
        JButton btnBack = new JButton("⬅ Kembali ke List");
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(new Color(41, 128, 185));
        btnBack.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        btnBack.addActionListener(e -> showList());
        topBar.add(btnBack);

        // Tombol edit & hapus (dummy)
        JButton btnEdit = new JButton("✏️ Edit");
        btnEdit.setBackground(new Color(255, 234, 167)); // Kuning terang
        btnEdit.setForeground(new Color(41, 128, 185));
        btnEdit.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnEdit.setFocusPainted(false);
        btnEdit.setBorder(BorderFactory.createLineBorder(new Color(241, 196, 15), 2));
        btnEdit.addActionListener(e -> editCurrentProker());
        JButton btnDelete = new JButton("🗑️ Hapus");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.black);
        btnDelete.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(BorderFactory.createLineBorder(new Color(192, 57, 43), 2));
        btnDelete.addActionListener(e -> deleteCurrentProker());
        topBar.add(btnEdit);
        topBar.add(btnDelete);

        // Content Card (Kotak Putih dengan border biru)
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(41, 128, 185), 3, true),
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblName = new JLabel("Nama Proker");
        lblName.setFont(new Font(FONT_INRIA_SANS, Font.BOLD, 26));
        lblName.setForeground(new Color(41, 128, 185));
        lblName.setForeground(new Color(41, 128, 185));

        lblStatus = new JLabel("Status: -");
        lblStatus.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 15));
        lblStatus.setOpaque(true);
        lblStatus.setBorder(new EmptyBorder(5, 10, 5, 10));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setForeground(new Color(41, 128, 185));
        progressBar.setBackground(new Color(236, 240, 241));

        lblDivAsal = new JLabel("Divisi: -");
        lblKetupel = new JLabel("Ketua Pelaksana: -");
        lblWaketupel = new JLabel("Wakil Ketua Pelaksana: -");

        txtDesc = new JTextArea(4, 40);
        txtDesc.setEditable(false);
        txtDesc.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185)),
                "Divisi/Seksi Internal Proker"));
        txtDesc.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 13));

        // Lampiran (dummy)
        lblLampiran = new JLabel("Lampiran: - (belum ada)");
        lblLampiran.setFont(new Font(Constants.FONT_POPPINS, Font.ITALIC, 12));
        lblLampiran.setForeground(new Color(127, 140, 141));

        // Catatan (dummy)
        txtCatatan = new JTextArea(2, 40);
        txtCatatan.setEditable(false);
        txtCatatan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185)),
                "Catatan/Update"));
        txtCatatan.setText("-");

        // Daftar tim (dummy)
        listTim = new JList<>(new String[] { "(belum ada data tim)" });
        listTim.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185)),
                "Tim Pelaksana"));

        JButton btnUpdate = new JButton("Ubah Status (Cycle)");
        btnUpdate.setBackground(new Color(41, 128, 185));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        btnUpdate.addActionListener(e -> actionUpdateStatus());

        // Layouting
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(lblName, gbc);
        gbc.gridy = 1;
        card.add(lblStatus, gbc);
        gbc.gridy = 2;
        card.add(progressBar, gbc);
        gbc.gridy = 3;
        card.add(new JSeparator(), gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        card.add(lblDivAsal, gbc);
        gbc.gridx = 1;
        card.add(new JLabel(""), gbc); // Spacer

        gbc.gridx = 0;
        gbc.gridy = 5;
        card.add(lblKetupel, gbc);
        gbc.gridx = 1;
        card.add(lblWaketupel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        card.add(new JScrollPane(txtDesc), gbc);

        gbc.gridy = 7;
        card.add(lblLampiran, gbc);

        gbc.gridy = 8;
        card.add(new JScrollPane(txtCatatan), gbc);

        gbc.gridy = 9;
        card.add(new JScrollPane(listTim), gbc);

        gbc.gridy = 10;
        card.add(btnUpdate, gbc);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(card, BorderLayout.CENTER);

        return panel;
    }

    // ==================== LOGIC METHODS ====================

    public void refreshData() {
        model.setRowCount(0);
        String[] divNames = OrgManager.getInstance().getDivisionNames();

        for (String dName : divNames) {
            Division div = OrgManager.getInstance().getDivisionByName(dName);
            if (div != null) {
                for (Proker p : div.getProkerList()) {
                    model.addRow(new Object[] {
                            p.getNamaProker(),
                            dName, // DBU/Divisi
                            p.getStatus(),
                            p.getKetupel(),
                            p.getProgress()
                    });
                }
            }
        }
        doFilter();
    }

    private void doFilter() {
        String search = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String nama = ((String) model.getValueAt(i, 0)).toLowerCase();
            String stat = ((String) model.getValueAt(i, 2));
            boolean match = true;
            if (!search.isEmpty() && !nama.contains(search))
                match = false;
            if (!"Semua Status".equals(status) && !stat.equals(status))
                match = false;
            if (!match)
                model.removeRow(i);
        }
    }

    private void openSelectedProker() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        int modelRow = table.convertRowIndexToModel(row);
        String pName = (String) model.getValueAt(modelRow, 0);
        String pDivName = (String) model.getValueAt(modelRow, 1);

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
        lblWaketupel.setText("Wakil Ketua Pelaksana: " + currentProker.getWaketupel());
        txtDesc.setText(currentProker.getDeskripsiDivisi());

        updateStatusLabel(currentProker.getStatus());
        int prog = currentProker.getProgress();
        progressBar.setValue(prog);
        progressBar.setString(prog + "%");

        lblLampiran.setText("Lampiran: - (belum ada)");
        txtCatatan.setText("-");
        listTim.setListData(new String[] { "(belum ada data tim)" });
    }

    private void updateStatusLabel(String status) {
        lblStatus.setText("Status: " + status);
        switch (status) {
            case Constants.STATUS_SELESAI -> {
                lblStatus.setBackground(new Color(46, 204, 113));
                lblStatus.setForeground(Color.WHITE);
            }
            case Constants.STATUS_BERJALAN -> {
                lblStatus.setBackground(new Color(52, 152, 219));
                lblStatus.setForeground(Color.WHITE);
            }
            default -> {
                lblStatus.setBackground(new Color(241, 196, 15));
                lblStatus.setForeground(new Color(44, 62, 80));
            }
        }
    }

    private void actionUpdateStatus() {
        if (currentProker == null)
            return;

        String s = currentProker.getStatus();
        switch (s) {
            case Constants.STATUS_RENCANA -> currentProker.setStatus(Constants.STATUS_BERJALAN);
            case Constants.STATUS_BERJALAN -> currentProker.setStatus(Constants.STATUS_SELESAI);
            default -> currentProker.setStatus(Constants.STATUS_RENCANA);
        }

        updateStatusLabel(currentProker.getStatus());
        JOptionPane.showMessageDialog(this, "Status diubah menjadi: " + currentProker.getStatus());
        switch (currentProker.getStatus()) {
            case Constants.STATUS_RENCANA -> currentProker.setProgress(0);
            case Constants.STATUS_BERJALAN -> currentProker.setProgress(50);
            default -> currentProker.setProgress(100);
        }
        progressBar.setValue(currentProker.getProgress());
        progressBar.setString(currentProker.getProgress() + "%");
    }

    private void showList() {
        refreshData();
        cardLayout.show(mainContainer, "LIST");
    }

    // ==================== TAMBAH PROKER DIALOG (Sederhana) ====================
    private void showInputProkerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Input Program Kerja Baru", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField inpNama = new JTextField();
        JComboBox<String> inpDiv = new JComboBox<>(OrgManager.getInstance().getDivisionNames());
        JTextField inpKetupel = new JTextField();
        JTextField inpWaketupel = new JTextField();
        JTextArea inpDesc = new JTextArea(3, 20);

        JButton btnSave = new JButton("SIMPAN");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 12));

        btnSave.addActionListener(e -> {
            String nama = inpNama.getText().trim();
            String div = (String) inpDiv.getSelectedItem();
            String ketupel = inpKetupel.getText().trim();
            String waketupel = inpWaketupel.getText().trim();
            String desc = inpDesc.getText().trim();
            if (nama.isEmpty() || ketupel.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nama Proker & Ketuplak wajib diisi!");
                return;
            }
            Proker p = new Proker(nama, desc, ketupel, waketupel, Constants.STATUS_RENCANA, 0, div);
            OrgManager.getInstance().getDivisionByName(div).addProker(p);
            JOptionPane.showMessageDialog(dialog, "Berhasil Input Proker: " + nama);
            dialog.dispose();
            refreshData();
        });

        formPanel.add(new JLabel("Nama Proker:"));
        formPanel.add(inpNama);
        formPanel.add(new JLabel("Divisi Penanggung Jawab:"));
        formPanel.add(inpDiv);
        formPanel.add(new JLabel("Ketua Pelaksana:"));
        formPanel.add(inpKetupel);
        formPanel.add(new JLabel("Wakil Pelaksana:"));
        formPanel.add(inpWaketupel);
        formPanel.add(new JLabel("Deskripsi:"));
        formPanel.add(new JScrollPane(inpDesc));
        formPanel.add(btnSave);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // --- FITUR HAPUS PROKER DARI LIST ---
    private void deleteSelectedProker() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih program kerja yang ingin dihapus!", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Konversi index jika ada sorting
        int modelRow = table.convertRowIndexToModel(row);
        String prokerName = (String) model.getValueAt(modelRow, 0);
        String divisi = (String) model.getValueAt(modelRow, 1);

        // Konfirmasi hapus
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus program kerja:\n\"" + prokerName + "\"\nDari divisi: " + divisi,
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Division div = OrgManager.getInstance().getDivisionByName(divisi);
            if (div != null) {
                Proker target = null;
                for (Proker p : div.getProkerList()) {
                    if (p.getNamaProker().equals(prokerName)) {
                        target = p;
                        break;
                    }
                }

                if (target != null) {
                    div.getProkerList().remove(target);
                    JOptionPane.showMessageDialog(this, "Program kerja berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshData(); // Refresh tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Program kerja tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // --- FITUR HAPUS PROKER DARI DETAIL VIEW ---
    private void deleteCurrentProker() {
        if (currentProker == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus program kerja:\n\"" + currentProker.getNamaProker() + "\"?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String divisiName = currentProker.getParentDivisi();
            Division div = OrgManager.getInstance().getDivisionByName(divisiName);
            
            if (div != null) {
                div.getProkerList().remove(currentProker);
                JOptionPane.showMessageDialog(this, "Program kerja berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                showList(); // Kembali ke list view
                refreshData();
            }
        }
    }

    // --- FITUR EDIT PROKER DARI DETAIL VIEW ---
    private void editCurrentProker() {
        if (currentProker == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Program Kerja", true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(9, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JTextField inpNama = new JTextField(currentProker.getNamaProker());
        JComboBox<String> inpStatus = new JComboBox<>(new String[]{Constants.STATUS_RENCANA, Constants.STATUS_BERJALAN, Constants.STATUS_SELESAI});
        inpStatus.setSelectedItem(currentProker.getStatus());
        JTextField inpKetupel = new JTextField(currentProker.getKetupel());
        JTextField inpWaketupel = new JTextField(currentProker.getWaketupel());
        JTextArea inpDesc = new JTextArea(currentProker.getDeskripsiDivisi(), 5, 20);
        inpDesc.setLineWrap(true);
        inpDesc.setWrapStyleWord(true);

        JButton btnSave = new JButton("💾 Simpan Perubahan");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 13));
        btnSave.setFocusPainted(false);

        btnSave.addActionListener(e -> {
            currentProker.setNamaProker(inpNama.getText());
            currentProker.setStatus((String) inpStatus.getSelectedItem());
            currentProker.setKetupel(inpKetupel.getText());
            currentProker.setWaketupel(inpWaketupel.getText());
            currentProker.setDeskripsiDivisi(inpDesc.getText());

            JOptionPane.showMessageDialog(dialog, "Perubahan berhasil disimpan!");
            dialog.dispose();
            populateDetail(); // Refresh detail view
            refreshData();
        });

        formPanel.add(new JLabel("Nama Proker:"));
        formPanel.add(inpNama);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(inpStatus);
        formPanel.add(new JLabel("Ketua Pelaksana:"));
        formPanel.add(inpKetupel);
        formPanel.add(new JLabel("Wakil Pelaksana:"));
        formPanel.add(inpWaketupel);
        formPanel.add(new JLabel("Deskripsi:"));
        formPanel.add(new JScrollPane(inpDesc));
        formPanel.add(btnSave);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Init Component (Kosong, dipanggil di Constructor)
    private void initListView() {
        // Method intentionally left empty because list view initialization is handled
        // in createListViewPanel().
        // If implementation is needed in the future, remove the exception below.
    }

    private void initDetailView() {
        // Method intentionally left empty because detail view initialization is handled
        // in createDetailViewPanel().
        // If implementation is needed in the future, remove the exception below.
    }
}
