package com.campusorg.gui;

import com.campusorg.gui.panels.*; 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    
    // PANEL MODULAR
    private HomePanel homePanel; // <-- SEKARANG PAKAI CLASS SENDIRI
    private AllMembersPanel allMembersPanel;
    private MemberInputPanel inputPanel;
    private ProkerPanel prokerPanel;
    
    private final Color COL_SIDEBAR = new Color(33, 47, 60);
    private final Color COL_ACTIVE  = new Color(52, 73, 94);
    private final Color COL_BG      = new Color(244, 246, 247);

    public MainFrame() {
        setTitle("HIMAKOM App (Final Version)");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COL_BG);

        // INIT SEMUA PANEL
        homePanel = new HomePanel(); // <-- Ini memuat Dashboard + Grid + Detail Divisi
        allMembersPanel = new AllMembersPanel();
        inputPanel = new MemberInputPanel();
        prokerPanel = new ProkerPanel();
        
        contentPanel.add(homePanel, "HOME");
        contentPanel.add(allMembersPanel, "DATA");
        contentPanel.add(inputPanel, "INPUT");
        contentPanel.add(prokerPanel, "PROKER");

        initSidebar();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(230, 0));
        sidebarPanel.setBackground(COL_SIDEBAR);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

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

        addSidebarBtn("🏠  Home Dashboard", "HOME");
        addSidebarBtn("👥  Data Anggota", "DATA");
        addSidebarBtn("📝  Input Anggota", "INPUT");
        addSidebarBtn("📅  Program Kerja", "PROKER");

        sidebarPanel.add(Box.createVerticalGlue());
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
            if (targetCard.equals("HOME")) homePanel.refreshData(); // Refresh Home saat diklik
            if (targetCard.equals("DATA")) allMembersPanel.refreshData();
            if (targetCard.equals("PROKER")) prokerPanel.refreshData();
            
            cardLayout.show(contentPanel, targetCard);
        });

        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(10));
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