package com.campusorg.gui.panels;

import com.campusorg.models.Member;
import com.campusorg.patterns.MemberFactory;
import com.campusorg.services.OrgManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MemberInputPanel extends JPanel {
    private JTextField inpName;
    private JComboBox<String> inpDiv;
    private JComboBox<String> inpRole;

    private final String[] ROLES_BPH = { "Ketua Himpunan", "Wakil Ketua", "Sekretaris", "Bendahara" };
    private final String[] ROLES_STD = { "Staff Muda", "Staff Ahli", "Ketua Departemen" };

    public MemberInputPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(244, 246, 247));
        setBorder(new EmptyBorder(20, 50, 20, 50));

        JPanel formCard = new JPanel(new GridLayout(0, 1, 10, 5));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(20, 20, 20, 20)));

        inpName = new JTextField();
        inpDiv = new JComboBox<>(OrgManager.getInstance().getDivisionNames());
        inpRole = new JComboBox<>();

        inpDiv.addActionListener(e -> updateRoleDropdown());
        updateRoleDropdown(); // Init

        JButton btnSave = new JButton("SIMPAN ANGGOTA");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> actionSave());
        formCard.add(new JLabel("Nama Lengkap"));
        formCard.add(inpName);
        formCard.add(new JLabel("Divisi"));
        formCard.add(inpDiv);
        formCard.add(new JLabel("Posisi"));
        formCard.add(inpRole);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(btnSave);

        add(new JLabel("<html><h2>Form Input Anggota</h2></html>"), BorderLayout.NORTH);
        add(formCard, BorderLayout.CENTER);
    }

    private void updateRoleDropdown() {
        String selected = (String) inpDiv.getSelectedItem();
        inpRole.removeAllItems();
        String[] roles = "BPH Inti".equals(selected) ? ROLES_BPH : ROLES_STD;
        for (String r : roles)
            inpRole.addItem(r);
    }

    private void actionSave() {
        String nm = inpName.getText();
        String dv = (String) inpDiv.getSelectedItem();
        String role = (String) inpRole.getSelectedItem();

        if (nm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kosong!");
            return;
        }

        String type = role.contains("Staff") ? role : "Pejabat Struktural";
        String title = role.contains("Staff") ? "" : role;

        Member m = MemberFactory.createMember(type, title, nm, "ID-" + System.currentTimeMillis());
        OrgManager.getInstance().registerMember(OrgManager.getInstance().getDivisionByName(dv), m);

        JOptionPane.showMessageDialog(this, "Berhasil Input: " + nm);
        inpName.setText("");
    }
}