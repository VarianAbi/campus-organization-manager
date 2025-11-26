package com.campusorg.models;

public class StaffAhli extends Member {
    public StaffAhli(String name, String id) {
        super(name, id);
    }

    @Override
    public void showDetails() {
        System.out.println("Staff Ahli: " + name);
    }

    @Override
    public String getRole() { return "Staff Ahli"; }
}