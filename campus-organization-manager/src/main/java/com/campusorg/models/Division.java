package com.campusorg.models;

import java.util.ArrayList;
import java.util.List;

public class Division implements OrgComponent {
    private String divisionName;
    private List<OrgComponent> members = new ArrayList<>();

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