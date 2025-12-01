package com.campusorg.gui;

import com.campusorg.models.*;
import com.campusorg.patterns.MemberFactory;
import com.campusorg.services.OrgManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public class MainFrame extends JFrame {

    // Layout Utama
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebarPanel;

    // --- HALAMAN 1: HOME ---
    private JPanel homePanel;
    private JPanel leadersContainer;
    private JPanel divisionsGrid;

    // --- HALAMAN 2: DETAIL DIVISI ---
    private JPanel detailDivPanel;
    private JLabel detailTitleLabel;
    private JTable detailTable;     // Tabel Anggota
    private DefaultTableModel detailModel;
    private JTable divProkerTable;  // Tabel Proker (Mini)
    private DefaultTableModel divProkerModel;
    private String currentActiveDivision; 

    // --- HALAMAN 3: ALL MEMBERS ---
    private JPanel allMembersPanel;
    private JTable allTable;
    private DefaultTableModel allModel;
    private JTextField searchField;
    private JComboBox<String> filterDivCombo;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // --- HALAMAN 4: INPUT ANGGOTA ---
    private JPanel inputPanel;
    private JTextField inpName;
    private JComboBox<String> inpDiv;
    private JComboBox<String> inpRole; 

    // --- HALAMAN 5: ALL PROKERS (GLOBAL) ---
    private JPanel allProkerPanel;
    private JTable globalProkerTable;
    private DefaultTableModel globalProkerModel;

    // --- HALAMAN 6: DETAIL PROKER (SPECIFIC) ---
    private JPanel prokerDetailPanel;
    private JLabel lblProkerName, lblProkerStatus, lblProkerDiv;
    private JLabel lblKetupel, lblWaketupel;
    private JTextArea txtProkerDivs; // List divisi internal proker

    // --- CONSTANTS & THEME ---
    private final String[] ROLES_BPH = {"Ketua Himpunan", "Wakil Ketua Himpunan", "Sekretaris Jendral", "Sekretaris Umum", "Bendahara Umum"};
    private final String[] ROLES_STD = {"Staff Muda", "Staff Ahli", "Ketua Departemen/Biro"};
    
    private final Color COL_SIDEBAR = new Color(33, 47, 60);
    private final Color COL_ACTIVE  = new Color(52, 73, 94);
    private final Color COL_BG      = new Color(244, 246, 247);
    private final Color COL_ACCENT  = new Color(41, 128, 185);

    public MainFrame() {
        setTitle("HIMAKOM Information System");
        setSize(1280, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Sidebar & Content
        initSidebar();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COL_BG);

        // 2. Init Pages
        initHomePanel();        
        initDetailDivPanel();   
        initAllMembersPanel();  
        initInputPanel();       
        initAllProkerPanel();   // New Page
        initProkerDetailPanel();// New Page

        // 3. Add to CardLayout
        contentPanel.add(homePanel, "HOME");
        contentPanel.add(detailDivPanel, "DIV_DETAIL");
        contentPanel.add(allMembersPanel, "ALL_MEMBERS");
        contentPanel.add(inputPanel, "INPUT_MEMBER");
        contentPanel.add(allProkerPanel, "ALL_PROKER");
        contentPanel.add(prokerDetailPanel, "PROKER_DETAIL");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        refreshHomeData();
    }

    // ================== UI: SIDEBAR ==================
    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBackground(COL_SIDEBAR);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // Logo
        JPanel logoPnl = new JPanel();
        logoPnl.setLayout(new BoxLayout(logoPnl, BoxLayout.Y_AXIS));
        logoPnl.setBackground(COL_SIDEBAR);
        logoPnl.setBorder(new EmptyBorder(30, 10, 20, 10));

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        ImageIcon logoIcon = loadIcon("logo_himakom.png", 80, 80);
        if (logoIcon != null) imageLabel.setIcon(logoIcon);
        else imageLabel.setText("<html><h2 style='color:white;'>[LOGO]</h2></html>");

        JLabel title = new JLabel("HIMAKOM APP");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        title.setAlignmentX(Component.CENTER_ALIGNMENT); 

        logoPnl.add(imageLabel);
        logoPnl.add(Box.createVerticalStrut(15)); 
        logoPnl.add(title);
        sidebarPanel.add(logoPnl);

        // Menu
        addSidebarBtn("🏠  Home Dashboard", "HOME");
        addSidebarBtn("👥  Data Anggota", "ALL_MEMBERS");
        addSidebarBtn("📅  Program Kerja", "ALL_PROKER"); // NEW MENU
        addSidebarBtn("📝  Input Anggota", "INPUT_MEMBER");

        sidebarPanel.add(Box.createVerticalGlue());
        JLabel credit = new JLabel("© 2024 Kelompok 2C", SwingConstants.CENTER);
        credit.setForeground(Color.GRAY);
        credit.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(credit);
        sidebarPanel.add(Box.createVerticalStrut(20));
    }

    private void addSidebarBtn(String text, String targetCard) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setBackground(COL_SIDEBAR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COL_ACTIVE); }
            public void mouseExited(MouseEvent e) { btn.setBackground(COL_SIDEBAR); }
        });

        btn.addActionListener(e -> {
            if(targetCard.equals("ALL_MEMBERS")) refreshAllMembersData();
            if(targetCard.equals("HOME")) refreshHomeData();
            if(targetCard.equals("ALL_PROKER")) refreshGlobalProkerData(); // Auto refresh
            cardLayout.show(contentPanel, targetCard);
        });

        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(10));
    }

    // ================== UI: 1. HOME PAGE ==================
    private void initHomePanel() {
        homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(COL_BG);
        
        JPanel mainScrollContent = new JPanel();
        mainScrollContent.setLayout(new BoxLayout(mainScrollContent, BoxLayout.Y_AXIS));
        mainScrollContent.setBackground(COL_BG);
        mainScrollContent.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblLead = new JLabel("Pimpinan Teras (BPH Inti)");
        lblLead.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLead.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leadersContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leadersContainer.setBackground(COL_BG);
        leadersContainer.setMaximumSize(new Dimension(2000, 120));

        JLabel lblDiv = new JLabel("Daftar Departemen & Biro");
        lblDiv.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDiv.setAlignmentX(Component.LEFT_ALIGNMENT);

        divisionsGrid = new JPanel(new GridLayout(0, 3, 15, 15)); 
        divisionsGrid.setBackground(COL_BG);

        mainScrollContent.add(lblLead);
        mainScrollContent.add(leadersContainer);
        mainScrollContent.add(Box.createVerticalStrut(20));
        mainScrollContent.add(new JSeparator());
        mainScrollContent.add(Box.createVerticalStrut(20));
        mainScrollContent.add(lblDiv);
        mainScrollContent.add(Box.createVerticalStrut(15));
        mainScrollContent.add(divisionsGrid);

        JScrollPane scroll = new JScrollPane(mainScrollContent);
        scroll.setBorder(null);
        homePanel.add(scroll, BorderLayout.CENTER);
    }

    // ================== UI: 2. DETAIL DIVISI (With Proker) ==================
    private void initDetailDivPanel() {
        detailDivPanel = new JPanel(new BorderLayout(10, 10));
        detailDivPanel.setBackground(COL_BG);
        detailDivPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_BG);
        JButton backBtn = new JButton("⬅ Kembali ke Home");
        backBtn.setBackground(Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(contentPanel, "HOME"));
        
        detailTitleLabel = new JLabel("Nama Divisi", SwingConstants.CENTER);
        detailTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        detailTitleLabel.setForeground(COL_ACCENT);

        header.add(backBtn, BorderLayout.WEST);
        header.add(detailTitleLabel, BorderLayout.CENTER);
        detailDivPanel.add(header, BorderLayout.NORTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(5);

        // Kiri: Anggota
        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Daftar Anggota"));
        memberPanel.setBackground(Color.WHITE);
        String[] cols = {"Nama Anggota", "Jabatan"};
        detailModel = new DefaultTableModel(cols, 0);
        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(25);
        memberPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // Kanan: Proker
        JPanel prokerPanel = new JPanel(new BorderLayout(5, 5));
        prokerPanel.setBorder(BorderFactory.createTitledBorder("Program Kerja (Klik untuk Detail)"));
        prokerPanel.setBackground(Color.WHITE);

        String[] pCols = {"Nama Proker", "Status"};
        divProkerModel = new DefaultTableModel(pCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        divProkerTable = new JTable(divProkerModel);
        divProkerTable.setRowHeight(25);
        
        // Listener Klik Tabel Proker -> Buka Detail
        divProkerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    openProkerDetailFromTable(divProkerTable, divProkerModel);
                }
            }
        });

        JButton btnAddProker = new JButton("➕ Tambah Proker Baru");
        btnAddProker.setBackground(new Color(46, 204, 113));
        btnAddProker.setForeground(Color.WHITE);
        btnAddProker.addActionListener(e -> showAddProkerDialog());

        prokerPanel.add(new JScrollPane(divProkerTable), BorderLayout.CENTER);
        prokerPanel.add(btnAddProker, BorderLayout.SOUTH);

        splitPane.setLeftComponent(memberPanel);
        splitPane.setRightComponent(prokerPanel);
        detailDivPanel.add(splitPane, BorderLayout.CENTER);
    }

    // ================== UI: 3. ALL MEMBERS PAGE ==================
    private void initAllMembersPanel() {
        allMembersPanel = new JPanel(new BorderLayout(10, 10));
        allMembersPanel.setBackground(COL_BG);
        allMembersPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel lblCari = new JLabel("🔍 Cari Nama:");
        searchField = new JTextField(15);
        JLabel lblFilter = new JLabel("   📂 Filter Divisi:");
        
        String[] divs = OrgManager.getInstance().getDivisionNames();
        List<String> list = new ArrayList<>();
        list.add("Semua Divisi");
        for(String s : divs) list.add(s);
        filterDivCombo = new JComboBox<>(list.toArray(new String[0]));

        JButton btnFilter = new JButton("Terapkan");
        btnFilter.setBackground(COL_ACCENT);
        btnFilter.setForeground(Color.WHITE);

        filterPanel.add(lblCari);
        filterPanel.add(searchField);
        filterPanel.add(lblFilter);
        filterPanel.add(filterDivCombo);
        filterPanel.add(btnFilter);

        String[] cols = {"Nama Lengkap", "Divisi", "Jabatan"};
        allModel = new DefaultTableModel(cols, 0);
        allTable = new JTable(allModel);
        allTable.setRowHeight(25);
        rowSorter = new TableRowSorter<>(allModel);
        allTable.setRowSorter(rowSorter);

        btnFilter.addActionListener(e -> {
            String text = searchField.getText();
            String div = (String) filterDivCombo.getSelectedItem();
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            if (!text.isEmpty()) filters.add(RowFilter.regexFilter("(?i)" + text, 0));
            if (!div.equals("Semua Divisi")) filters.add(RowFilter.regexFilter("(?i)" + div, 1));
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        });

        allMembersPanel.add(new JLabel("Data Seluruh Anggota HIMAKOM"), BorderLayout.NORTH);
        allMembersPanel.add(filterPanel, BorderLayout.NORTH);
        allMembersPanel.add(new JScrollPane(allTable), BorderLayout.CENTER);
    }

    // ================== UI: 4. INPUT PAGE ==================
    private void initInputPanel() {
        inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(COL_BG);
        inputPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JPanel formCard = new JPanel(new GridLayout(0, 1, 10, 5));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(20, 20, 20, 20)));
        
        inpName = new JTextField();
        inpDiv = new JComboBox<>(OrgManager.getInstance().getDivisionNames());
        inpRole = new JComboBox<>();

        inpDiv.addActionListener(e -> {
            String selectedDiv = (String) inpDiv.getSelectedItem();
            inpRole.removeAllItems();
            if ("BPH Inti".equals(selectedDiv)) for (String r : ROLES_BPH) inpRole.addItem(r);
            else for (String r : ROLES_STD) inpRole.addItem(r);
        });
        // Trigger first load
        if(inpDiv.getItemCount() > 0) inpDiv.setSelectedIndex(0);

        JButton btnSave = new JButton("SIMPAN ANGGOTA");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.addActionListener(e -> actionSaveMember());

        formCard.add(new JLabel("Nama Lengkap"));
        formCard.add(inpName);
        formCard.add(new JLabel("Divisi / Penempatan"));
        formCard.add(inpDiv);
        formCard.add(new JLabel("Jabatan / Posisi"));
        formCard.add(inpRole);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(btnSave);

        inputPanel.add(new JLabel("<html><h2>Form Input Anggota Baru</h2></html>"), BorderLayout.NORTH);
        inputPanel.add(formCard, BorderLayout.CENTER);
    }

    // ================== UI: 5. GLOBAL PROKER PAGE (NEW) ==================
    private void initAllProkerPanel() {
        allProkerPanel = new JPanel(new BorderLayout(10, 10));
        allProkerPanel.setBackground(COL_BG);
        allProkerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Daftar Program Kerja Seluruh HIMAKOM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        String[] cols = {"Nama Proker", "Status", "Divisi Penanggung Jawab", "Ketua Pelaksana"};
        globalProkerModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        globalProkerTable = new JTable(globalProkerModel);
        globalProkerTable.setRowHeight(30);
        globalProkerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Listener Klik
        globalProkerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openProkerDetailFromTable(globalProkerTable, globalProkerModel);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(globalProkerTable);
        
        // Hint label
        JLabel hint = new JLabel("ℹ️ Klik 2x pada baris untuk melihat detail proker & mengubah status.");
        hint.setForeground(Color.GRAY);

        allProkerPanel.add(title, BorderLayout.NORTH);
        allProkerPanel.add(scroll, BorderLayout.CENTER);
        allProkerPanel.add(hint, BorderLayout.SOUTH);
    }

    // ================== UI: 6. DETAIL PROKER PAGE (NEW) ==================
    private void initProkerDetailPanel() {
        prokerDetailPanel = new JPanel(new BorderLayout());
        prokerDetailPanel.setBackground(COL_BG);
        prokerDetailPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Header with Back Button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(COL_BG);
        JButton btnBack = new JButton("⬅ Kembali");
        btnBack.addActionListener(e -> cardLayout.show(contentPanel, "ALL_PROKER")); // Default back to global
        topBar.add(btnBack);

        // Content Card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(30, 30, 30, 30)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components
        lblProkerName = new JLabel("Nama Proker");
        lblProkerName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblProkerName.setForeground(COL_ACCENT);
        
        lblProkerStatus = new JLabel("Status: Rencana");
        lblProkerStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProkerStatus.setOpaque(true);
        lblProkerStatus.setBackground(Color.ORANGE);
        lblProkerStatus.setBorder(new EmptyBorder(5, 10, 5, 10));

        lblProkerDiv = new JLabel("Divisi: -");
        lblKetupel = new JLabel("Ketupel: -");
        lblWaketupel = new JLabel("Waketupel: -");
        
        txtProkerDivs = new JTextArea(5, 30);
        txtProkerDivs.setEditable(false);
        txtProkerDivs.setBorder(BorderFactory.createTitledBorder("Divisi/Seksi Internal Proker"));
        
        JButton btnChangeStatus = new JButton("Ubah Status Proker");
        btnChangeStatus.addActionListener(e -> actionChangeStatus());

        // Layouting
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; card.add(lblProkerName, gbc);
        gbc.gridy=1; card.add(lblProkerStatus, gbc);
        gbc.gridy=2; card.add(new JSeparator(), gbc);
        
        gbc.gridy=3; gbc.gridwidth=1; card.add(lblProkerDiv, gbc);
        gbc.gridx=1; card.add(new JLabel(""), gbc); // Spacer
        
        gbc.gridx=0; gbc.gridy=4; card.add(lblKetupel, gbc);
        gbc.gridx=1; card.add(lblWaketupel, gbc);
        
        gbc.gridx=0; gbc.gridy=5; gbc.gridwidth=2; 
        card.add(new JScrollPane(txtProkerDivs), gbc);
        
        gbc.gridy=6; 
        card.add(btnChangeStatus, gbc);

        prokerDetailPanel.add(topBar, BorderLayout.NORTH);
        prokerDetailPanel.add(card, BorderLayout.CENTER);
    }

    // ================== LOGIC: DATA LOADING ==================

    private void refreshHomeData() {
        leadersContainer.removeAll();
        Division bph = OrgManager.getInstance().getDivisionByName("BPH Inti");
        if (bph != null && !bph.getMembers().isEmpty()) {
            for (OrgComponent comp : bph.getMembers()) {
                if (comp instanceof Member) {
                    Member m = (Member) comp;
                    leadersContainer.add(createLeaderCard(m.getName(), m.getRole()));
                }
            }
        } else {
            leadersContainer.add(new JLabel("(Belum ada data Pimpinan Inti)"));
        }

        divisionsGrid.removeAll();
        String[] allDivs = OrgManager.getInstance().getDivisionNames();
        for (String divName : allDivs) {
            if (divName.equals("BPH Inti") || divName.equals("Divisi Kaderisasi") || divName.equals("Divisi Apresiasi & Evaluasi")) continue; 
            divisionsGrid.add(createDivisionCard(divName));
        }
        leadersContainer.revalidate(); leadersContainer.repaint();
        divisionsGrid.revalidate(); divisionsGrid.repaint();
    }

    private void refreshAllMembersData() {
        allModel.setRowCount(0);
        Division root = OrgManager.getInstance().getRoot();
        traverseForAll(root);
    }

    private void traverseForAll(OrgComponent comp) {
        if (comp instanceof Division) {
            Division d = (Division) comp;
            for (OrgComponent c : d.getMembers()) {
                if (c instanceof Member) {
                    Member m = (Member) c;
                    allModel.addRow(new Object[]{m.getName(), d.getName(), m.getRole()});
                } else {
                    traverseForAll(c);
                }
            }
        }
    }

    private void refreshGlobalProkerData() {
        globalProkerModel.setRowCount(0);
        String[] divNames = OrgManager.getInstance().getDivisionNames();
        for (String dName : divNames) {
            Division div = OrgManager.getInstance().getDivisionByName(dName);
            if(div != null) {
                for(Proker p : div.getProkerList()) {
                    globalProkerModel.addRow(new Object[]{
                        p.getNamaProker(), p.getStatus(), dName, p.getKetupel()
                    });
                }
            }
        }
    }

    // ================== LOGIC: ACTIONS ==================

    private void openDivisionDetail(String divName) {
        currentActiveDivision = divName; 
        detailTitleLabel.setText(divName);
        detailModel.setRowCount(0);
        divProkerModel.setRowCount(0); 

        Division div = OrgManager.getInstance().getDivisionByName(divName);
        
        // Load Members (Recursive for MSDH)
        if (divName.equals("MSDH") && div != null) {
            loadRecursiveMember(div, "");
        } else if (div != null) {
            for (OrgComponent c : div.getMembers()) {
                if (c instanceof Member) {
                    Member m = (Member) c;
                    detailModel.addRow(new Object[]{m.getName(), m.getRole()});
                }
            }
        }

        // Load Prokers
        if (div != null) {
            for (Proker p : div.getProkerList()) {
                divProkerModel.addRow(new Object[]{p.getNamaProker(), p.getStatus()});
            }
        }
        
        cardLayout.show(contentPanel, "DIV_DETAIL");
    }

    private void loadRecursiveMember(Division d, String suffix) {
        for (OrgComponent c : d.getMembers()) {
            if (c instanceof Member) {
                Member m = (Member) c;
                detailModel.addRow(new Object[]{m.getName(), m.getRole() + suffix});
            } else if (c instanceof Division) {
                loadRecursiveMember((Division) c, " [" + c.getName() + "]");
            }
        }
    }

    // --- LOGIC ADD PROKER (DIALOG) ---
    private void showAddProkerDialog() {
        if(currentActiveDivision == null) return;

        JTextField txtName = new JTextField();
        JTextField txtKetupel = new JTextField();
        JTextField txtWaketupel = new JTextField();
        JTextArea txtDivisi = new JTextArea(3, 20);
        txtDivisi.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        Object[] msg = {
            "Nama Proker:", txtName,
            "Ketua Pelaksana:", txtKetupel,
            "Wakil Ketupel:", txtWaketupel,
            "Divisi/Seksi Internal (Pisahkan koma):", new JScrollPane(txtDivisi)
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Tambah Proker di " + currentActiveDivision, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            if(txtName.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Nama Proker Wajib!"); return; }
            
            Proker newP = new Proker(
                txtName.getText(), currentActiveDivision, 
                txtKetupel.getText(), txtWaketupel.getText(), txtDivisi.getText()
            );
            
            OrgManager.getInstance().getDivisionByName(currentActiveDivision).addProker(newP);
            // Refresh tabel di halaman detail divisi
            divProkerModel.addRow(new Object[]{newP.getNamaProker(), newP.getStatus()});
        }
    }

    // --- LOGIC OPEN PROKER DETAIL ---
    private void openProkerDetailFromTable(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        // Kalau tabel di-sort, konversi index row view ke model
        int modelRow = table.convertRowIndexToModel(row); 
        
        String pName = (String) model.getValueAt(modelRow, 0); // Nama Proker
        
        // Cari objek proker
        Proker target = null;
        String[] divs = OrgManager.getInstance().getDivisionNames();
        for(String d : divs) {
            Division div = OrgManager.getInstance().getDivisionByName(d);
            for(Proker p : div.getProkerList()) {
                if(p.getNamaProker().equals(pName)) { target = p; break; }
            }
            if(target != null) break;
        }

        if(target != null) {
            lblProkerName.setText(target.getNamaProker());
            lblProkerStatus.setText("Status: " + target.getStatus());
            // Warna status
            if(target.getStatus().equals("Selesai")) lblProkerStatus.setBackground(new Color(46, 204, 113));
            else if(target.getStatus().equals("Berjalan")) lblProkerStatus.setBackground(Color.CYAN);
            else lblProkerStatus.setBackground(Color.ORANGE);

            lblProkerDiv.setText("Divisi Penanggung Jawab: " + target.getParentDivisi());
            lblKetupel.setText("Ketua Pelaksana: " + target.getKetupel());
            lblWaketupel.setText("Wakil Ketupel: " + target.getWaketupel());
            txtProkerDivs.setText(target.getDeskripsiDivisi());
            
            cardLayout.show(contentPanel, "PROKER_DETAIL");
        }
    }

    private void actionChangeStatus() {
        String currentName = lblProkerName.getText();
        // Cari lagi objeknya (agak repetitif tapi aman)
        Proker target = null;
        String[] divs = OrgManager.getInstance().getDivisionNames();
        for(String d : divs) {
            Division div = OrgManager.getInstance().getDivisionByName(d);
            for(Proker p : div.getProkerList()) {
                if(p.getNamaProker().equals(currentName)) { target = p; break; }
            }
        }

        if(target != null) {
            String[] opts = {"Rencana", "Berjalan", "Selesai"};
            String newStat = (String) JOptionPane.showInputDialog(this, "Pilih Status:", "Update Status", 
                JOptionPane.QUESTION_MESSAGE, null, opts, target.getStatus());
            
            if(newStat != null) {
                target.setStatus(newStat);
                lblProkerStatus.setText("Status: " + newStat);
                // Update warna label langsung
                if(newStat.equals("Selesai")) lblProkerStatus.setBackground(new Color(46, 204, 113));
                else if(newStat.equals("Berjalan")) lblProkerStatus.setBackground(Color.CYAN);
                else lblProkerStatus.setBackground(Color.ORANGE);
            }
        }
    }

    private void actionSaveMember() {
        String nm = inpName.getText();
        String dv = (String) inpDiv.getSelectedItem();
        String selectedRole = (String) inpRole.getSelectedItem();

        if (nm.isEmpty()) { JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!"); return; }

        String typeForFactory = selectedRole.contains("Staff") ? selectedRole : "Pejabat Struktural";
        String titleForFactory = selectedRole.contains("Staff") ? "" : selectedRole;

        Member newM = MemberFactory.createMember(typeForFactory, titleForFactory, nm, "ID-"+System.currentTimeMillis());
        OrgManager.getInstance().registerMember(OrgManager.getInstance().getDivisionByName(dv), newM);
        JOptionPane.showMessageDialog(this, "Sukses Input: " + nm);
        inpName.setText(""); 
    }

    // ================== FACTORIES & HELPERS ==================
    private JPanel createLeaderCard(String name, String role) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1), new EmptyBorder(10, 15, 10, 15)));
        JLabel n = new JLabel(name); n.setFont(new Font("Segoe UI", Font.BOLD, 14)); n.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel r = new JLabel(role); r.setFont(new Font("Segoe UI", Font.PLAIN, 12)); r.setForeground(Color.GRAY); r.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(n); p.add(r); return p;
    }

    private JPanel createDivisionCard(String divName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1), new EmptyBorder(15, 15, 15, 15)));
        JLabel lbl = new JLabel(divName, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(COL_SIDEBAR);
        
        JLabel iconLabel = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = loadIcon(getIconFilename(divName), 50, 50);
        if (icon != null) iconLabel.setIcon(icon);
        else { iconLabel.setText("📂"); iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30)); }

        p.add(iconLabel, BorderLayout.CENTER);
        p.add(lbl, BorderLayout.SOUTH);
        p.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { p.setBackground(new Color(235, 245, 251)); }
            public void mouseExited(MouseEvent e) { p.setBackground(Color.WHITE); }
            public void mouseClicked(MouseEvent e) { openDivisionDetail(divName); }
        });
        return p;
    }

    private String getIconFilename(String divName) {
        return divName.toLowerCase().replace(".", "").replace(" ", "_") + ".png";
    }

    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            URL imgURL = getClass().getResource("/images/" + filename);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            return null;
        } catch (Exception e) { return null; }
    }
}