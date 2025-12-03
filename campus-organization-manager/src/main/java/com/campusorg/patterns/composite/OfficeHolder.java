package com.campusorg.patterns.composite;

public class OfficeHolder extends Member {
    private String jobTitle; // Contoh: "Ketua Himpunan", "Kepala MSDH", "Ketua Departemen"

    public OfficeHolder(String name, String id, String jobTitle) {
        super(name, id);
        this.jobTitle = jobTitle;
    }

    @Override
    public void showDetails() {
        System.out.println(jobTitle + ": " + name);
    }

    @Override
    public String getRole() { return jobTitle; }
}