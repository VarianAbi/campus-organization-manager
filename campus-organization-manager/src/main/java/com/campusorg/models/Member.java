package com.campusorg.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Member implements OrgComponent {
    protected String name;
    protected String id;
    protected List<String> notifications = new ArrayList<>();
    
    protected int uangKas = 0; 
    
    // --- FIELD BARU ---
    protected String perpanjangan = "-"; // Default strip (bukan perpanjangan)
    // ------------------

    public Member(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() { return name; }
    
    public void setName(String name) { this.name = name; } // Setter Nama

    public void update(String message) {
        String notif = "[" + this.name + " - " + getRole() + "] Menerima Pesan: " + message;
        notifications.add(notif);
        System.out.println(notif); 
    }

    public List<String> getNotifications() { return notifications; }

    public int getUangKas() { return uangKas; }
    public void bayarKas(int jumlah) { this.uangKas += jumlah; }

    // --- GETTER & SETTER PERPANJANGAN ---
    public String getPerpanjangan() { return perpanjangan; }
    public void setPerpanjangan(String perpanjangan) { this.perpanjangan = perpanjangan; }
}