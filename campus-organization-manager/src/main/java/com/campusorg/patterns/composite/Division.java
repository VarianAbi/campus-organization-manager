package com.campusorg.patterns.composite;

import java.util.ArrayList;
import java.util.List;

import com.campusorg.models.Proker;

public class Division implements OrgComponent {
    private String divisionName;
    private List<OrgComponent> members = new ArrayList<>();
    
    // --- TAMBAHAN BARU (PROKER) ---
    private List<Proker> prokerList = new ArrayList<>();
    // ------------------------------

    public Division(String divisionName) {
        this.divisionName = divisionName;
    }

    public void addMember(OrgComponent comp) {
        members.add(comp);
    }

    public void removeMember(OrgComponent comp) {
        members.remove(comp);
    }

    public List<OrgComponent> getMembers() {
        return members;
    }

    // --- METHOD BARU UNTUK PROKER ---
    public void addProker(Proker p) {
        prokerList.add(p);
    }

    public List<Proker> getProkerList() {
        return prokerList;
    }
    // --------------------------------

    @Override
    public void showDetails() {
        System.out.println("Divisi: " + divisionName);
        for (OrgComponent comp : members) {
            comp.showDetails();
        }
    }

    @Override
    public String getName() { return divisionName; }

    @Override
    public String getRole() { return "Divisi"; }
}