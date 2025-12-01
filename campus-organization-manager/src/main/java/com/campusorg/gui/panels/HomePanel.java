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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.campusorg.models.Division;
import com.campusorg.models.Member;
import com.campusorg.models.OrgComponent;
import com.campusorg.models.Proker;
import com.campusorg.services.OrgManager;

public class HomePanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    // --- DASHBOARD COMPONENTS ---
    private JPanel dashboardScrollContent;
    private JPanel leadersContainer;
    private JPanel divisionsGrid;

    // --- DETAIL VIEW COMPONENTS ---
    private JPanel detailDivPanel;
    private JLabel detailTitleLabel;
    private JTable memberTable;
    private DefaultTableModel memberModel;
    private JTable prokerTable;
    private DefaultTableModel prokerModel;
    
    // State
    private String currentActiveDivision;
    private final Color COL_SIDEBAR = new Color(33, 47, 60);
    private final Color COL_BG = new Color(244, 246, 247);
    private final Color COL_ACCENT = new Color(41, 128, 185);

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(COL_BG);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        
        // 1. Init Dashboard UI
        initDashboardUI();
        
        // 2. Init Detail UI
        initDetailUI();

        mainContainer.add(new JScrollPane(dashboardScrollContent), "DASHBOARD");
        mainContainer.add(detailDivPanel, "DETAIL");

        add(mainContainer, BorderLayout.CENTER);
        
        // Load Data Awal
        refreshData();
    }

    // ================== DASHBOARD VIEW ==================
    private void initDashboardUI() {
        dashboardScrollContent = new JPanel();
        dashboardScrollContent.setLayout(new BoxLayout(dashboardScrollContent, BoxLayout.Y_AXIS));
        dashboardScrollContent.setBackground(COL_BG);
        dashboardScrollContent.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblLead = new JLabel("Pimpinan Teras (BPH Inti)");
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
        splitPane.setResizeWeight(0.6);
        
        // Kiri: Anggota
        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Daftar Anggota"));
        memberPanel.setBackground(Color.WHITE);
        String[] mCols = {"Nama Anggota", "Jabatan"};
        memberModel = new DefaultTableModel(mCols, 0);
        memberTable = new JTable(memberModel);
        memberTable.setRowHeight(25);
        memberPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        // Kanan: Proker
        JPanel prokerPanel = new JPanel(new BorderLayout());
        prokerPanel.setBorder(BorderFactory.createTitledBorder("Program Kerja"));
        prokerPanel.setBackground(Color.WHITE);
        String[] pCols = {"Nama Proker", "Status"};
        prokerModel = new DefaultTableModel(pCols, 0);
        prokerTable = new JTable(prokerModel);
        prokerTable.setRowHeight(25);
        prokerPanel.add(new JScrollPane(prokerTable), BorderLayout.CENTER);

        splitPane.setLeftComponent(memberPanel);
        splitPane.setRightComponent(prokerPanel);
        detailDivPanel.add(splitPane, BorderLayout.CENTER);
    }

    // ================== LOGIC ==================
    public void refreshData() {
        // 1. Refresh BPH
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

        // 2. Refresh Grid
        divisionsGrid.removeAll();
        String[] allDivs = OrgManager.getInstance().getDivisionNames();
        for (String divName : allDivs) {
            if (divName.equals("BPH Inti") || divName.equals("Divisi Kaderisasi") || divName.equals("Divisi Apresiasi & Evaluasi")) continue; 
            divisionsGrid.add(createDivisionCard(divName));
        }
        
        leadersContainer.revalidate(); leadersContainer.repaint();
        divisionsGrid.revalidate(); divisionsGrid.repaint();
    }

    private void openDivisionDetail(String divName) {
        currentActiveDivision = divName;
        detailTitleLabel.setText(divName);
        memberModel.setRowCount(0);
        prokerModel.setRowCount(0);

        Division div = OrgManager.getInstance().getDivisionByName(divName);
        
        // Load Members
        if (divName.equals("MSDH") && div != null) {
            loadRecursiveMember(div, "");
        } else if (div != null) {
            for (OrgComponent c : div.getMembers()) {
                if (c instanceof Member) {
                    Member m = (Member) c;
                    memberModel.addRow(new Object[]{m.getName(), m.getRole()});
                }
            }
        }

        // Load Prokers
        if (div != null) {
            for (Proker p : div.getProkerList()) {
                prokerModel.addRow(new Object[]{p.getNamaProker(), p.getStatus()});
            }
        }
        
        cardLayout.show(mainContainer, "DETAIL");
    }

    private void loadRecursiveMember(Division d, String suffix) {
        for (OrgComponent c : d.getMembers()) {
            if (c instanceof Member) {
                Member m = (Member) c;
                memberModel.addRow(new Object[]{m.getName(), m.getRole() + suffix});
            } else if (c instanceof Division) {
                loadRecursiveMember((Division) c, " [" + c.getName() + "]");
            }
        }
    }

    // ================== FACTORIES ==================
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