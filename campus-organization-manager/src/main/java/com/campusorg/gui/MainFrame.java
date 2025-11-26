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
    private JTable detailTable;
    private DefaultTableModel detailModel;

    // --- HALAMAN 3: ALL MEMBERS ---
    private JTable allTable;
    private DefaultTableModel allModel;
    private JTextField searchField;
    private JComboBox<String> filterDivCombo;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // --- HALAMAN 4: INPUT (Updated) ---
    private JTextField inpName;
    private JComboBox<String> inpDiv;
    private JComboBox<String> inpRole; // Isinya dinamis
    
    // --- CONSTANT ROLES ---
    private final String[] ROLES_BPH = {
        "Ketua Himpunan", 
        "Wakil Ketua Himpunan", 
        "Sekretaris Jendral", 
        "Sekretaris Umum", 
        "Bendahara Umum"
    };
    
    private final String[] ROLES_STD = {
        "Staff Muda", 
        "Staff Ahli", 
        "Ketua Departemen/Biro"
    };

    // --- WARNA TEMA ---
    private final Color COL_SIDEBAR = new Color(33, 47, 60);     // Dark Blue
    private final Color COL_ACTIVE  = new Color(52, 73, 94);     // Lighter Blue
    private final Color COL_BG      = new Color(244, 246, 247);  // Light Gray
    private final Color COL_ACCENT  = new Color(41, 128, 185);   // Blue Accent

    public MainFrame() {
        setTitle("HIMAKOM Information System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. SIDEBAR (Menu Kiri)
        initSidebar();

        // 2. CONTENT AREA (Tengah)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COL_BG);

        // Init Halaman-Halaman
        initHomePanel();        
        initDetailDivPanel();   
        initAllMembersPanel();  
        initInputPanel();       

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(detailDivPanel, "DETAIL");
        contentPanel.add(allMembersPanel, "ALL");
        contentPanel.add(inputPanel, "INPUT");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Load data awal
        refreshHomeData();
    }

    // ================== UI: SIDEBAR ==================
    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(230, 0));
        sidebarPanel.setBackground(COL_SIDEBAR);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // Logo Area
        JPanel logoPnl = new JPanel(new BorderLayout());
        logoPnl.setBackground(COL_SIDEBAR);
        logoPnl.setBorder(new EmptyBorder(30, 20, 30, 20));
        JLabel title = new JLabel("HIMAKOM APP", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoPnl.add(title);
        sidebarPanel.add(logoPnl);

        // Menu Buttons
        addSidebarBtn("🏠  Home Dashboard", "HOME");
        addSidebarBtn("👥  Semua Anggota", "ALL");
        addSidebarBtn("📝  Input / Admin", "INPUT");

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
        btn.setMaximumSize(new Dimension(200, 45));
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
            if(targetCard.equals("ALL")) refreshAllMembersData();
            if(targetCard.equals("HOME")) refreshHomeData();
            cardLayout.show(contentPanel, targetCard);
        });

        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(10));
    }

    // ================== UI: HOME PAGE ==================
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

    // ================== UI: DETAIL DIVISI ==================
    private void initDetailDivPanel() {
        detailDivPanel = new JPanel(new BorderLayout(10, 10));
        detailDivPanel.setBackground(COL_BG);
        detailDivPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_BG);
        
        JButton backBtn = new JButton("⬅ Kembali ke Home");
        backBtn.setBackground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(contentPanel, "HOME"));
        
        detailTitleLabel = new JLabel("Nama Divisi", SwingConstants.CENTER);
        detailTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        detailTitleLabel.setForeground(COL_ACCENT);

        header.add(backBtn, BorderLayout.WEST);
        header.add(detailTitleLabel, BorderLayout.CENTER);

        String[] cols = {"Nama Anggota", "Jabatan / Status"};
        detailModel = new DefaultTableModel(cols, 0);
        detailTable = new JTable(detailModel);
        detailTable.setRowHeight(30);
        detailTable.getTableHeader().setBackground(COL_SIDEBAR);
        detailTable.getTableHeader().setForeground(Color.WHITE);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(detailTable);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        detailDivPanel.add(header, BorderLayout.NORTH);
        detailDivPanel.add(scroll, BorderLayout.CENTER);
    }

    // ================== UI: ALL MEMBERS PAGE ==================
    private JPanel allMembersPanel;
    private void initAllMembersPanel() {
        allMembersPanel = new JPanel(new BorderLayout(10, 10));
        allMembersPanel.setBackground(COL_BG);
        allMembersPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        
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

        JScrollPane scroll = new JScrollPane(allTable);

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
        allMembersPanel.add(scroll, BorderLayout.CENTER);
    }

    // ================== UI: INPUT PAGE (UPDATED LOGIC) ==================
    private JPanel inputPanel;
    private void initInputPanel() {
        inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(COL_BG);
        inputPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JPanel formCard = new JPanel(new GridLayout(0, 1, 10, 5));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        inpName = new JTextField();
        
        // Dropdown Divisi
        inpDiv = new JComboBox<>(OrgManager.getInstance().getDivisionNames());
        
        // Dropdown Role (Kosong dulu, nanti diisi otomatis)
        inpRole = new JComboBox<>();

        // LOGIC DINAMIS: Saat Divisi dipilih, Ubah isi Role
        inpDiv.addActionListener(e -> updateRoleDropdown());

        // Panggil sekali di awal untuk set default state
        updateRoleDropdown();

        JButton btnSave = new JButton("SIMPAN ANGGOTA");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.addActionListener(e -> actionSave());

        formCard.add(new JLabel("Nama Lengkap"));
        formCard.add(inpName);
        formCard.add(new JLabel("Divisi / Penempatan"));
        formCard.add(inpDiv);
        formCard.add(new JLabel("Jabatan / Posisi"));
        formCard.add(inpRole); // Dropdown ini otomatis berubah isi
        
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(btnSave);

        inputPanel.add(new JLabel("<html><h2>Form Input Anggota Baru</h2></html>"), BorderLayout.NORTH);
        inputPanel.add(formCard, BorderLayout.CENTER);
    }

    // Helper untuk update isi dropdown Role berdasarkan Divisi
    private void updateRoleDropdown() {
        String selectedDiv = (String) inpDiv.getSelectedItem();
        inpRole.removeAllItems(); // Hapus isi lama
        
        if ("BPH Inti".equals(selectedDiv)) {
            // Jika BPH, tampilkan jabatan tinggi
            for (String role : ROLES_BPH) inpRole.addItem(role);
        } else {
            // Jika Divisi lain, tampilkan jabatan standar
            for (String role : ROLES_STD) inpRole.addItem(role);
        }
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
            if (divName.equals("BPH Inti") || 
                divName.equals("Divisi Kaderisasi") || 
                divName.equals("Divisi Apresiasi & Evaluasi")) {
                continue; 
            }
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

    // ================== LOGIC: ACTIONS ==================

    private void openDivisionDetail(String divName) {
        detailTitleLabel.setText(divName);
        detailModel.setRowCount(0);

        Division div = OrgManager.getInstance().getDivisionByName(divName);
        
        if (divName.equals("MSDH") && div != null) {
            for (OrgComponent c : div.getMembers()) {
                if (c instanceof Member) {
                    Member m = (Member) c;
                    detailModel.addRow(new Object[]{m.getName(), m.getRole() + " (Pengurus)"});
                } else if (c instanceof Division) {
                    Division subDiv = (Division) c;
                    for (OrgComponent subC : subDiv.getMembers()) {
                        if (subC instanceof Member) {
                            Member m = (Member) subC;
                            detailModel.addRow(new Object[]{m.getName(), m.getRole() + " [" + subDiv.getName() + "]"});
                        }
                    }
                }
            }
        } 
        else if (div != null) {
            for (OrgComponent c : div.getMembers()) {
                if (c instanceof Member) {
                    Member m = (Member) c;
                    detailModel.addRow(new Object[]{m.getName(), m.getRole()});
                }
            }
        }
        
        cardLayout.show(contentPanel, "DETAIL");
    }

    // UPDATE LOGIC SAVE: Menyesuaikan dengan Dropdown Baru
    private void actionSave() {
        String nm = inpName.getText();
        String dv = (String) inpDiv.getSelectedItem();
        String selectedRole = (String) inpRole.getSelectedItem();

        if (nm.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!"); 
            return; 
        }

        // Logic Mapping ke Factory
        String typeForFactory;
        String titleForFactory;

        if (selectedRole.contains("Staff")) {
            // Kalau pilih Staff Muda/Ahli -> Tipe=Staff..., Title=Kosong
            typeForFactory = selectedRole;
            titleForFactory = ""; 
        } else {
            // Kalau pilih Ketua Himpunan, Sekjen, Ketua Dept -> Tipe=Pejabat, Title=PilihanDropdown
            typeForFactory = "Pejabat Struktural";
            titleForFactory = selectedRole; 
        }

        Member newM = MemberFactory.createMember(
            typeForFactory,
            titleForFactory, 
            nm, 
            "ID-"+System.currentTimeMillis()
        );

        OrgManager.getInstance().registerMember(OrgManager.getInstance().getDivisionByName(dv), newM);
        JOptionPane.showMessageDialog(this, "Sukses Input: " + nm + " (" + selectedRole + ")");
        inpName.setText(""); 
    }

    // ================== COMPONENT FACTORIES ==================

    private JPanel createLeaderCard(String name, String role) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel n = new JLabel(name);
        n.setFont(new Font("Segoe UI", Font.BOLD, 14));
        n.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel r = new JLabel(role);
        r.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        r.setForeground(Color.GRAY);
        r.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(n);
        p.add(r);
        return p;
    }

    private JPanel createDivisionCard(String divName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lbl = new JLabel(divName, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(COL_SIDEBAR);

        JLabel icon = new JLabel("📂", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 30));

        p.add(icon, BorderLayout.CENTER);
        p.add(lbl, BorderLayout.SOUTH);

        p.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { p.setBackground(new Color(235, 245, 251)); }
            public void mouseExited(MouseEvent e) { p.setBackground(Color.WHITE); }
            public void mouseClicked(MouseEvent e) { openDivisionDetail(divName); }
        });

        return p;
    }
}