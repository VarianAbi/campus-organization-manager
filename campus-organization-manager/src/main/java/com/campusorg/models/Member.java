package com.campusorg.models;

import java.util.ArrayList;
import java.util.List;

// Abstract Class utama
public abstract class Member implements OrgComponent {
    protected String name;
    protected String id;
    protected List<String> notifications = new ArrayList<>();

    public Member(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() { return name; }

    public void update(String message) {
        String notif = "[" + this.name + " - " + getRole() + "] Menerima Pesan: " + message;
        notifications.add(notif);
        System.out.println(notif); 
    }

    public List<String> getNotifications() {
        return notifications;
    }
}