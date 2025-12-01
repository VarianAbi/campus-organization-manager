package com.campusorg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.campusorg.models.Division;
import com.campusorg.models.Member;
import com.campusorg.models.OrgComponent;
import com.campusorg.services.OrgManager;

public class AllMembersPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private TableRowSorter<DefaultTableModel> sorter;

    public AllMembersPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(244, 246, 247));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Filter Bar
        JPanel filterPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPnl.setBackground(Color.WHITE);
        filterPnl.setBorder(new LineBorder(Color.LIGHT_GRAY));

        searchField = new JTextField(15);
        List<String> divs = new ArrayList<>();
        divs.add("Semua Divisi");
        for(String s : OrgManager.getInstance().getDivisionNames()) divs.add(s);
        filterCombo = new JComboBox<>(divs.toArray(new String[0]));
        
        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> doFilter());

        filterPnl.add(new JLabel("Cari:"));
        filterPnl.add(searchField);
        filterPnl.add(new JLabel("Divisi:"));
        filterPnl.add(filterCombo);
        filterPnl.add(btnFilter);

        // Table
        String[] cols = {"Nama", "Divisi", "Jabatan"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        add(new JLabel("<h2>Data Anggota</h2>"), BorderLayout.NORTH);
        add(filterPnl, BorderLayout.NORTH); // Note: Layout perlu diperbaiki nanti
        add(new JScrollPane(table), BorderLayout.CENTER);
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
                    model.addRow(new Object[]{m.getName(), d.getName(), m.getRole()});
                } else traverse(c);
            }
        }
    }

    private void doFilter() {
        String text = searchField.getText();
        String div = (String) filterCombo.getSelectedItem();
        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        if(!text.isEmpty()) filters.add(RowFilter.regexFilter("(?i)"+text, 0));
        if(!div.equals("Semua Divisi")) filters.add(RowFilter.regexFilter("(?i)"+div, 1));
        sorter.setRowFilter(RowFilter.andFilter(filters));
    }
}