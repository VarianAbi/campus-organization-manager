package com.campusorg.gui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.campusorg.models.Proker;
import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OrgComponent;
import com.campusorg.patterns.factory.MemberFactory;
import com.campusorg.patterns.singleton.OrgManager;

public class HomePanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    // --- DASHBOARD COMPONENTS ---
    private JPanel dashboardScrollContent;
    private JPanel leadersContainer;
    private JPanel divisionsGrid;

    // --- DETAIL VIEW COMPONENTS ---
    private JPanel detailDivPanel;
    private JLabel detailTitleLabel;
    
    // Member Table
    private JTable memberTable;
    private DefaultTableModel memberModel;
    
    // Proker Table
    private JTable prokerTable;
    private DefaultTableModel prokerModel;
    private JTextField inpProkerName; // Quick Add
    
    // State
    private String currentActiveDivision;
    private static final Color COL_SIDEBAR = new Color(33, 47, 60);
    private static final Color COL_BG = new Color(244, 246, 247);
    private static final Color COL_ACCENT = new Color(41, 128, 185);

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(COL_BG);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        initDashboardUI();
        initDetailUI();

        mainContainer.add(new JScrollPane(dashboardScrollContent), "DASHBOARD");
        mainContainer.add(detailDivPanel, "DETAIL");

        add(mainContainer, BorderLayout.CENTER);
        
        refreshData();
    }

    // ================== DASHBOARD VIEW ==================
    private void initDashboardUI() {
        dashboardScrollContent = new JPanel();
        dashboardScrollContent.setLayout(new BoxLayout(dashboardScrollContent, BoxLayout.Y_AXIS));
        dashboardScrollContent.setBackground(COL_BG);
        dashboardScrollContent.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblLead = new JLabel("Struktur Organisasi");
        lblLead.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLead.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leadersContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leadersContainer.setBackground(COL_BG);
        leadersContainer.setMaximumSize(new Dimension(2000, 120));

        divisionsGrid = new JPanel(new GridLayout(0, 3, 15, 15)); 
        divisionsGrid.setBackground(COL_BG);

        dashboardScrollContent.add(lblLead);
        dashboardScrollContent.add(leadersContainer);
        dashboardScrollContent.add(Box.createVerticalStrut(20));
        dashboardScrollContent.add(new JSeparator());
        dashboardScrollContent.add(Box.createVerticalStrut(20));
        dashboardScrollContent.add(Box.createVerticalStrut(15));
        dashboardScrollContent.add(divisionsGrid);
    }

    // ================== DETAIL VIEW ==================
    private void initDetailUI() {
        detailDivPanel = new JPanel(new BorderLayout(10, 10));
        detailDivPanel.setBackground(COL_BG);
        detailDivPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COL_BG);
        JButton backBtn = new JButton("⬅ Kembali ke Dashboard");
        backBtn.setBackground(Color.WHITE);
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "DASHBOARD"));
        
        detailTitleLabel = new JLabel("Nama Divisi", SwingConstants.CENTER);
        detailTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        detailTitleLabel.setForeground(COL_ACCENT);

        header.add(backBtn, BorderLayout.WEST);
        header.add(detailTitleLabel, BorderLayout.CENTER);
        detailDivPanel.add(header, BorderLayout.NORTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.65); // Kiri lebih lebar

        splitPane.setLeftComponent(createMemberPanel());
        splitPane.setRightComponent(createProkerPanel());
        detailDivPanel.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createMemberPanel() {
        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Daftar Anggota & Keuangan"));
        memberPanel.setBackground(Color.WHITE);

        String[] mCols = {"Nama", "Jabatan", "Perpanjangan", "Uang Kas"};
        memberModel = new DefaultTableModel(mCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        memberTable = new JTable(memberModel);
        memberTable.setRowHeight(30);

        memberTable.setDefaultRenderer(Object.class, getMemberTableCellRenderer());

        JPanel memberActionPnl = createMemberActionPanel();

        memberPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        memberPanel.add(memberActionPnl, BorderLayout.SOUTH);

        return memberPanel;
    }

    // Helper method for table cell renderer
    private DefaultTableCellRenderer getMemberTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (row < 0 || row >= table.getRowCount()) {
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setCellForeground(c, table, row, column);

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
    }

    // Extracted helper for foreground color logic
    private void setCellForeground(Component c, JTable table, int row, int column) {
        try {
            if (column == 2) {
                String perpanjangan = (String) table.getValueAt(row, 2);
                c.setForeground(getPerpanjanganColor(perpanjangan));
            } else {
                c.setForeground(Color.BLACK);
            }
        } catch (Exception _) {
            c.setForeground(Color.BLACK);
        }
    }

    private Color getPerpanjanganColor(String perpanjangan) {
        if (perpanjangan != null && perpanjangan.contains("ADKES")) {
            return new Color(41, 128, 185);
        } else if (perpanjangan != null && perpanjangan.contains("KESRA")) {
            return new Color(230, 126, 34);
        } else {
            return Color.LIGHT_GRAY;
        }
    }

    // Helper method for member action panel
    private JPanel createMemberActionPanel() {
        JPanel memberActionPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        memberActionPnl.setBackground(Color.WHITE);
        JButton btnBayarKas = new JButton("💰 Bayar Kas");
        JButton btnEditMember = new JButton("✏️ Edit Anggota");
        
        btnBayarKas.setBackground(new Color(46, 204, 113)); btnBayarKas.setForeground(Color.BLACK);
        btnEditMember.setBackground(new Color(241, 196, 15)); btnEditMember.setForeground(Color.BLACK);

        btnBayarKas.addActionListener(e -> actionPayKas());
        btnEditMember.addActionListener(e -> actionEditMember()); 

        memberActionPnl.add(btnEditMember);
        memberActionPnl.add(btnBayarKas);

        return memberActionPnl;
    }

    private JPanel createProkerPanel() {
        JPanel prokerPanel = new JPanel(new BorderLayout(5, 5));
        prokerPanel.setBorder(BorderFactory.createTitledBorder("Program Kerja (Klik 2x utk Detail)"));
        prokerPanel.setBackground(Color.WHITE);

        String[] pCols = {"Nama Proker", "Status"};
        prokerModel = new DefaultTableModel(pCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        prokerTable = new JTable(prokerModel);
        prokerTable.setRowHeight(25);

        prokerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) actionEditProker();
            }
        });

        JPanel inputProkerPnl = new JPanel(new BorderLayout());
        inputProkerPnl.setOpaque(false);
        inpProkerName = new JTextField();
        JButton btnAddProker = new JButton("Add");
        btnAddProker.addActionListener(e -> actionAddProker());

        inputProkerPnl.add(new JLabel(" Nama: "), BorderLayout.WEST);
        inputProkerPnl.add(inpProkerName, BorderLayout.CENTER);
        inputProkerPnl.add(btnAddProker, BorderLayout.EAST);

        JButton btnDetailProker = new JButton("Lihat Detail / Edit Proker");
        btnDetailProker.addActionListener(e -> actionEditProker());

        prokerPanel.add(new JScrollPane(prokerTable), BorderLayout.CENTER);
        prokerPanel.add(inputProkerPnl, BorderLayout.NORTH);
        prokerPanel.add(btnDetailProker, BorderLayout.SOUTH);

        return prokerPanel;
    }

    // ================== LOGIC: DATA & ACTIONS ==================

    public final void refreshData() {
        leadersContainer.removeAll();
        Division bph = OrgManager.getInstance().getDivisionByName("BPH Inti");
        if (bph != null && !bph.getMembers().isEmpty()) {
            for (OrgComponent comp : bph.getMembers()) {
                if (comp instanceof Member m) {
                    leadersContainer.add(createLeaderCard(m.getName(), m.getRole()));
                }
            }
        } else leadersContainer.add(new JLabel("(Belum ada data)"));

        divisionsGrid.removeAll();
        for (String divName : OrgManager.getInstance().getDivisionNames()) {
            if (divName.equals("BPH Inti") || divName.equals("Divisi Kaderisasi") || divName.equals("Divisi Apresiasi & Evaluasi")) continue; 
            divisionsGrid.add(createDivisionCard(divName));
        }
        leadersContainer.revalidate(); leadersContainer.repaint();
        divisionsGrid.revalidate(); divisionsGrid.repaint();
    }

    private void openDivisionDetail(String divName) {
        currentActiveDivision = divName;
        detailTitleLabel.setText(divName);
        
        // LOGIC KOLOM: Kalau Biro ada kolom perpanjangan, kalau Divisi Biasa tidak ada
        boolean isBiro = divName.contains("Biro");
        if (isBiro) {
            String[] cols = {"Nama", "Jabatan", "Perpanjangan Ke", "Uang Kas"};
            memberModel.setColumnIdentifiers(cols);
        } else {
            String[] cols = {"Nama", "Jabatan", "Uang Kas"};
            memberModel.setColumnIdentifiers(cols);
        }

        memberModel.setRowCount(0);
        prokerModel.setRowCount(0);

        Division div = OrgManager.getInstance().getDivisionByName(divName);
        if (div != null) {
            // Load Members
            loadRecursiveMember(div, isBiro);
            
            // Jika BUKAN Biro, cari orang delegasi yang masuk
            if (!isBiro) {
                findAndAddIncomingDelegations(divName);
            }

            // Load Prokers
            for (Proker p : div.getProkerList()) {
                prokerModel.addRow(new Object[]{p.getNamaProker(), p.getStatus()});
            }
        }
        cardLayout.show(mainContainer, "DETAIL");
    }

    private void loadRecursiveMember(Division d, boolean showPerpanjanganColumn) {
        for (OrgComponent c : d.getMembers()) {
            switch (c) {
                case Member m -> {
                    String kasText = "Rp " + NumberFormat.getNumberInstance(Locale.GERMANY).format(m.getUangKas());
                    
                    if (showPerpanjanganColumn) {
                        memberModel.addRow(new Object[]{m.getName(), m.getRole(), m.getPerpanjangan(), kasText});
                    } else {
                        memberModel.addRow(new Object[]{m.getName(), m.getRole(), kasText});
                    }
                }
                default -> {
                    if (c instanceof Division division) {
                        loadRecursiveMember(division, showPerpanjanganColumn);
                    }
                    // Do nothing
                }
            }
        }
    }

    // Logic Delegasi: Cari anak biro yang ditugaskan ke divisi ini
    private void findAndAddIncomingDelegations(String targetDivName) {
        String[] sourceBiros = {"Biro ADKES", "Biro KESRA"};
        for (String biroName : sourceBiros) {
            Division biro = OrgManager.getInstance().getDivisionByName(biroName);
            if (biro != null) {
                addDelegatedMembersFromBiro(biro, biroName, targetDivName);
            }
        }
    }

    private void addDelegatedMembersFromBiro(Division biro, String biroName, String targetDivName) {
        for (OrgComponent c : biro.getMembers()) {
            if (c instanceof Member m && isDelegatedToDivision(m, targetDivName)) {
                String roleDisplay = getDelegationRoleDisplay(biroName);
                String kasText = formatKas(m.getUangKas());
                memberModel.addRow(new Object[]{m.getName(), roleDisplay, kasText});
            }
        }
    }

    private boolean isDelegatedToDivision(Member m, String targetDivName) {
        return m.getPerpanjangan().equals(targetDivName);
    }

    private String getDelegationRoleDisplay(String biroName) {
        if (biroName.contains("ADKES")) {
            return "Sekretaris (Delegasi ADKES)";
        } else {
            return "Bendahara (Delegasi KESRA)";
        }
    }

    private String formatKas(int uangKas) {
        return "Rp " + NumberFormat.getNumberInstance(Locale.GERMANY).format(uangKas);
    }

    // --- FITUR EDIT ANGGOTA (UPDATED DENGAN GANTI ROLE) ---
    private void actionEditMember() {
        int row = memberTable.getSelectedRow();
        if (row == -1) {
            showSelectMemberDialog();
            return;
        }

        String name = (String) memberModel.getValueAt(row, 0);
        String roleDisplay = (String) memberModel.getValueAt(row, 1);

        if (isDelegasi(roleDisplay)) {
            showDelegasiInfoDialog();
            return;
        }

        Division div = OrgManager.getInstance().getDivisionByName(currentActiveDivision);
        Member target = findMemberRecursive(div, name);

        if (target != null) {
            JTextField txtName = new JTextField(target.getName());
            JComboBox<String> cmbRole = createRoleComboBox(target.getRole());
            boolean isBiro = currentActiveDivision.contains("Biro");
            JComponent perpanjanganInput = createPerpanjanganInput(isBiro, target.getPerpanjangan());

            Object[] message = {
                "Nama Anggota:", txtName,
                "Jabatan:", cmbRole,
                "Perpanjangan Ke:", perpanjanganInput
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Anggota", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                handleEditMemberOk(div, target, txtName, cmbRole, isBiro, perpanjanganInput);
            }
        }
    }

    private void showSelectMemberDialog() {
        JOptionPane.showMessageDialog(this, "Pilih anggota dulu!");
    }

    private boolean isDelegasi(String roleDisplay) {
        return roleDisplay.contains("Delegasi");
    }

    private void showDelegasiInfoDialog() {
        JOptionPane.showMessageDialog(this, "Ini anggota delegasi. Edit di Biro asalnya.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private JComboBox<String> createRoleComboBox(String selectedRole) {
        JComboBox<String> cmbRole = new JComboBox<>();
        if (currentActiveDivision.equals("BPH Inti")) {
            String[] roles = {"Ketua Himpunan", "Wakil Ketua Himpunan", "Sekretaris Jendral", "Sekretaris Umum", "Bendahara Umum"};
            for (String r : roles) cmbRole.addItem(r);
        } else {
            String[] roles = {"Staff Muda", "Staff Ahli", "Ketua Departemen/Biro"};
            for (String r : roles) cmbRole.addItem(r);
        }
        cmbRole.setSelectedItem(selectedRole);
        return cmbRole;
    }

    private JComponent createPerpanjanganInput(boolean isBiro, String selectedPerpanjangan) {
        if (isBiro) {
            JComboBox<String> cmbPerpanjangan = new JComboBox<>();
            cmbPerpanjangan.addItem("-");
            for (String dName : OrgManager.getInstance().getDivisionNames()) {
                if (!dName.equals(currentActiveDivision) && !dName.equals("BPH Inti") && !dName.equals("MSDH")) {
                    cmbPerpanjangan.addItem(dName);
                }
            }
            cmbPerpanjangan.setSelectedItem(selectedPerpanjangan);
            return cmbPerpanjangan;
        } else {
            return new JLabel("(Tidak tersedia)");
        }
    }

    private void handleEditMemberOk(Division div, Member target, JTextField txtName, JComboBox<String> cmbRole, boolean isBiro, JComponent perpanjanganInput) {
        target.setName(txtName.getText());
        String newRole = (String) cmbRole.getSelectedItem();
        if (!newRole.equals(target.getRole())) {
            target = recreateMemberWithNewRole(div, target, txtName.getText(), newRole);
        }
        if (isBiro && perpanjanganInput instanceof JComboBox) {
            target.setPerpanjangan((String) ((JComboBox<?>) perpanjanganInput).getSelectedItem());
        }
        openDivisionDetail(currentActiveDivision);
    }

    private Member recreateMemberWithNewRole(Division div, Member target, String newName, String newRole) {
        String oldId = "ID-" + System.currentTimeMillis();
        int oldKas = target.getUangKas();
        String typeForFactory = newRole.contains("Staff") ? newRole : "Pejabat Struktural";
        String titleForFactory = newRole.contains("Staff") ? "" : newRole;
        Member newMember = MemberFactory.createMember(typeForFactory, titleForFactory, newName, oldId);
        newMember.bayarKas(oldKas);
        div.removeMember(target);
        div.addMember(newMember);
        return newMember;
    }

    // --- FITUR EDIT PROKER ---
    private void actionEditProker() {
        int row = prokerTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih proker dulu!"); return; }

        String pName = (String) prokerModel.getValueAt(row, 0);
        Division div = OrgManager.getInstance().getDivisionByName(currentActiveDivision);
        Proker target = null;
        for(Proker p : div.getProkerList()) { if(p.getNamaProker().equals(pName)) { target = p; break; } }

        if (target != null) {
            JTextField txtNama = new JTextField(target.getNamaProker());
            JTextField txtKetupel = new JTextField(target.getKetupel());
            JTextField txtWaketupel = new JTextField(target.getWaketupel());
            String[] stats = {"Rencana", "Berjalan", "Selesai"};
            JComboBox<String> cmbStatus = new JComboBox<>(stats);
            cmbStatus.setSelectedItem(target.getStatus());
            JTextArea txtDesc = new JTextArea(target.getDeskripsiDivisi(), 5, 20);

            Object[] message = {
                "Nama Proker:", txtNama, "Status:", cmbStatus, "Ketua Pelaksana:", txtKetupel,
                "Wakil Ketuplak:", txtWaketupel, "Deskripsi:", new JScrollPane(txtDesc)
            };

            if (JOptionPane.showConfirmDialog(this, message, "Edit Proker", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                target.setNamaProker(txtNama.getText());
                target.setStatus((String) cmbStatus.getSelectedItem());
                target.setKetupel(txtKetupel.getText());
                target.setWaketupel(txtWaketupel.getText());
                target.setDeskripsiDivisi(txtDesc.getText());
                openDivisionDetail(currentActiveDivision);
            }
        }
    }

    private void actionPayKas() {
        int row = memberTable.getSelectedRow();
        if (row == -1) return;
        String name = (String) memberModel.getValueAt(row, 0);
        Division div = OrgManager.getInstance().getDivisionByName(currentActiveDivision);
        Member target = findMemberRecursive(div, name);
        if (target == null) { // Cari di biro lain kalau delegasi
            target = findMemberRecursive(OrgManager.getInstance().getDivisionByName("Biro ADKES"), name);
            if (target == null) target = findMemberRecursive(OrgManager.getInstance().getDivisionByName("Biro KESRA"), name);
        }
        if (target != null) {
            String input = JOptionPane.showInputDialog(this, "Bayar Kas:", "10000");
            if (input != null) {
                try { target.bayarKas(Integer.parseInt(input)); openDivisionDetail(currentActiveDivision); } catch(NumberFormatException _) {
                    // Ignore invalid input; do not update kas if input is not a number
                }
            }
        }
    }

    private void actionAddProker() {
        String pName = inpProkerName.getText();
        if (pName.isEmpty()) return;
        
        Division div = OrgManager.getInstance().getDivisionByName(currentActiveDivision);
        if (div != null) {
            // PERBAIKAN DISINI: Menyesuaikan dengan Konstruktor Proker yang baru
            // Urutan: Nama, Deskripsi, Ketupel, Waketupel, Status, Progress, Divisi
            Proker newP = new Proker(
                pName,       // Nama
                "-",         // Deskripsi (Default)
                "-",         // Ketupel (Default)
                "-",         // Waketupel (Default)
                "Rencana",   // Status (Default)
                0,           // Progress (Default)
                currentActiveDivision // Divisi Asal
            );
            
            div.addProker(newP);
            inpProkerName.setText("");
            openDivisionDetail(currentActiveDivision);
        }
    }

    private Member findMemberRecursive(OrgComponent comp, String targetName) {
        return switch (comp) {
            case Member m -> m.getName().equals(targetName) ? m : null;
            case Division d -> {
                Member found = null;
                for (OrgComponent c : d.getMembers()) {
                    found = findMemberRecursive(c, targetName);
                    if (found != null) break;
                }
                yield found;
            }
            default -> null;
        };
    }

    // ================== FACTORIES ==================
    private JPanel createLeaderCard(String name, String role) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1), 
            new EmptyBorder(10, 15, 10, 15)));
        JLabel n = new JLabel(name); n.setFont(new Font("Segoe UI", Font.BOLD, 14)); n.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel r = new JLabel(role); r.setFont(new Font("Segoe UI", Font.PLAIN, 12)); r.setForeground(Color.GRAY); r.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(n); p.add(r); return p;
    }

    private JPanel createDivisionCard(String divName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1), 
            new EmptyBorder(15, 15, 15, 15)));
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
            @Override
            public void mouseEntered(MouseEvent e) { p.setBackground(new Color(235, 245, 251)); }
            @Override
            public void mouseExited(MouseEvent e) { p.setBackground(Color.WHITE); }
            @Override
            public void mouseClicked(MouseEvent e) { openDivisionDetail(divName); }
        });
        return p;
    }

    private String getIconFilename(String divName) { return divName.toLowerCase().replace(".", "").replace(" ", "_") + ".png"; }
    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            URL imgURL = getClass().getResource("/images/" + filename);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                return new ImageIcon(originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
            return null;
        } catch (Exception _) { return null; }
    }
}