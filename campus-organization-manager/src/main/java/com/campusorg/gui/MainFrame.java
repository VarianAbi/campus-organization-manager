package com.campusorg.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.campusorg.gui.panels.AllMembersPanel;
import com.campusorg.gui.panels.HomePanel;
import com.campusorg.gui.panels.ProkerPanel;
import com.campusorg.utils.Constants;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private JPanel sidebarPanel;

    // PANEL MODULAR (InputPanel SUDAH DIHAPUS)
    private final HomePanel homePanel;
    private final AllMembersPanel allMembersPanel;
    private final ProkerPanel prokerPanel;

    private static final Color COL_SIDEBAR = new Color(33, 47, 60);
    private static final Color COL_ACTIVE = new Color(52, 73, 94);
    private static final Color COL_BG = new Color(244, 246, 247);

    public MainFrame() {
        setTitle("HIMAKOM App (Final Version)");
        setSize(1280, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Setup Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COL_BG);

        // 2. INIT PANELS (Tidak perlu navigasi lambda lagi)
        homePanel = new HomePanel();
        allMembersPanel = new AllMembersPanel(); // Tombol input sudah ada di dalam panel ini
        prokerPanel = new ProkerPanel();

        // 3. Add Panels
        contentPanel.add(homePanel, Constants.CARD_HOME);
        contentPanel.add(allMembersPanel, Constants.CARD_MEMBERS);
        contentPanel.add(prokerPanel, Constants.CARD_PROKER);

        // 4. Sidebar
        initSidebar();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(230, 0));
        sidebarPanel.setBackground(COL_SIDEBAR);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // --- Logo Area ---
        JPanel logoPnl = new JPanel();
        logoPnl.setLayout(new BoxLayout(logoPnl, BoxLayout.Y_AXIS));
        logoPnl.setBackground(COL_SIDEBAR);
        logoPnl.setBorder(new EmptyBorder(30, 10, 20, 10));

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon logoIcon = loadIcon("logo_himakom.png", 80, 80);
        if (logoIcon != null)
            imageLabel.setIcon(logoIcon);
        else
            imageLabel.setText("<html><h2 style='color:white;'>[LOGO]</h2></html>");

        JLabel title = new JLabel("HIMAKOM APP");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Inria Sans", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPnl.add(imageLabel);
        logoPnl.add(Box.createVerticalStrut(15));
        logoPnl.add(title);
        sidebarPanel.add(logoPnl);

        // --- MENU BUTTONS ---
        addSidebarBtn("🏠  Home Dashboard", Constants.CARD_HOME);
        addSidebarBtn("👥  Data Anggota", Constants.CARD_MEMBERS);
        addSidebarBtn("📅  Program Kerja", Constants.CARD_PROKER);

        sidebarPanel.add(Box.createVerticalGlue());

        JLabel credit = new JLabel("© 2025 Himakom App", SwingConstants.CENTER);
        credit.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 11));
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
        btn.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COL_ACTIVE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COL_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            if (targetCard.equals(Constants.CARD_HOME))
                homePanel.refreshData();
            if (targetCard.equals(Constants.CARD_MEMBERS))
                allMembersPanel.refreshData();
            if (targetCard.equals(Constants.CARD_PROKER))
                prokerPanel.refreshData();

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
        } catch (Exception _) {
            return null;
        }
    }
}
