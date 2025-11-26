package com.campusorg.models;

public class StaffMuda extends Member {
    public StaffMuda(String name, String id) {
        super(name, id);
    }

    @Override
    public void showDetails() {
        System.out.println("Staff Muda: " + name);
    }

    @Override
    public String getRole() { return "Staff Muda"; }
}