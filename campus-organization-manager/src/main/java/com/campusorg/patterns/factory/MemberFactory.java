package com.campusorg.patterns.factory;

import com.campusorg.models.*;
import com.campusorg.patterns.composite.Member;
import com.campusorg.patterns.composite.OfficeHolder;
import com.campusorg.patterns.composite.StaffAhli;
import com.campusorg.patterns.composite.StaffMuda;

public class MemberFactory {
    public static Member createMember(String type, String specificTitle, String name, String id) {
        // type diambil dari ComboBox GUI: 
        // "Staff Muda", "Staff Ahli", atau "Pejabat Struktural"
        
        if (type.equals("Staff Muda")) {
            return new StaffMuda(name, id);
        } 
        else if (type.equals("Staff Ahli")) {
            return new StaffAhli(name, id);
        } 
        else if (type.equals("Pejabat Struktural")) {
            // Jabatan khusus (Kahim, Kadep, dll)
            return new OfficeHolder(name, id, specificTitle);
        } 
        else {
            // Default fallback (Safety)
            return new StaffMuda(name, id);
        }
    }
}