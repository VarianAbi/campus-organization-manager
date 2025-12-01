package com.campusorg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.campusorg.models.Division;
import com.campusorg.models.Member;
import com.campusorg.models.OrgComponent;
import com.campusorg.patterns.MemberFactory;
import com.campusorg.services.OrgManager;

public class AllMembersPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private TableRowSorter<DefaultTableModel> sorter;

    // Constants untuk Input
    private final String[] ROLES_BPH = { "Ketua Himpunan", "Wakil Ketua", "Sekretaris", "Bendahara" };
    private final String[] ROLES_STD = { "Staff Muda", "Staff Ahli", "Ketua Departemen" };

    public AllMembersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(244, 246, 247));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // 1. Header & Filter Bar
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel title = new JLabel("Data Seluruh Anggota HIMAKOM");
        title.setFont(new Font("Inria Sans", Font.BOLD, 22));

        // Panel Filter
        JPanel filterPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPnl.setBackground(Color.WHITE);
        filterPnl.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

        searchField = new JTextField(15);
        searchField.setFont(new Font("Poppins", Font.PLAIN, 13));
        List<String> divs = new ArrayList<>();
        divs.add("Semua Divisi");
        for (String s : OrgManager.getInstance().getDivisionNames())
            divs.add(s);
        filterCombo = new JComboBox<>(divs.toArray(new String[0]));
        filterCombo.setFont(new Font("Poppins", Font.PLAIN, 13));

        JButton btnFilter = new JButton("Terapkan Filter");
        btnFilter.setBackground(new Color(52, 152, 219));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFont(new Font("Poppins", Font.BOLD, 12));
        btnFilter.addActionListener(e -> doFilter());

        filterPnl.add(new JLabel("🔍 Cari:"));
        filterPnl.add(searchField);
        filterPnl.add(new JLabel("  📂 Divisi:"));
        filterPnl.add(filterCombo);
        filterPnl.add(btnFilter);

        // --- TOMBOL TAMBAH ANGGOTA (POPUP) ---
        JButton btnAdd = new JButton("➕ Tambah Anggota");
        btnAdd.setBackground(new Color(46, 204, 113)); // Hijau
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Poppins", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> showInputMemberDialog()); // Panggil Popup

        // Layout Header
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setFont(new Font("Poppins", Font.PLAIN, 12));
        btnPanel.add(btnAdd);
        actionPanel.add(filterPnl, BorderLayout.WEST);
        actionPanel.add(btnPanel, BorderLayout.EAST);

        topContainer.add(title, BorderLayout.NORTH);
        topContainer.add(actionPanel, BorderLayout.CENTER);

        // 2. Table anggota
        String[] cols = { "Nama", "Divisi", "Jabatan" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Poppins", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Inria Sans", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(33, 47, 60));
        table.getTableHeader().setForeground(Color.WHITE);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        add(topContainer, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Refresh data anggota
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);
        traverse(OrgManager.getInstance().getRoot());
    }

    private void traverse(OrgComponent comp) {
        if (comp instanceof Division) {
            Division d = (Division) comp;
            for (OrgComponent c : d.getMembers()) {
                if (c instanceof Member) {
                    Member m = (Member) c;
                    model.addRow(new Object[] { m.getName(), d.getName(), m.getRole() });
                } else
                    traverse(c);
            }
        }
    }

    private void doFilter() {
        String text = searchField.getText();
        String div = (String) filterCombo.getSelectedItem();
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!text.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + text, 0));
        if (!div.equals("Semua Divisi"))
            filters.add(RowFilter.regexFilter("(?i)" + div, 1));
        sorter.setRowFilter(RowFilter.andFilter(filters));
    }

    // ==================== TAMBAH ANGGOTA DIALOG ====================
    private void showInputMemberDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Input Anggota Baru", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setFont(new Font("Poppins", Font.PLAIN, 12));

        JTextField inpName = new JTextField();
        inpName.setFont(new Font("Poppins", Font.PLAIN, 13));
        JComboBox<String> inpDiv = new JComboBox<>(OrgManager.getInstance().getDivisionNames());
        inpDiv.setFont(new Font("Poppins", Font.PLAIN, 13));
        JComboBox<String> inpRole = new JComboBox<>();

        // Logic Dropdown Dinamis
        inpDiv.addActionListener(e -> {
            String selected = (String) inpDiv.getSelectedItem();
            inpRole.removeAllItems();
            String[] roles = "BPH Inti".equals(selected) ? ROLES_BPH : ROLES_STD;
            for (String r : roles)
                inpRole.addItem(r);
        });
        // Init dropdown pertama kali
        if (inpDiv.getItemCount() > 0)
            inpDiv.setSelectedIndex(0);

        JButton btnSave = new JButton("SIMPAN");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Poppins", Font.BOLD, 12));

        btnSave.addActionListener(e -> {
            String nm = inpName.getText();
            String dv = (String) inpDiv.getSelectedItem();
            String role = (String) inpRole.getSelectedItem();

            if (nm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nama Kosong!");
                return;
            }

            String type = role.contains("Staff") ? role : "Pejabat Struktural";
            String title = role.contains("Staff") ? "" : role;

            Member m = MemberFactory.createMember(type, title, nm, "ID-" + System.currentTimeMillis());
            OrgManager.getInstance().registerMember(OrgManager.getInstance().getDivisionByName(dv), m);

            JOptionPane.showMessageDialog(dialog, "Berhasil Input: " + nm);
            dialog.dispose(); // Tutup Dialog
            refreshData(); // Refresh Tabel Otomatis
        });

        formPanel.add(new JLabel("Nama Lengkap:"));
        formPanel.add(inpName);
        formPanel.add(new JLabel("Divisi:"));
        formPanel.add(inpDiv);
        formPanel.add(new JLabel("Jabatan:"));
        formPanel.add(inpRole);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(btnSave);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}