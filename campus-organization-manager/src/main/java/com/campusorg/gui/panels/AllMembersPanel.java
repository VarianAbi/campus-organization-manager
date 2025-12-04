package com.campusorg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.campusorg.patterns.composite.Division;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OrgComponent;
import com.campusorg.patterns.factory.MemberFactory;
import com.campusorg.patterns.singleton.OrgManager;
import com.campusorg.utils.Constants;

public class AllMembersPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private final transient TableRowSorter<DefaultTableModel> sorter;

    // Constants untuk Input
    private static final String[] ROLES_BPH = { "Ketua Himpunan", "Wakil Ketua", "Sekretaris", "Bendahara" };
    private static final String[] ROLES_STD = { "Staff Muda", "Staff Ahli", "Ketua Departemen" };

    @SuppressWarnings("CollectionsToArray")
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
        searchField.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 13));
        List<String> divs = new ArrayList<>();
        divs.add("Semua Divisi");
        for (String s : OrgManager.getInstance().getDivisionNames())
            Collections.addAll(divs, s);
        filterCombo = new JComboBox<>(divs.toArray(new String[divs.size()]));
        filterCombo.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 13));
        filterCombo.setForeground(Color.blue);

        JButton btnFilter = new JButton("Terapkan Filter");
        btnFilter.setBackground(new Color(52, 152, 219));
        btnFilter.setForeground(Color.black);
        btnFilter.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 12));
        btnFilter.addActionListener(e -> doFilter());

        filterPnl.add(new JLabel("🔍 Cari:"));
        filterPnl.add(searchField);
        filterPnl.add(new JLabel("  📂 Divisi:"));
        filterPnl.add(filterCombo);
        filterPnl.add(btnFilter);

        // --- TOMBOL TAMBAH & HAPUS ANGGOTA ---
        JButton btnAdd = new JButton("➕ Tambah Anggota");
        btnAdd.setBackground(new Color(46, 204, 113)); // Hijau
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 12));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> showInputMemberDialog()); // Panggil Popup

        JButton btnDelete = new JButton("🗑️ Hapus Anggota");
        btnDelete.setBackground(new Color(231, 76, 60)); // Merah
        btnDelete.setForeground(Color.black);
        btnDelete.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 12));
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteSelectedMember());

        // Layout Header
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 12));
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
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
        table.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Inria Sans", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(33, 47, 60));
        table.getTableHeader().setForeground(Color.BLACK);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        add(topContainer, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Refresh data anggota
        refreshData();
    }

    public final void refreshData() {
        model.setRowCount(0);
        traverse(OrgManager.getInstance().getRoot());
    }

    private void traverse(OrgComponent comp) {
        if (comp instanceof Division d) {
            for (OrgComponent c : d.getMembers()) {
                if (c instanceof Member m) {
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
        if (!div.equals(Constants.STATUS_SEMUA))
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
        formPanel.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 12));

        JTextField inpName = new JTextField();
        inpName.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 13));
        JComboBox<String> inpDiv = new JComboBox<>(OrgManager.getInstance().getDivisionNames());
        inpDiv.setFont(new Font(Constants.FONT_POPPINS, Font.PLAIN, 13));
        JComboBox<String> inpRole = new JComboBox<>();

        // Logic Dropdown Dinamis
        inpDiv.addActionListener(e -> {
            String selected = (String) inpDiv.getSelectedItem();
            inpRole.removeAllItems();
            String[] roles = Constants.DIV_BPH_INTI.equals(selected) ? ROLES_BPH : ROLES_STD;
            for (String r : roles)
                inpRole.addItem(r);
        });
        // Init dropdown pertama kali
        if (inpDiv.getItemCount() > 0)
            inpDiv.setSelectedIndex(0);

        JButton btnSave = new JButton("SIMPAN");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.black);
        btnSave.setFont(new Font(Constants.FONT_POPPINS, Font.BOLD, 12));

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

    // --- FITUR HAPUS ANGGOTA ---
    private void deleteSelectedMember() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih anggota yang ingin dihapus!", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Konversi index jika ada sorting
        int modelRow = table.convertRowIndexToModel(row);
        String name = (String) model.getValueAt(modelRow, 0);
        String divisi = (String) model.getValueAt(modelRow, 1);
        String jabatan = (String) model.getValueAt(modelRow, 2);

        // Konfirmasi hapus
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin menghapus anggota:\n" + name + "\nDivisi: " + divisi + "\nJabatan: " + jabatan,
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Division div = OrgManager.getInstance().getDivisionByName(divisi);
            if (div != null) {
                Member target = findMemberInDivision(div, name);
                if (target != null) {
                    div.removeMember(target);
                    JOptionPane.showMessageDialog(this, "Anggota berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshData(); // Refresh tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Anggota tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Helper method untuk mencari member di divisi
    private Member findMemberInDivision(Division div, String targetName) {
        for (OrgComponent comp : div.getMembers()) {
            switch (comp) {
                case Member m when m.getName().equals(targetName) -> {
                    return m;
                }
                case Division subDiv -> {
                    Member found = findMemberInDivision(subDiv, targetName);
                    if (found != null) return found;
                }
                default -> { /* non-member components ignored */ }
            }
        }
        return null;
    }

    public TableRowSorter<DefaultTableModel> getSorter() {
        return sorter;
    }
}