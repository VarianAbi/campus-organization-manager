package com.campusorg.patterns.factory;

import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OfficeHolder;
import com.campusorg.patterns.composite.StaffAhli;
import com.campusorg.patterns.composite.StaffMuda;

public class MemberFactory {
    private MemberFactory() {}

    public static Member createMember(String type, String specificTitle, String name, String id) {
        // type diambil dari ComboBox GUI: 
        // "Staff Muda", "Staff Ahli", atau "Pejabat Struktural"
        
        return switch (type) {
            case "Staff Muda" -> new StaffMuda(name, id);
            case "Staff Ahli" -> new StaffAhli(name, id);
            case "Pejabat Struktural" -> new OfficeHolder(name, id, specificTitle);
            default -> new StaffMuda(name, id);
        };
    }
}